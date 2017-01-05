package performance;

import java.io.IOException;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class NetCdfReader implements AutoCloseable {
    
    private NetcdfFile file;
    
    public NetCdfReader(String uri) {
	try {
	    file = NetcdfDataset.openDataset(uri);
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not open the file: " + uri, e);
	}
    }

    @Override
    public void close() throws Exception {
	file.close();
    }
    
    public void iterateData() {
	for (Variable var : file.getVariables()) {
	    try {
		var.read();
	    } catch (IOException e) {
		e.printStackTrace();
		throw new IllegalArgumentException("Could not read the variabe " + var.getFullName(), e);
	    }
	}
    }
}
