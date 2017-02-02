package protocol;

public abstract class Protocol
{
	private String dataset;
	private String baseUrl;

	public Protocol(String dataset, String baseUrl) {
	    setBaseUrl(baseUrl);
	    setDataset(dataset);
	}
	
	public Protocol(Protocol other) {
	    if (other == null || other.dataset == null || other.baseUrl == null) {
		throw new IllegalArgumentException("Invalid protocol: the dataset and base URL have to be set");
	    }
	    
	    this.dataset = other.dataset;
	    this.baseUrl = other.baseUrl;
	}
	
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getDataset() {
		return this.dataset;
	}
	
	public String getBaseUrl() {
		return this.baseUrl;
	}
}
