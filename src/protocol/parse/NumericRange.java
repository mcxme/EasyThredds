package protocol.parse;

public class NumericRange implements Range
{
	public static final int STD_STRIDE = 1;
	public static final int STD_START = 0;
	
	private Number start;
	private int step;
	private Number end;
	
	public NumericRange(Number start, int step, Number end) {
		if (end.doubleValue() < start.doubleValue()) {
			throw new IllegalArgumentException("The range has to be defined as: start <= end");
		} else if (step < 1) {
			throw new IllegalArgumentException("The step size has to be > 0");
		}
		
		this.start = start;
		this.step = step;
		this.end = end;
	}
	
	/**
	 * Returns true if the range comprises a single value only 
	 */
	public boolean isSingleValue() {
	    return start.equals(end);
	}

	public Number getStart() {
		return start;
	}
	
	public int getStride() {
		return step;
	}
	
	public Number getEnd() {
		return end;
	}
	
	public boolean hasDefaultStride() {
	    return this.step == STD_STRIDE;
	}
	
	@Override
	public NumericRange getSelection()
	{
    		return this;
	}
	
	/**
	 * Textual representation of the range in the format [start:step:end]
	 */
	@Override
	public String toString() {
	    return "[" + start + ":" + step + ":" + end + "]";
	}
	
	/**
	 * Parses a range in the format '[x:y:z]' where x<z define the bounds of the range and y the step size:
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
	
	public static NumericRange parse(String[] vars) {
		if (vars.length != 3) {
			throw new IllegalArgumentException("A range must contain 3 variables to be valid but had " + vars.length);
		}
		
		int y = (vars[1].isEmpty())? STD_STRIDE : Integer.parseInt(vars[1]);
		Number x,z;
		// can both values be represented as integer or as float?
		if ((vars[0].isEmpty() || isInteger(vars[0]))
			&& (vars[2].isEmpty() || isInteger(vars[2]))) {
		    x = (vars[0].isEmpty())? STD_START : Integer.parseInt(vars[0]);
		    z = (vars[2].isEmpty())? x : Integer.parseInt(vars[2]);
		} else {
		    x = (vars[0].isEmpty())? new Integer(STD_START).doubleValue() : Double.parseDouble(vars[0]);
		    z = (vars[2].isEmpty())? x : Double.parseDouble(vars[2]);
		}
		
		return new NumericRange(x,y,z);
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
