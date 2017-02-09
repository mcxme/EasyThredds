package protocol.reader.ncssMeta;

public class NCSSMetaFloatReader extends NCSSMetaReader
{
    private float[] values;

    public NCSSMetaFloatReader(String variableName, float[] values)
    {
	super(variableName);
	this.values = values;
    }
    
    public NCSSMetaFloatReader(String variableName, String startTxt, String incrementTxt, int length) {
	super(variableName);
	
	float start = Float.parseFloat(startTxt);
	float incr = Float.parseFloat(incrementTxt);
	values = new float[length];
	for (int i = 0; i < length; i++) {
	    values[i] = start;
	    start += incr;
	}
    }
    
    public NCSSMetaFloatReader(String variableName, int length)
    {
	super(variableName);
	this.values = new float[length];
    }

    @Override
    public float[] readFloatArray(String variableName)
    {
	if (hasVariableWithName(variableName))
	{
	    return values;
	} else
	{
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
	return Float.BYTES;
    }

    @Override
    public void set(int i, String numTxt)
    {
	values[i] = Float.parseFloat(numTxt);
    }
}
