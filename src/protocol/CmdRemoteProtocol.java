package protocol;

public class CmdRemoteProtocol extends TranslatedProtocol
{

    public CmdRemoteProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return null;
    }

    @Override
    protected String getTranslatedQuery(CollectiveProtocol protocol)
    {
	return null;
    }

}
