package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.QueryBuilder;

public abstract class DapProtocol extends TranslatedProtocol
{
    public DapProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	SpatialRange lonRange = protocol.getLongitudeRange();
	SpatialRange latRange = protocol.getLatitudeRange();
	NumericRange zRange = protocol.getHightRange();
	TimeRange timeRange = protocol.getTimeRange();
	
	// output format: a[time][level][lat][lon], b[][][][], ...
	for (String var : protocol.getVariables()) {
	    query.append(var);
	    if (protocol.hasTimeRangeDefined()) {
		query.append(timeRange.getSelection());
	    }
	    if (protocol.hasHightRange()) {
		query.append(zRange);
	    }
	    if (protocol.hasLatitudeRange()) {
		query.append(latRange.getSelection());
	    }
	    if (protocol.hasLongitudeRange()) {
		query.append(lonRange.getSelection());
	    }
	    
	    query.append(",");
	}
	
	query.removeLastChar();
    }
}
