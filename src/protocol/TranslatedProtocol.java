package protocol;

public abstract class TranslatedProtocol extends Protocol
{
	protected abstract String getProtocolUrlAbbrevation();
		
	protected abstract String getTranslatedQuery(CollectiveProtocol protocol);
	
	public String getTranslatedUrl(CollectiveProtocol protocol) {
		StringBuilder builder = new StringBuilder();
		builder.append(getBaseUrl());
		builder.append("/");
		builder.append(getProtocolUrlAbbrevation());
		builder.append("/");
		builder.append(getDataset());
		builder.append("?");
		builder.append(getTranslatedQuery(protocol));
		return builder.toString();
	}
}
