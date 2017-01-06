package reader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import ucar.ma2.Array;
import ucar.ma2.IndexIterator;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class Dap4Reader implements IReader
{
    private NetcdfFile dataset;
    
    @Override
    public void setUri(String baseUri, String query)
    {
	try {
	    URL dodsUrl = new URL(baseUri + "?" + query);
	    String ddsBase = baseUri.replace(".dap", ".dds");
	    URL ddsUrl = new URL(ddsBase + "?" + query);
	    
	    String fileName = "dap4File.dap";
	    File downloadedFile = new File(fileName);
	    File dodsFile = new File(fileName + ".dap");
	    File ddsFile = new File(fileName + ".dds");
	    FileUtils.copyURLToFile(dodsUrl, dodsFile);
	    FileUtils.copyURLToFile(ddsUrl, ddsFile);
	    dataset = NetcdfDataset.openDataset("file:" + downloadedFile.getAbsolutePath());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void close() throws Exception {
	if (dataset != null) {
	    dataset.close();
	}
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
