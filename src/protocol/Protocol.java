package protocol;

/**
 * This is the base class of all protocols and merely defines some common
 * functionality and data.
 */
public abstract class Protocol
{
    /**
     * The dataset as used in the THREDDS catalogue. E.g. 'dataset/foo/bar.sc'
     */
    private String dataset;
    /**
     * The base URL targeted by this protocol. E.g. 'localhost:8080' or
     * 'http://nc-catalogue.scc.kit.edu/thredds'
     */
    private String baseUrl;

    public Protocol(String dataset, String baseUrl)
    {
	setBaseUrl(baseUrl);
	setDataset(dataset);
    }

    public Protocol(Protocol other)
    {
	if (other == null || other.dataset == null || other.baseUrl == null)
	{
	    throw new IllegalArgumentException("Invalid protocol: the dataset and base URL have to be set");
	}

	this.dataset = other.dataset;
	this.baseUrl = other.baseUrl;
    }

    public void setDataset(String dataset)
    {
	this.dataset = dataset;
    }

    public void setBaseUrl(String baseUrl)
    {
	this.baseUrl = baseUrl;
    }

    public String getDataset()
    {
	return this.dataset;
    }

    public String getBaseUrl()
    {
	return this.baseUrl;
    }
}
