package reader;

import java.io.IOException;
import java.io.InputStream;

import ucar.nc2.stream.CdmRemote;

public class CdmRemoteReader implements IReader {
    
    private InputStream stream;
    
    public CdmRemoteReader() { }
    
    @Override
    public void setUri(String baseUri, String query)
    {
	try {
	    stream = CdmRemote.sendQuery(baseUri, query);
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not open the file: " + baseUri, e);
	}
    }
    
    @Override
    public void close() throws Exception {
	if (stream != null) {
	    stream.close();
	}
    }
    
    @Override
    public long iterateAllData()
    {
	try {
	    long bytes = 0L;
	    while (stream.read() != -1) { bytes += 1L;}
	    return bytes;
	} catch (IOException e) {
	    throw new IllegalArgumentException("COuld not read the input stream", e);
	}
    }
}
