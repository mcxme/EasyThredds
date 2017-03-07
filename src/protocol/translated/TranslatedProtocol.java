package protocol.translated;

import java.net.URI;
import java.net.URISyntaxException;

import protocol.CollectiveProtocol;
import protocol.Protocol;
import protocol.reader.IReader;
import protocol.translated.util.DimensionArray;
import protocol.translated.util.QueryBuilder;
import protocol.translated.util.VariableReader;

/**
 * This class describes common behaviour for external protocols into which the
 * collective protocol can be translated (see
 * {@link protocol.CollectiveProtocol}).
 */
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
    
    /**
     * Returns the name of the protocol.
     */
    public abstract String getProtocolName();
    
    /**
     * Returns a file extension for the data file required by the protocol
     */
    protected abstract String getFileNameExtension();

    /**
     * Translates the given protocol using the query builder.
     */
    protected abstract void translateQuery(CollectiveProtocol protocol, QueryBuilder query);
    
    public abstract service.ProtocolPicker.Protocol getType();
    
    /**
     * Provides a reader for the current protocol 
     */
    protected abstract IReader readerFactory();
    
    /**
     * Checks whether the given collective protocol (and query) can be
     * translated using this protocol.
     */
    public abstract boolean canTranslate(CollectiveProtocol collectiveProtocol);
    
    /**
     * Gets the reader corresponding to this protocol.
     */
    public IReader getReader() {
	IReader reader = readerFactory();
	String uri = getTranslatedHttpUrl().toString();
	String[] parts = uri.split("\\?");
	reader.setUri(parts[0], parts[1]);
	return reader;
    }

    /**
     * Gets the netCdf URL: this is basically the normal URL where HTTP(s) is
     * replaced with the protocol.
     */
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
    
    /**
     * Gets the entire dataset url: base URL/protocol/dataset.extension
     */
    public String getDatasetBaseUrl() {
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
    
    /**
     * Gets the entire dataset url with the query: {@link #getDatasetBaseUrl()}
     * ?query
     */
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
    
    /**
     * The sub classes are responsible for downloading dimensionality data.
     */
    protected abstract DimensionArray downloadDimensionData(CollectiveProtocol protocol);

    /**
     * Returns the variable reader for the dimension data which is either loaded
     * from cache or from the remote data set.
     * 
     * @return
     */
    public VariableReader getDimensionData()
    {
	VariableReader variableReader = VariableReader.getInstance();
	String datasetKey = getDataset();
	// need to fetch the dataset?
	if (!variableReader.hasDataset(datasetKey)) {
	    DimensionArray dims = downloadDimensionData(query);
	    variableReader.addDataset(datasetKey, dims);
	}

	return variableReader;
    }

    /**
     * Gets only the raw dimension data as opposed to {@link #getDimensionData()}
     */
    public DimensionArray getDimensionArray() {
	VariableReader reader = getDimensionData();
	return reader.getDataset(getDataset());
    }
}
