package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.Range;

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
    protected StringBuilder getTranslatedQuery(CollectiveProtocol protocol)
    {
	StringBuilder builder = new StringBuilder();
	
	Range latRange = protocol.getLatitudeRange();
	Range lonRange = protocol.getLongitudeRange();
	
	// create the bounding box
	builder.append("north=");
	builder.append(latRange.getEnd());
	builder.append("&south=");
	builder.append(latRange.getStart());
	builder.append("&east=");
	builder.append(lonRange.getStart());
	builder.append("&west=");
	builder.append(lonRange.getEnd());
	
	// add all variables
	builder.append("&var=");
	for (String variable : protocol.getVariables()) {
	    builder.append(variable);
	    builder.append(",");
	}
	builder.deleteCharAt(builder.length() - 1);
	
	// take the minimum stride
	int horizontalStride = Math.min(latRange.getStep(), lonRange.getStep());
	builder.append("&horizStride=");
	builder.append(horizontalStride);
	
	return builder;
    }

}
