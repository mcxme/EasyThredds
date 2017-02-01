package protocol.parse;

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
    
    protected static int parseStride(String textualStride) {
	return (textualStride.isEmpty() || textualStride == null)?
		STD_STRIDE
		: Integer.parseInt(textualStride);
    }
}
