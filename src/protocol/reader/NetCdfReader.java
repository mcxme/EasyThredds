package protocol.reader;

import java.io.IOException;

import ucar.ma2.Array;
import ucar.ma2.IndexIterator;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * This is a reader for NetCDF-files (.nc). It is based on the netCdf-library
 * for Java and provides all reading related functionality. The actual download
 * of the file should be handled by each protocol individually.
 */
public abstract class NetCdfReader implements IReader
{
    public static final String BASE_DIRECTORY = "netCdfFiles";
    
    
    private NetcdfFile dataset;
    
    @Override
    public void close() throws Exception
    {
	if (dataset != null) {
	    dataset.close();
	}
    }
    
    protected abstract NetcdfFile buildNetCdfFile(String baseUri, String query, String identifier);

    @Override
    public void setUri(String baseUri, String query)
    {
	setUri(baseUri, query, "");
    }
    
    @Override
    public void setUri(String baseUri, String query, String baseNameIdentifier)
    {
	dataset = buildNetCdfFile(baseUri, query, baseNameIdentifier);
    }

    @Override
    public long iterateAllData()
    {
	long bytes = 0L;
	for (Variable var : dataset.getVariables()) {
	    if (var.isCoordinateVariable()) continue;
	    
	    try {
		Array data = var.read();
		int dataByteSize = data.getDataType().getSize();
		IndexIterator it = data.getIndexIterator();
		while (it.hasNext()) {
		    it.next();
		    bytes += dataByteSize;
		}
	    } catch (IOException e) {
		e.printStackTrace();
		throw new IllegalArgumentException("Could not read the variabe " + var.getFullName(), e);
	    }
	}
        return bytes;
    }
    
    @Override
    public float[] readFloatArray(String variableName)
    {
        try {
            Variable var = dataset.findVariable(variableName);
            if (var == null) {
        	throw new IllegalArgumentException("No variable was found with the name " + variableName);
            } else if (!var.getDataType().isNumeric()) {
        	throw new IllegalArgumentException("The variable '" + variableName + "' is not numeric");
            } else if (!var.getDataType().isFloatingPoint()) {
        	throw new IllegalArgumentException("The variable '" + variableName + "' is not a floating point");
            }
            
            Array values = var.read();
	    return (float[]) values.copyTo1DJavaArray();
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not put the variable " + variableName + " into a 1D double array", e);
	}
    }
    
    @Override
    public long[] readLongArray(String variableName)
    {
        try {
            Variable var = dataset.findVariable(variableName);
            if (var == null) {
        	throw new IllegalArgumentException("No variable was found with the name " + variableName);
            } else if (!var.getDataType().isNumeric()) {
        	throw new IllegalArgumentException("The variable '" + variableName + "' is not numeric");
            } else if (var.getDataType().isFloatingPoint()) {
        	throw new IllegalArgumentException("The variable '" + variableName + "' is a floating point");
            }
            
	    return (long[]) var.read().copyTo1DJavaArray();
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not put the variable " + variableName + " into a 1D long array", e);
	}
    }
    
    @Override
    public double[] readDoubleArray(String variableName)
    {
        try {
            Variable var = dataset.findVariable(variableName);
            if (var == null) {
        	throw new IllegalArgumentException("No variable was found with the name " + variableName);
            } else if (!var.getDataType().isNumeric()) {
        	throw new IllegalArgumentException("The variable '" + variableName + "' is not numeric");
            } else if (!var.getDataType().isFloatingPoint()) {
        	throw new IllegalArgumentException("The variable '" + variableName + "' is a floating point");
            }
            
            Array values = var.read();
	    return (double[]) values.copyTo1DJavaArray();
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not put the variable " + variableName + " into a 1D long array", e);
	}
    }
    
    @Override
    public int[] variableShape(String variableName)
    {
	try {
            Variable var = dataset.findVariable(variableName);
            if (var == null) {
        	throw new IllegalArgumentException("No variable was found with the name " + variableName);
            }
            
            Array values = var.read();
            return values.getShape();
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not retrieve the size of the variable " + variableName, e);
	}
    }
    
    @Override
    public int singleDimVariableLength(String variableName)
    {
        int[] dims = variableShape(variableName);
        if (dims.length > 1) {
            throw new IllegalArgumentException("The variable has more than one dimension");
        }
        
        return dims[0];
    }
    
    @Override
    public boolean hasVariableWithName(String name)
    {
	return dataset.findVariable(name) != null;
    }

}
