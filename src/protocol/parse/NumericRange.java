package protocol.parse;

/**
 * This class is used for simple numeric ranges such as those for altitude level subsetting.
 */
public class NumericRange extends Range
{
	public static final int STD_START = 0;
	
	private Number start;
	private Number end;
	
	public NumericRange(Number start, Number end) {
	    this(start, end, Range.STD_STRIDE);
	}
	
	public NumericRange(Number start, Number end, int stride) {
	    super(stride);
		if (end.doubleValue() < start.doubleValue()) {
			throw new IllegalArgumentException("The range has to be defined as: start <= end");
		}
		
		this.start = start;
		this.end = end;
	}
	
	public Number getStart() {
		return start;
	}
	
	public Number getEnd() {
		return end;
	}
	
	public int getIntExtent() {
	    return end.intValue() - start.intValue();
	}
	
	/**
	 * Textual representation of the range in the format [start:step:end]
	 */
	@Override
	public String toString() {
	    return "[" + start + ":" + stride + ":" + end + "]";
	}
	
	@Override
	public boolean isPoint()
	{
	    return start.equals(end);
	}
	
	/**
	 * Parses a range in the format '[x:y:z]' where x <= z define the bounds of the range and y the step size:
	 * [x, x + y, x + 2y, ..., z]
	 * 
	 * If the stride is omitted a default stride is assumed.
	 * If the end is omitted it is set to the start: [x;y;] = [x;y;x]
	 */
	public static NumericRange parse(String textualRange) {
		if (textualRange.length() <= 4) {
			throw new IllegalArgumentException("The textual representation of the range is not long enough to be valid: " + textualRange);
		}
		
		// Remove the brackets
		String trimmedRange = textualRange.substring(1, textualRange.length() - 1);
		String[] vars = trimmedRange.split(":", -1);
		return parse(vars);
	}
	
	/**
	 * see {@link protocol.parse.NumericRange#parse(String)}
	 */
	public static NumericRange parse(String[] vars) {
		if (vars.length != 3) {
			throw new IllegalArgumentException("A range must contain 3 variables to be valid but had " + vars.length);
		}
		
		Number x,y;
		// can both values be represented as integer or as float?
		if ((vars[0].isEmpty() || isInteger(vars[0]))
			&& (vars[2].isEmpty() || isInteger(vars[2]))) {
		    x = (vars[0].isEmpty())? STD_START : Integer.parseInt(vars[0]);
		    y = (vars[2].isEmpty())? x : Integer.parseInt(vars[2]);
		} else {
		    x = (vars[0].isEmpty())? new Integer(STD_START).doubleValue() : Double.parseDouble(vars[0]);
		    y = (vars[2].isEmpty())? x : Double.parseDouble(vars[2]);
		}
		
		int stride = parseStride(vars[1]);
		return new NumericRange(x,y,stride);
	}
	
	private static boolean isInteger(String textualNumeric) {
	    try {
		Integer.parseInt(textualNumeric);
		return true;
	    } catch (NumberFormatException e) {
		return false;
	    }
	}
}
