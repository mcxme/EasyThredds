package protocol;

public abstract class Protocol
{

	private String dataset;
	private String baseUrl;
	
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	protected String getDataset() {
		return this.dataset;
	}
	
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
