package performance;

import java.io.IOException;
import java.io.InputStream;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.stream.CdmRemote;

public class NetCdfReader implements AutoCloseable {
    
    private NetcdfFile file;
    private InputStream stream;
    
    public NetCdfReader(String uri) {
	try {
	    String[] parts = uri.split("\\?");
	    stream = CdmRemote.sendQuery(parts[0], parts[1]);
	    
//	    file = NetcdfDataset.openDataset(uri);
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not open the file: " + uri, e);
	}
    }

    @Override
    public void close() throws Exception {
	if (file != null) {
	    file.close();
	}
    }
    
    public void iterateData() {
	try {
	    while (stream.read() != -1) {}
	} catch (IOException e) {
	    throw new IllegalArgumentException("COuld not read the input stream", e);
	}
//	for (Variable var : file.getVariables()) {
//	    try {
//		var.read();
//	    } catch (IOException e) {
//		e.printStackTrace();
//		throw new IllegalArgumentException("Could not read the variabe " + var.getFullName(), e);
//	    }
//	}
    }
}
