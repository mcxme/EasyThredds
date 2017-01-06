package reader;

import java.io.IOException;

import ucar.ma2.Array;
import ucar.ma2.IndexIterator;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public abstract class NetCdfReader implements IReader
{

    private NetcdfFile dataset;
    
    @Override
    public void close() throws Exception
    {
	if (dataset != null) {
	    dataset.close();
	}
    }
    
    protected abstract NetcdfFile buildNetCdfFile(String baseUri, String query);

    @Override
    public void setUri(String baseUri, String query)
    {
	dataset = buildNetCdfFile(baseUri, query);
    }

    @Override
    public long iterateAllData()
    {
	long bytes = 0L;
	for (Variable var : dataset.getVariables()) {
	    try {
		Array data = var.read();
		IndexIterator it = data.getIndexIterator();
		while (it.hasNext()) {
		    it.getByteNext();
		    bytes += 1;
		}
	    } catch (IOException e) {
		e.printStackTrace();
		throw new IllegalArgumentException("Could not read the variabe " + var.getFullName(), e);
	    }
	}
        return bytes;
    }

}
