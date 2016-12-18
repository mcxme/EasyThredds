package protocol;

import util.ConfigReader;

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

    @Override
    protected String getTranslatedQuery(CollectiveProtocol protocol)
    {
	return null;
    }

}
