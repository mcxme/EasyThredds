package protocol.reader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import ucar.nc2.NetcdfFile;

/**
 * A reader that is capable of downloading remote NCSS data sets.
 */
public class NCSSReader extends NetCdfReader
{

    // The netCdf file
    private File ncFile;
    
    @Override
    protected NetcdfFile buildNetCdfFile(String baseUri, String query, String identifier)
    {
	try {
	    // download the .nc file
	    URL ncUrl = new URL(baseUri + "?" + query);
	    String fileName = (identifier.isEmpty() || identifier == null)? "defaultNcss" : identifier;
	    String filePath = BASE_DIRECTORY + File.separator
		    + "ncssFiles" + File.separator
		    + fileName + ".nc";
	    ncFile = new File(filePath);
	    FileUtils.copyURLToFile(ncUrl, ncFile);
	    
	    // provide the .nc file locally to the netCdf library
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
