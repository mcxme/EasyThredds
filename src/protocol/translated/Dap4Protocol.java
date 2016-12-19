package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;

public class Dap4Protocol extends DapProtocol
{
    public Dap4Protocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getDap4UrlName();
    }
}
