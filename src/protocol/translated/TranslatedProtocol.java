package protocol.translated;

import java.net.URI;
import java.net.URISyntaxException;

import protocol.CollectiveProtocol;
import protocol.Protocol;
import protocol.translated.util.QueryBuilder;
import reader.IReader;

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
    
    protected abstract String getNetCdfName();
    
    public abstract String getProtocolName();
    
    /**
     * Returns a file extension for the data file required by the protocol
     */
    protected abstract String getFileNameExtension();

    /**
     * Translates the given protocol using the query builder.
     */
    protected abstract void translateQuery(CollectiveProtocol protocol, QueryBuilder query);
    
    /**
     * Provides a reader for the current protocol 
     */
    protected abstract IReader readerFactory();
    
    public abstract boolean canTranslate(CollectiveProtocol collectiveProtocol);
    
    public IReader getReader() {
	IReader reader = readerFactory();
	String uri = getTranslatedHttpUrl().toString();
	String[] parts = uri.split("\\?");
	reader.setUri(parts[0], parts[1]);
	return reader;
    }

    public URI getTranslatedNetCdfUrl() {
	URI http = getTranslatedHttpUrl();
	String protocol = getNetCdfName();
	String replaced = http.toString().replaceFirst("http", protocol);
	try {
	    return new URI(replaced);
	} catch (URISyntaxException e){
	    e.printStackTrace();
	    return null;
	}
    }
    
    protected String getDatasetBaseUrl() {
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
	return builder.toString();
    }
    
    public URI getTranslatedHttpUrl()
    {
	QueryBuilder translated = new QueryBuilder();
	translateQuery(query, translated);
	
	// format: base/abbr/data/set.ext?query
	StringBuilder builder = new StringBuilder();
	builder.append(getDatasetBaseUrl());
	builder.append("?");
	builder.append(translated);
	
	try {
	    return new URI(builder.toString());
	} catch (URISyntaxException e) {
	    throw new IllegalStateException(e);
	}
    }
    
    @Override
    public String toString() {
	return getTranslatedHttpUrl().toString();
    }
}
