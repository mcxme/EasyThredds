package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import reader.Dap4Reader;
import reader.IReader;

public class Dap4Protocol extends DapProtocol
{
    public Dap4Protocol(CollectiveProtocol query)
    {
	super(query);
    }
    
    @Override
    protected String getFileNameExtension()
    {
	return null;
    }
    
    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getDap4UrlName();
    }
    
    @Override
    protected String getNetCdfName() {
	return "dap4";
    }

    @Override
    protected IReader readerFactory()
    {
	return new Dap4Reader();
    }

    @Override
    public String getProtocolName() {
	return "DAP4";
    }
    
}
