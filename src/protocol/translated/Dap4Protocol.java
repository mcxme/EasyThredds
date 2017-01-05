package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
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
	throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getProtocolName() {
	return "DAP4";
    }
    
}
