package protocol.reader.ncssMeta;

public class NCSSMetaLongReader extends NCSSMetaReader
{
    private long[] values;
    
    public NCSSMetaLongReader(String variableName, long[] values)
    {
	super(variableName);
	this.values = values;
    }
    
    public NCSSMetaLongReader(String variableName, String startTxt, String incrementTxt, int length) {
	super(variableName);
	
	long start = Long.parseLong(startTxt);
	long incr = Long.parseLong(incrementTxt);
	values = new long[length];
	for (int i = 0; i < length; i++) {
	    values[i] = start;
	    start += incr;
	}
    }

    public NCSSMetaLongReader(String variableName, int length)
    {
	super(variableName);
	this.values = new long[length];
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
    public void set(int i, String numtxt)
    {
	values[i] = Long.parseLong(numtxt);
    }

    @Override
    protected int dataTypeSize()
    {
	return Long.BYTES;
    }

}
