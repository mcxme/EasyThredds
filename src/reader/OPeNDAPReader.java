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
    protected NetcdfFile buildNetCdfFile(String baseUri, String query, String identifier)
    {
	try {
	    URL dodsUrl = new URL(baseUri + "?" + query);
	    String ddsBase = baseUri;
	    if (baseUri.contains(".dods")) {
		ddsBase = baseUri.replace(".dods", ".dds");
	    } else if (!baseUri.contains(".dds")) {
		ddsBase = baseUri + ".dds";
	    }
	    
	    URL ddsUrl = new URL(ddsBase + "?" + query);
	    String fileName = (identifier.isEmpty() || identifier == null)? "defaultOpendap" : identifier;
	    String filePath = BASE_DIRECTORY + File.separator +
		    "opendapFiles" + File.separator
		    + fileName + ".dods";
	    File downloadedFile = new File(filePath);
	    dodsFile = new File(filePath + ".dods");
	    ddsFile = new File(filePath + ".dds");
	    FileUtils.copyURLToFile(dodsUrl, dodsFile);
	    FileUtils.copyURLToFile(ddsUrl, ddsFile);
	    return NetcdfDataset.openFile("file:" + downloadedFile.getAbsolutePath(), null);
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not build the NetCdf File", e);
	}
    }
}
