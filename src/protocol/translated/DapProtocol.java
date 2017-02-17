package protocol.translated;

import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.reader.IReader;
import protocol.translated.util.DimensionArray;
import protocol.translated.util.QueryBuilder;
import protocol.translated.util.VariableReader;

public abstract class DapProtocol extends TranslatedProtocol
{
    public DapProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	String datasetKey = getDataset();
	VariableReader variableReader = getDimensionData();
	SpatialRange lonRange = protocol.getLongitudeRange();
	SpatialRange latRange = protocol.getLatitudeRange();
	NumericRange zRange = protocol.getHightRange();
	TimeRange timeRange = protocol.getTimeRange();
	
	// output format: a[time][level][lat][lon], b[][][][], ...
	for (String var : protocol.getVariables()) {
	    query.append(var);
	    if (protocol.hasTimeRangeDefined()) {
		NumericRange timeIndexRange = variableReader.getTimeIndexRange(datasetKey, timeRange);
		query.append(timeIndexRange);
	    }
	    if (protocol.hasHightRange()) {
		NumericRange lvlIndexRange = variableReader.getAltitudeIndexRange(datasetKey, zRange);
		query.append(lvlIndexRange);
	    }
	    if (protocol.hasLatitudeRange()) {
		NumericRange latIndexRange = variableReader.getLatitudeIndexRange(datasetKey, latRange);
		query.append(latIndexRange);
	    }
	    if (protocol.hasLongitudeRange()) {
		NumericRange lonIndexRange = variableReader.getLongitudeIndexRange(datasetKey, lonRange);
		query.append(lonIndexRange);
	    }
	    
	    query.append(",");
	}
	
	query.removeLastChar();
    }

    @Override
    public boolean canTranslate(CollectiveProtocol collectiveProtocol)
    {
	return true;
    }
    
    @Override
    protected DimensionArray downloadDimensionData(CollectiveProtocol protocol)
    {
	// first request longitude, latitude and level in a single request
	String requestedSpatialDims = "";
	if (protocol.hasLongitudeRange())
	    requestedSpatialDims += "lon,";
	if (protocol.hasLatitudeRange())
	    requestedSpatialDims += "lat,";
	if (protocol.hasHightRange())
	    requestedSpatialDims += "lev,";

	IReader latReader = null;
	IReader lonReader = null;
	IReader lvlReader = null;
	if (!requestedSpatialDims.isEmpty())
	{
	    requestedSpatialDims = requestedSpatialDims.substring(0, requestedSpatialDims.length() - 1);
	    IReader reader = readerFactory();
	    reader.setUri(getDatasetBaseUrl(), requestedSpatialDims, getDataset() + "-spatial");
	    if (protocol.hasLongitudeRange())
		lonReader = reader;
	    if (protocol.hasLatitudeRange())
		latReader = reader;
	    if (protocol.hasHightRange())
		lvlReader = reader;
	}

	// second call required as NetCdf library is confused when the time
	// dimension is requested along with the longitude, latitude and
	// altitude
	IReader timeReader = null;
	if (protocol.hasTimeRangeDefined())
	{
	    timeReader = readerFactory();
	    timeReader.setUri(getDatasetBaseUrl(), "time", getDataset() + "-time");
	}

	return new DimensionArray(latReader, lonReader, lvlReader, timeReader);
    }
}
