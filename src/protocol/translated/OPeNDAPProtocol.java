package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;

public class OPeNDAPProtocol extends DapProtocol
{
    
    public static final String FILE_EXTENSION = "ascii";

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
    protected String getFileNameExtension()
    {
	return null; //FILE_EXTENSION;
    }
}
