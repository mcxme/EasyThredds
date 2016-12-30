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

    /**
     * Returns the shortcut of the protocol which is used in the URI
     */
    protected abstract String getProtocolUrlAbbrevation();
    
    /**
     * Returns a file extension for the data file required by the protocol
     */
    protected abstract String getFileNameExtension();

    /**
     * Translates the given protocol using the query builder.
     */
    protected abstract void translateQuery(CollectiveProtocol protocol, QueryBuilder query);

    public URI getTranslatedUrl()
    {
	QueryBuilder translated = new QueryBuilder();
	translateQuery(query, translated);
	
	// format: base/abbr/data/set.ext?query
	StringBuilder builder = new StringBuilder();
	builder.append(getBaseUrl());
	builder.append("/");
	builder.append(getProtocolUrlAbbrevation());
	builder.append("/");
	builder.append(getDataset());
	String extension = getFileNameExtension();
	if (extension != null) {
	    builder.append(".");
	    builder.append(extension);
	}
		
	builder.append("?");
	builder.append(translated);
	
	try {
	    return new URI(builder.toString());
	} catch (URISyntaxException e) {
	    throw new IllegalStateException(e);
	}
    }
}
