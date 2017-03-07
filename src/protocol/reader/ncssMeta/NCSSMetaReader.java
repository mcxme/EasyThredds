package protocol.reader.ncssMeta;

import protocol.reader.IReader;

/**
 * This reader class helps to read dimensionality meta data for the NCSS
 * protocol.
 */
public abstract class NCSSMetaReader implements IReader
{
    private String variableName;
    
    public NCSSMetaReader(String variableName) {
	if (variableName == null || variableName.isEmpty()) {
	    throw new IllegalArgumentException("has to specify a valid variable");
	}
	
	this.variableName = variableName;
    }

    protected abstract int getLength();
    
    /**
     * Get the i-th item.
     */
    public abstract Number get(int i);
    
    /**
     * Set the i-th item.
     */
    public abstract void set(int i, String numTxt);
        
    
    protected abstract int dataTypeSize();
    
    @Override
    public void close() throws Exception {}

    @Override
    public void setUri(String baseUri, String query)
    {
	throw new UnsupportedOperationException("Not supported -> use the constructor instead");
    }

    @Override
    public void setUri(String baseUri, String query, String baseNameIdentifier)
    {
	setUri(baseUri, query);
    }
    
    @Override
    public long iterateAllData()
    {
	long bytes = 0;
	for (int i = 0; i < getLength(); i++) {
	    get(i);
	    bytes += dataTypeSize();
	}
	
	return bytes;
    }

    @Override
    public boolean hasVariableWithName(String name)
    {
	return this.variableName.equals(name);
    }

    @Override
    public float[] readFloatArray(String variableName)
    {
	throw new UnsupportedOperationException("The variable is not of type float");
    }

    @Override
    public double[] readDoubleArray(String variableName)
    {
	throw new UnsupportedOperationException("The variable is not of type double");
    }

    @Override
    public long[] readLongArray(String variableName)
    {
	throw new UnsupportedOperationException("The variable is not of type long");
    }

    @Override
    public int[] variableShape(String variableName)
    {
	if (hasVariableWithName(variableName)) {
	    return new int[] { singleDimVariableLength(variableName) };
	} else {
	    return null;
	}
    }

    @Override
    public int singleDimVariableLength(String variableName)
    {
	if (hasVariableWithName(variableName)) {
	    return getLength(); 
	} else {
	    return 0;
	}
    }

}
