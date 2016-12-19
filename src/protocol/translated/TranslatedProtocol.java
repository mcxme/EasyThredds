package protocol.translated;

import java.net.URI;
import java.net.URISyntaxException;

import protocol.CollectiveProtocol;
import protocol.Protocol;
import protocol.translated.util.QueryBuilder;

public abstract class TranslatedProtocol extends Protocol
{
    private CollectiveProtocol query;

    public TranslatedProtocol(CollectiveProtocol query)
    {
	super(query);
	this.query = query;
    }

    protected abstract String getProtocolUrlAbbrevation();

    protected abstract void translateQuery(CollectiveProtocol protocol, QueryBuilder query);

    public URI getTranslatedUrl()
    {
	StringBuilder builder = new StringBuilder();
	builder.append(getBaseUrl());
	builder.append("/");
	builder.append(getProtocolUrlAbbrevation());
	builder.append("/");
	builder.append(getDataset());
	builder.append("?");
	
	QueryBuilder translated = new QueryBuilder();
	translateQuery(query, translated);
	builder.append(translated);
	
	try {
	    return new URI(builder.toString());
	} catch (URISyntaxException e) {
	    throw new IllegalStateException(e);
	}
    }
}
