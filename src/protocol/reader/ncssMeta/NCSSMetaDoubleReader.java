package protocol.reader.ncssMeta;

/**
 * This subclass for reading dimensionality data for the NCSS protocol is
 * responsible for double data.
 */

public class NCSSMetaDoubleReader extends NCSSMetaReader
{

    private double[] values;
    
    public NCSSMetaDoubleReader(String variableName, double[] values)
    {
	super(variableName);
	this.values = values;
    }
    
    public NCSSMetaDoubleReader(String variableName, String startTxt, String incrementTxt, int length) {
	super(variableName);
	
	double start = Double.parseDouble(startTxt);
	double incr = Double.parseDouble(incrementTxt);
	values = new double[length];
	for (int i = 0; i < length; i++) {
	    values[i] = start;
	    start += incr;
	}
    }
    
    public NCSSMetaDoubleReader(String variableName, int length)
    {
	super(variableName);
	this.values = new double[length];
    }
    
    @Override
    public double[] readDoubleArray(String variableName)
    {
	if (hasVariableWithName(variableName)) {
	    return values;
	} else {
	    return null;
	}
    }

    @Override
    protected int getLength()
    {
	return values.length;
    }

    @Override
    public Number get(int i)
    {
	return values[i];
    }

    @Override
    protected int dataTypeSize()
    {
	return Double.BYTES;
    }

    @Override
    public void set(int i, String numtxt)
    {
	values[i] = Double.parseDouble(numtxt);
    }

}
