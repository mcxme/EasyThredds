
public abstract class TranslatedProtocol extends Protocol
{
	public abstract void translate(CollectiveProtocol protocol);
	
	protected abstract String getProtocolUrlAbbrevation();
		
	protected abstract String getTranslatedQuery();
	
	public String getUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append(getBaseUrl());
		builder.append("/");
		builder.append(getProtocolUrlAbbrevation());
		builder.append("/");
		builder.append(getDataset());
		builder.append("?");
		builder.append(getTranslatedQuery());
		return builder.toString();
	}
}
