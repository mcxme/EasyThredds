package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.QueryBuilder;
import reader.CdmRemoteReader;
import reader.IReader;

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
    protected IReader readerFactory()
    {
	return new CdmRemoteReader();
    }
    
    @Override
    public String getProtocolName() {
	return "CdmRemote";
    }
    
    @Override
    protected String getNetCdfName() {
	return "cdmremote";
//	throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    protected String getFileNameExtension()
    {
	return null;
    }

    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	TimeRange timeRange = protocol.getTimeRange();
	SpatialRange latRange = protocol.getLatitudeRange();
	SpatialRange lonRange = protocol.getLongitudeRange();
	NumericRange lvlRange = protocol.getHightRange();

	query.add("req", "data");
	if (protocol.getVariables().isEmpty()) {
	    throw new IllegalArgumentException("at least one variable has to be defined");
	}
	
	query.append("&var=");
	for (String var : protocol.getVariables()) {
	    // a(time, lev, lat, lon);b;c
	    query.append(var);
	    query.append("(");
	    // TODO: remove lvl ranges
	    if (protocol.hasTimeRangeDefined()) {
		query.append(textualRange(lvlRange));
		query.append(",");
	    }
	    if (protocol.hasHightRange()) {
		query.append(textualRange(lvlRange));
		query.append(",");
	    }
	    if (protocol.hasLatitudeRange()) {
		query.append(textualRange(lvlRange));
		query.append(",");
	    }
	    if (protocol.hasLongitudeRange()) {
		query.append(textualRange(lvlRange));
		query.append(",");
	    }
	    
	    query.removeLastChar();
	    query.append(")");
	    query.append(";");
	}
	query.removeLastChar();
	
	if (protocol.hasTimeRangeDefined()) {
	    query.add("time_start", timeRange.getStartTime());
	    query.add("time_end", timeRange.getEndTime());
	}
	
	if (protocol.hasLatitudeRange()) {
	    query.add("north", latRange.getEndCoordinate());
	    query.add("south", latRange.getStartCoordinate());
	}
	
	if (protocol.hasLongitudeRange()) {
	    query.add("east", lonRange.getStartCoordinate());
	    query.add("west", lonRange.getEndCoordinate());
	}
	
	query.add("accept", ConfigReader.getInstace().getCdmRemoteOutputFormat());
    }
    
    private String textualRange(NumericRange range) {
	assert (range != null);
	String out = "" + range.getStart() + ":";
	if (!range.hasDefaultStride()) {
	    out += range.getStride() + ":";
	}
	out += range.getEnd();
	return out;
    }

}
