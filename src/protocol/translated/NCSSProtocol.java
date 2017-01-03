package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.QueryBuilder;

/**
 * Adapter class for the NetCdf Subset Service (NCSS) 
 */
public class NCSSProtocol extends TranslatedProtocol
{
    
    public NCSSProtocol(CollectiveProtocol query)
    {
	super(query);
    }
    

    @Override
    public String getProtocolName() {
	return "NCSS";
    }
    

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getNcssUrlName();
    }
    
    @Override
    protected String getFileNameExtension()
    {
	return null;
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
	
	// create the bounding box
	query.add("south", latRange.getStartCoordinate());
	query.add("north", latRange.getEndCoordinate());
	query.add("west", lonRange.getStartCoordinate());
	query.add("east", lonRange.getEndCoordinate());
	
	// add all variables
	if (protocol.hasVariablesDefined()) {
	    query.add("var", protocol.getVariables());
	}
	
	// take the minimum stride
	int horizontalStride = Math.min(latRange.getStride(), lonRange.getStride());
	query.add("horizStride", horizontalStride);
	
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
	    query.add("time_start", timeRange.getStartTime());
	    query.add("time_end", timeRange.getEndTime());
	    query.add("time_stride", timeRange.getStride());
	}
	
	// add the output format
	query.add("accept", ConfigReader.getInstace().getNcssOutputFormat());
    }

}
