package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;

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
