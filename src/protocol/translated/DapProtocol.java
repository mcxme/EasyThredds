package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.QueryBuilder;
import protocol.translated.util.VariableReader;
import reader.IReader;

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
	VariableReader variableReader = loadDimensionData(protocol);
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
    
    private VariableReader loadDimensionData(CollectiveProtocol protocol) {
	VariableReader variableReader = VariableReader.getInstance();
	String datasetKey = getDataset();
	if (!variableReader.hasDataset(datasetKey)) {
	    IReader reader = readerFactory();
	    reader.setUri(getDatasetBaseUrl(), "lon,lat,lev,time");
	    variableReader.addDataset(datasetKey, reader);
	}
	
	return variableReader;
    }
}
