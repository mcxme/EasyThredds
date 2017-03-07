package protocol.reader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * A reader that is capable of downloading remote OPeNDAP data sets.
 */
public class OPeNDAPReader extends NetCdfReader
{
    // the actual data file
    private File dodsFile;
    // the data description
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
	URL dodsUrl = null;
	try {
	    
	    // create the query as specified by OPeNDAP
	    dodsUrl = new URL(baseUri + "?" + query);
	    String ddsBase = baseUri;
	    if (baseUri.contains(".dods")) {
		ddsBase = baseUri.replace(".dods", ".dds");
	    } else if (!baseUri.contains(".dds")) {
		ddsBase = baseUri + ".dds";
	    }
	    
	    // Download the data along with the description
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
	    
	    // provide the stored data to the netCdf library
	    return NetcdfDataset.openFile("file:" + downloadedFile.getAbsolutePath(), null);
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not build the NetCdf File from URL " + dodsUrl.toString(), e);
	}
    }
}
