package protocol;

public class NCSSProtocol extends TranslatedProtocol
{
    public NCSSProtocol(CollectiveProtocol query)
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
