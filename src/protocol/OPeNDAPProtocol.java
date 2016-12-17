package protocol;

import util.ConfigReader;

public class OPeNDAPProtocol extends TranslatedProtocol
{
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
	return null;
    }

}
