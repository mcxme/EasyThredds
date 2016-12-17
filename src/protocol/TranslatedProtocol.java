package protocol;

public abstract class TranslatedProtocol extends Protocol
{
    	private CollectiveProtocol query;
    	
    	public TranslatedProtocol(CollectiveProtocol query) {
    	    super(query);
    	    this.query = query;
    	}
    
	protected abstract String getProtocolUrlAbbrevation();
		
	protected abstract String getTranslatedQuery(CollectiveProtocol protocol);
	
	public String getTranslatedUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append(getBaseUrl());
		builder.append("/");
		builder.append(getProtocolUrlAbbrevation());
		builder.append("/");
		builder.append(getDataset());
		builder.append("?");
		builder.append(getTranslatedQuery(query));
		return builder.toString();
	}
}
