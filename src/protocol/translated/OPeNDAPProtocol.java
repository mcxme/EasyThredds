package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.translated.util.QueryBuilder;

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
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	NumericRange xRange = protocol.getLongitudeRange();
	NumericRange yRange = protocol.getLatitudeRange();
	NumericRange zRange = protocol.getHightRange();
	
	// example output format: Z_sfc[0:1:0][0:1:94][0:1:134]
	query.append(VAR_NAME);
	query.append(zRange);
	query.append(xRange);
	query.append(yRange);
    }
}
