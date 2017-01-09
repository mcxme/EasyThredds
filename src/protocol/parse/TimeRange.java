package protocol.parse;

import java.util.Arrays;

import javax.swing.JPopupMenu.Separator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeRange implements Range
{
	public static final String SEPARATOR = ";";
	public static final String RANGE_START = "[";
	public static final String RANGE_END = "]";
	
	private static final String DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";

	private DateTime startTime;
	
	private DateTime endTime;
	
	private NumericRange selection;
	
	public TimeRange(DateTime startTime, DateTime endTime, NumericRange selection) {
		if (startTime == null || endTime == null || selection == null) {
			throw new IllegalArgumentException("Neither the start nor the end can be null and a valid selection is required");
		} else if (endTime.isBefore(startTime)) {
			throw new IllegalArgumentException("The start time of the time range has to be before the end time");
		} else if (DATE_FORMAT.contains(SEPARATOR) || DATE_FORMAT.contains(RANGE_START) || DATE_FORMAT.contains(RANGE_END)) {
		    throw new IllegalStateException("The time format must not contain range brackets or the variable separator");
		}
		
		this.startTime = startTime;
		this.endTime = endTime;
		this.selection = selection;
	}
	
	public DateTime getStartTime() {
	    return this.startTime;
	}
	
	public int getStartIndex() {
	    return this.selection.getStart().intValue();
	}
	
	public DateTime getEndTime() {
		return this.endTime;
	}
	
	public int getEndPoint() {
	    return this.selection.getEnd().intValue();
	}

	public int getStride() {
		return this.selection.getStride();
	}
	
	@Override
	public NumericRange getSelection()
	{
    		return selection;
	}
	
	@Override
	public String toString() {
	    return RANGE_START + startTime
		    + SEPARATOR + selection.getStart()
		    + SEPARATOR + selection.getStride()
		    + SEPARATOR + selection.getEnd()
		    + SEPARATOR + endTime
		    + RANGE_END;
	}
	
	/**
	 * Parses the textual representation of a time range.
	 * Input format:
	 *  <-- start time ---> start | stride | end  <--- end time ----->
	 * [dd/MM/yyyy-HH:mm:ss ;  s  ;    x    ;  e ; dd/MM/yyyy-HH:mm:ss]
	 * 
	 * A default stride is selected if the stride is omitted: [start;s;;e;end]
	 * If no end time is provided the end time is set equal to the start time: [start;s;;e;]
	 * If no end point is provided the end point index is set equal to the start point index: [start;s;;;]
	 * If no start point index is provided zero is assumed: [start;;;;]
	 * If neither a start index nor an end index are provided the according separators can be omitted: [start;x;end]
	 */
	public static TimeRange parse(String textual) {
		if (!textual.startsWith(RANGE_START) || !textual.endsWith(RANGE_END)) {
			throw new IllegalArgumentException("The time range has to be in brackets: [...]");
		}
		
		String noBrackets = textual.substring(1, textual.length() - 1);
		String[] parts = noBrackets.split(SEPARATOR, -1);
		if (parts.length == 3) {
		    String[] noBracketsExtended = new String[5];
		    Arrays.fill(noBracketsExtended, "");
		    noBracketsExtended[0] = parts[0];
		    noBracketsExtended[2] = parts[1];
		    noBracketsExtended[4] = parts[2];
		    parts = noBracketsExtended;
		}

		return parse(parts);
	}
	
	public static TimeRange parse(String[] parts) {
		if (parts.length != 5) {
		    throw new IllegalArgumentException("A time range has to be specified as 5 parts: [s;x;y;z;e]");
		}

		DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);		
		DateTime startTime = formatter.parseDateTime(parts[0]);
		DateTime endTime = (parts[4].isEmpty())? startTime : formatter.parseDateTime(parts[4]);
		
		String[] selectionSubParts = Arrays.copyOfRange(parts, 1, 4);
		NumericRange selection = NumericRange.parse(selectionSubParts);
		
		return new TimeRange(startTime, endTime, selection);
	}

}
