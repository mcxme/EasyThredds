package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.DimensionArray;
import protocol.translated.util.QueryBuilder;
import protocol.translated.util.VariableReader;
import reader.IReader;
import reader.NCSSReader;

/**
 * Adapter class for the NetCdf Subset Service (NCSS) 
 */
public class NCSSProtocol extends TranslatedProtocol
{
    public static final boolean MAKE_OUTPUT_CF_COMPLIANT = true;
    
    public NCSSProtocol(CollectiveProtocol query)
    {
	super(query);
    }
    
    @Override
    public String getProtocolName() {
	return "NCSS";
    }
    
    @Override
    protected String getNetCdfName() {
	throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getNcssUrlName();
    }
    
    @Override
    protected IReader readerFactory()
    {
	return new NCSSReader();
    }
    
    @Override
    protected String getFileNameExtension()
    {
	return null;
    }

    @Override
    public boolean canTranslate(CollectiveProtocol protocol)
    {
	SpatialRange latRange = protocol.getLatitudeRange();
	SpatialRange lonRange = protocol.getLongitudeRange();

	// NCSS cannot differentiate longitude and latitude strides
	if (protocol.hasLongitudeRange() && protocol.hasLatitudeRange()
		&& (latRange.getStride() != lonRange.getStride()))
	{
	    return false;
	}

	// NCSS cannot specify altitude ranges but is only capable of traversing
	// the entire range (stride is possible)
	if (protocol.hasHightRange())
	{
	    VariableReader reader = loadDimensionData(protocol);
	    if (reader.isFullAltitudeRange(getDatasetBaseUrl(), protocol.getHightRange()))
	    {
		return false;
	    }
	}
	
	return true;
    }
    
    /**
     * Forms the query based on the Netcdf Subset Service Reference:
     * {@link http://www.unidata.ucar.edu/software/thredds/current/tds/reference/NetcdfSubsetServiceReference.html}
     */
    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	SpatialRange latRange = protocol.getLatitudeRange();
	SpatialRange lonRange = protocol.getLongitudeRange();
	
	// add all variables
	if (protocol.hasVariablesDefined()) {
	    query.add("var", protocol.getVariables());
	}
	
	if (latRange.isPoint() && lonRange.isPoint()) {
	    // create the request point
	    query.add("longitude", lonRange.getStartCoordinate());
	    query.add("latitude", latRange.getStartCoordinate());
	} else {
	    // create the bounding box
	    query.add("south", latRange.getStartCoordinate());
	    query.add("north", latRange.getEndCoordinate());
	    query.add("west", lonRange.getStartCoordinate());
	    query.add("east", lonRange.getEndCoordinate());
	    
	    // take the minimum spatial stride
	    int horizontalStride = Math.min(latRange.getStride(), lonRange.getStride());
	    query.add("horizStride", horizontalStride);
	}
	
	
	if (protocol.hasHightRange()) {
	    NumericRange hightRange = protocol.getHightRange();
	    if (hightRange.isSingleValue()) {
		// The range is a single value -> single level
		query.add("vertCoord", hightRange.getStart());
	    } else {
		// The range has multiple values -> vertical stride
		query.add("vertStride", hightRange.getStride());
	    }
	}
	
	// add the time specification if defined
	if (protocol.hasTimeRangeDefined()) {
	    TimeRange timeRange = protocol.getTimeRange();
	    if (timeRange.isPoint()) {
		query.add("time", timeRange.getStartTime());
	    } else {
		query.add("time_start", timeRange.getStartTime());
		query.add("time_end", timeRange.getEndTime());
		query.add("time_stride", timeRange.getStride());
	    }
	}
	
	// add the output format
	query.add("accept", ConfigReader.getInstace().getNcssOutputFormat());
	// enforce CF compliance
	query.add("addLatLon", MAKE_OUTPUT_CF_COMPLIANT);
    }
    
    private VariableReader loadDimensionData(CollectiveProtocol protocol)
    {
	VariableReader variableReader = VariableReader.getInstance();
	String datasetKey = getDataset();
	// need to fetch the dataset?
	if (!variableReader.hasDataset(datasetKey)) {
	    
	    // first request longitude, latitude and level in a single request
	    String requestedSpatialDims = "";
	    if (protocol.hasTimeRangeDefined())
		requestedSpatialDims += "time,";
	    if (protocol.hasLongitudeRange())
		requestedSpatialDims += "lon,";
	    if (protocol.hasLatitudeRange())
		requestedSpatialDims += "lat,";
	    if (protocol.hasHightRange())
		requestedSpatialDims += "lev,";
	    
	    requestedSpatialDims = requestedSpatialDims.substring(0, requestedSpatialDims.length() - 1);
	    IReader latReader = null;
	    IReader lonReader = null;
	    IReader lvlReader = null;
	    IReader timeReader = null;
	    if (!requestedSpatialDims.isEmpty()) {
    		IReader reader = readerFactory();
    		reader.setUri(getDatasetBaseUrl(), requestedSpatialDims, datasetKey + "-dims");
        	    if (protocol.hasLongitudeRange())
        		lonReader = reader;
        	    if (protocol.hasLatitudeRange())
        		latReader = reader;
        	    if (protocol.hasHightRange())
        		lvlReader = reader;
        	    if (protocol.hasTimeRangeDefined())
        		timeReader = reader;
	    }
	    
	    DimensionArray dims = new DimensionArray(latReader, lonReader, lvlReader, timeReader);
	    variableReader.addDataset(datasetKey, dims);
	}

	return variableReader;
    }

}
