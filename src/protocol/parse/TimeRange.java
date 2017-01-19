package protocol.parse;

import java.util.Arrays;

import javax.swing.JPopupMenu.Separator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeRange extends Range
{
	public static final String SEPARATOR = ";";
	public static final String RANGE_START = "[";
	public static final String RANGE_END = "]";
	
	private static final String DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";

	private DateTime startTime;
	
	private DateTime endTime;
	
	public TimeRange(DateTime startTime, DateTime endTime, int stride) {
	    super(stride);
		if (startTime == null || endTime == null) {
			throw new IllegalArgumentException("Neither the start nor the end can be null and a valid selection is required");
		} else if (endTime.isBefore(startTime)) {
			throw new IllegalArgumentException("The start time of the time range has to be before the end time");
		} else if (DATE_FORMAT.contains(SEPARATOR) || DATE_FORMAT.contains(RANGE_START) || DATE_FORMAT.contains(RANGE_END)) {
		    throw new IllegalStateException("The time format must not contain range brackets or the variable separator");
		}
		
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public DateTime getStartTime() {
	    return this.startTime;
	}
	
	public DateTime getEndTime() {
		return this.endTime;
	}
	
	@Override
	public String toString() {
	    return RANGE_START + startTime
		    + SEPARATOR + stride
		    + SEPARATOR + endTime
		    + RANGE_END;
	}
	
	/**
	 * Parses the textual representation of a time range.
	 * Input format:
	 *  <-- start time --->  stride <--- end time ----->
	 * [dd/MM/yyyy-HH:mm:ss ;  x   ; dd/MM/yyyy-HH:mm:ss]
	 * 
	 * A default stride is selected if the stride is omitted: [start;;end]
	 * If no end time is provided the end time is set equal to the start time: [start;s;]
	 */
	public static TimeRange parse(String textual) {
		if (!textual.startsWith(RANGE_START) || !textual.endsWith(RANGE_END)) {
			throw new IllegalArgumentException("The time range has to be in brackets: [...]");
		}
		
		String noBrackets = textual.substring(1, textual.length() - 1);
		String[] parts = noBrackets.split(SEPARATOR, -1);

		return parse(parts);
	}
	
	public static TimeRange parse(String[] parts) {
		if (parts.length != 3) {
		    throw new IllegalArgumentException("A time range has to be specified as 3 parts: [s;x;e]");
		}

		DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);		
		DateTime startTime = formatter.parseDateTime(parts[0]);
		DateTime endTime = (parts[2].isEmpty())? startTime : formatter.parseDateTime(parts[2]);
		int stride = parseStride(parts[1]);
		
		return new TimeRange(startTime, endTime, stride);
	}

}
