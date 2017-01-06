package reader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;

public class OPeNDAPReader extends NetCdfReader
{
    private File dodsFile;
    private File ddsFile;
    
    @Override
    public void close() throws Exception {
	if (dodsFile != null) {
	    dodsFile.delete();
	}
	if (ddsFile != null) {
	    ddsFile.delete();
	}
	
	super.close();
    }

    @Override
    protected NetcdfFile buildNetCdfFile(String baseUri, String query)
    {
	try {
	    URL dodsUrl = new URL(baseUri + "?" + query);
	    String ddsBase = baseUri.replace(".dods", ".dds");
	    URL ddsUrl = new URL(ddsBase + "?" + query);
	    String fileName = "opendapFile.dods";
	    File downloadedFile = new File(fileName);
	    dodsFile = new File(fileName + ".dods");
	    ddsFile = new File(fileName + ".dds");
	    FileUtils.copyURLToFile(dodsUrl, dodsFile);
	    FileUtils.copyURLToFile(ddsUrl, ddsFile);
	    return NetcdfDataset.openDataset("file:" + downloadedFile.getAbsolutePath());
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not build the NetCdf File", e);
	}
    }
}
