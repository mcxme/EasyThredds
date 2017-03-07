package protocol.parse;

/**
 * Specifies some common behaviour of all ranges. Each range has some concept of
 * a start, a stride and an end. Whereas, start and end are defined
 * individually, the stride is always a positive integer.
 *
 * A range where the start is equal to the end is considered a point.
 */
public abstract class Range
{
    public static final int STD_STRIDE = 1;
    
    protected int stride;
    
    public Range(int stride) {
	if (stride < 1) {
	    throw new IllegalArgumentException("The stride has to be a positive integer");
	}
	
	this.stride = stride;
    }
    
    public abstract boolean isPoint();
    
    public int getStride() {
	return stride;
    }
    
    public boolean hasDefaultStride() {
	    return stride == STD_STRIDE;
	}

    /**
     * Parses the given numerical stride. If none is provided the default stride
     * is assumed.
     */
    protected static int parseStride(String textualStride) {
	return (textualStride.isEmpty() || textualStride == null)?
		STD_STRIDE
		: Integer.parseInt(textualStride);
    }
}
