package protocol;

import util.ConfigReader;

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
    protected StringBuilder getTranslatedQuery(CollectiveProtocol protocol)
    {
	StringBuilder builder = new StringBuilder();
	
	return builder;
    }

}
