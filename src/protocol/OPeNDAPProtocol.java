package protocol;

import util.ConfigReader;

public class OPeNDAPProtocol extends TranslatedProtocol
{
    private static final String VAR_NAME = "Z_sfc";
    
    public OPeNDAPProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getOpenDapUrlName();
    }

    @Override
    protected String getTranslatedQuery(CollectiveProtocol protocol)
    {
	Range xRange = protocol.getLongitudeRange();
	Range yRange = protocol.getLatitudeRange();
	Range zRange = protocol.getHightRange();
	
	// example output format: Z_sfc[0:1:0][0:1:94][0:1:134]
	StringBuilder builder = new StringBuilder(VAR_NAME);
	builder.append(zRange);
	builder.append(xRange);
	builder.append(yRange);
	
	return builder.toString();
    }
}
