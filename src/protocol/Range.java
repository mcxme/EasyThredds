package protocol;

public class Range
{
	private int start;
	private int step;
	private int end;
	
	public Range(int start, int step, int end) {
		if (end < start) {
			throw new IllegalArgumentException("The range has to be defined as: start <= end");
		} else if (step < 1) {
			throw new IllegalArgumentException("The step size has to be > 0");
		}
		
		this.start = start;
		this.step = step;
		this.end = end;
	}

	public int getStart() {
		return start;
	}
	
	public int getStep() {
		return step;
	}
	
	public int getEnd() {
		return end;
	}
	
	public int getMax() {
	    return Math.max(start, end);
	}
	
	public int getMin() {
	    return Math.min(start, end);
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
	 * @param textualRange
	 * @return
	 */
	public static Range parse(String textualRange) {
		if (textualRange.length() <= 4) {
			throw new IllegalArgumentException("The textual representation of the range is not long enough to be valid: " + textualRange);
		}
		
		// Remove the brackets
		String trimmedRange = textualRange.substring(1, textualRange.length() - 1);
		String[] vars = trimmedRange.split(":");
		if (vars.length != 3) {
			throw new IllegalArgumentException("A range must contain 3 variables to be valid but had " + vars.length);
		}
		
		int x = Integer.parseInt(vars[0]);
		int y = Integer.parseInt(vars[1]);
		int z = Integer.parseInt(vars[2]);
		
		return new Range(x,y,z);
	}
	
}
