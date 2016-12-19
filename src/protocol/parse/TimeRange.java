package protocol.parse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeRange
{
	public static final String SEPARATOR = ";";
	private static final String DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";
	private static final int STD_STRIDE = 1;

	private DateTime start;
	
	private DateTime end;
	
	/**
	 * Take every n-th element in the time range
	 */
	private int stride;
	
	public TimeRange(DateTime start, DateTime end, int stride) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Neither the start nor the end can be null");
		} else if (end.isBefore(start)) {
			throw new IllegalArgumentException("The start time of the time range has to be before the end time");
		}
		
		this.start = start;
		this.end = end;
		this.stride = stride;
	}
	
	public DateTime getStart() {
	    return this.start;
	}
	
	public DateTime getEnd() {
		return this.end;
	}

	public int getStride() {
		return this.stride;
	}
	
	/**
	 * Parses the textual representation of a time range.
	 * Input format:
	 *  <---- start -----> stride <---- end ----->
	 * [dd/MM/yyyy-HH:mm:ss ; x ; dd/MM/yyyy-HH:mm:ss]
	 * 
	 * A default stride is selected if the stride is omitted: [start;;end]
	 */
	public static TimeRange parse(String textual) {
		if (!textual.startsWith("[") || !textual.endsWith("]")) {
			throw new IllegalArgumentException("The time range has to be in brackets: [...]");
		}
		
		String noBrackets = textual.substring(1, textual.length() - 1);
		String[] parts = noBrackets.split(SEPARATOR);
		if (parts.length != 3) {
			throw new IllegalArgumentException("A time range has to be specified as 3 parts: [x;y;z]");
		}
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);
		DateTime start = formatter.parseDateTime(parts[0]);
		DateTime end = formatter.parseDateTime(parts[2]);
		int stride = (parts[1].isEmpty())? STD_STRIDE : Integer.parseInt(parts[1]);
		
		return new TimeRange(start, end, stride);
	}

}
