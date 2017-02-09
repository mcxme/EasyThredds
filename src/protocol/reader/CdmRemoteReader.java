package protocol.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import ucar.ma2.Array;
import ucar.ma2.IndexIterator;
import ucar.nc2.NetcdfFile;
import ucar.nc2.stream.CdmRemote;
import ucar.nc2.stream.NcStreamReader;

public class CdmRemoteReader implements IReader {
    
    private Array dataArray;
    private InputStream ncStream;
    private String variableName;
    
    public CdmRemoteReader() { }
    
    @Override
    public void close() throws Exception {
	if (ncStream != null) {
	    ncStream.close();
	}
    }

    public void setVariable(String variableName) {
	this.variableName = variableName;
    }
    
    @Override
    public void setUri(String baseUri, String query, String baseNameIdentifier)
    {
	setUri(baseUri, query);
    }
    
    @Override
    public void setUri(String baseUri, String query)
    {
	try {
	    // read the data from the ncstream
	    ncStream = CdmRemote.sendQuery(baseUri, query);
	    NcStreamReader ncStreamReader = new NcStreamReader();
	    Object data = ncStreamReader.readData(ncStream, null);
	    
	    // get the array data
	    Class resultClass = data.getClass();
	    Field arrayDataField = resultClass.getDeclaredField("data");
	    arrayDataField.setAccessible(true);
	    dataArray = (Array)arrayDataField.get(data);
	    
	    // http://www.unidata.ucar.edu/software/thredds/current/netcdf-java/reference/stream/NcstreamGrammer.html
//	    dataFile = NetcdfDataset.openFile("cdmremote:" + baseUri, null);
//	    ncStream = CdmRemote.sendQuery(baseUri, query);
//	    ncStreamReader = new NcStreamReader();
//	    ncStreamReader.readData(ncStream, dataFile);
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not open the file: " + baseUri, e);
	} catch (NoSuchFieldException | IllegalArgumentException e) {
	    throw new IllegalStateException("Wrong field", e);
	} catch (SecurityException | IllegalAccessException e) {
	    throw new IllegalStateException("Not allowed to perform generic changes", e);
	}
    }

    @Override
    public long iterateAllData()
    {
	long bytes = 0;
	int dataByteSize = dataArray.getDataType().getSize();
	IndexIterator it = dataArray.getIndexIterator();
	while (it.hasNext()) {
	    Object val = it.next();
	    bytes += dataByteSize;
	}
	
	return bytes;
    }

    @Override
    public float[] readFloatArray(String variableName)
    {
	return (float[]) dataArray.copyTo1DJavaArray();
    }

    @Override
    public long[] readLongArray(String variableName)
    {
	return (long[]) dataArray.copyTo1DJavaArray();
    }
    
    @Override
    public double[] readDoubleArray(String variableName)
    {
	return (double[]) dataArray.copyTo1DJavaArray();
    }
    
    @Override
    public boolean hasVariableWithName(String name)
    {
	if (variableName == null) {
	    throw new IllegalStateException("No variable was set");
	}
	
	return variableName.equals(name);
    }
    
    @Override
    public int singleDimVariableLength(String variableName)
    {
	throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    public int[] variableShape(String variableName)
    {
	return dataArray.getShape();
    }
}
