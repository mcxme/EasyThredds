package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
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
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getNcssUrlName();
    }

    /**
     * Forms the query based on the Netcdf Subset Service Reference:
     * {@link http://www.unidata.ucar.edu/software/thredds/current/tds/reference/NetcdfSubsetServiceReference.html}
     */
    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	NumericRange latRange = protocol.getLatitudeRange();
	NumericRange lonRange = protocol.getLongitudeRange();
	
	// create the bounding box
	query.add("north", latRange.getEnd());
	query.add("east", lonRange.getStart());
	query.add("south", latRange.getStart());
	query.add("west", lonRange.getEnd());
	
	// add all variables
	if (protocol.hasVariablesDefined()) {
	    query.add("var", protocol.getVariables());
	}
	
	// take the minimum stride
	int horizontalStride = Math.min(latRange.getStep(), lonRange.getStep());
	query.add("horizStride", horizontalStride);
	
	if (protocol.hasHightRange()) {
	    NumericRange hightRange = protocol.getHightRange();
	    if (hightRange.isSingleValue()) {
		// The range is a single value -> single level
		query.add("vertCoord", hightRange.getStart());
	    } else {
		// The range has multiple values -> vertical stride
		query.add("vertStride", hightRange.getStep());
	    }
	}
	
	// add the time specification if defined
	if (protocol.hasTimeRangeDefined()) {
	    TimeRange timeRange = protocol.getTimeRange();
	    query.add("time_start", timeRange.getStart());
	    query.add("time_end", timeRange.getEnd());
	    query.add("time_stride", timeRange.getStride());
	}
    }

}
