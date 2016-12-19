package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.translated.util.QueryBuilder;

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
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	query.add("todo", "true");
    }

}
