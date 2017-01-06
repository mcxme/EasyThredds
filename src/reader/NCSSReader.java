package reader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;

public class NCSSReader extends NetCdfReader
{

    private File ncFile;
    
    @Override
    protected NetcdfFile buildNetCdfFile(String baseUri, String query)
    {
	try {
	    URL ncUrl = new URL(baseUri + "?" + query);
	    String fileName = "ncssFile.nc";
	    ncFile = new File(fileName);
	    FileUtils.copyURLToFile(ncUrl, ncFile);
	    return NetcdfFile.open("file:" + ncFile.getAbsolutePath());
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not build the NetCdf File", e);
	}
    }
    
    @Override
    public void close() throws Exception
    {
	if (ncFile != null) {
	    ncFile.delete();
	}
	
        super.close();
    }
    
}
