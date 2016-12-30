package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.QueryBuilder;

public class CdmRemoteProtocol extends TranslatedProtocol
{

    public CdmRemoteProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getCdmRemoteUrlName();
    }    
    
    @Override
    protected String getFileNameExtension()
    {
	return null;
    }

    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	query.add("req", "data");
	query.add("var", protocol.getVariables());
	
	if (protocol.hasTimeRangeDefined()) {
	    TimeRange timeRange = protocol.getTimeRange();
	    query.add("time_start", timeRange.getStartTime());
	    query.add("time_end", timeRange.getEndTime());
	}
	
	if (protocol.hasLatitudeRange()) {
	    SpatialRange latRange = protocol.getLatitudeRange();
	    query.add("north", latRange.getEndCoordinate());
	    query.add("south", latRange.getStartCoordinate());
	}
	
	if (protocol.hasLongitudeRange()) {
	    SpatialRange lonRange = protocol.getLongitudeRange();
	    query.add("east", lonRange.getStartCoordinate());
	    query.add("west", lonRange.getEndCoordinate());
	}
    }

}
