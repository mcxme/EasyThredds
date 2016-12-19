package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
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
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	query.add("req", "data");
	query.add("var", protocol.getVariables());
	
	if (protocol.hasTimeRangeDefined()) {
	    TimeRange timeRange = protocol.getTimeRange();
	    query.add("time_start", timeRange.getStart());
	    query.add("time_end", timeRange.getEnd());
	}
	
	if (protocol.hasLatitudeRange()) {
	    NumericRange latRange = protocol.getLatitudeRange();
	    query.add("north", latRange.getEnd());
	    query.add("south", latRange.getStart());
	}
	
	if (protocol.hasLongitudeRange()) {
	    NumericRange lonRange = protocol.getLongitudeRange();
	    query.add("east", lonRange.getStart());
	    query.add("west", lonRange.getEnd());
	}
    }

}
