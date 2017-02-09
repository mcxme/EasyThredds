package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.reader.IReader;
import protocol.reader.OPeNDAPReader;

public class OPeNDAPProtocol extends DapProtocol
{
    
    public static final String FILE_EXTENSION = "dods";

    public OPeNDAPProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getOpenDapUrlName();
    }
    
    @Override
    public String getProtocolName() {
	return "OPeNDAP";
    }
    
    @Override
    protected String getNetCdfName() {
	return "dods";
    }
    
    @Override
    protected IReader readerFactory()
    {
	return new OPeNDAPReader();
    }
    
    @Override
    protected String getFileNameExtension()
    {
	return FILE_EXTENSION;
    }
}
