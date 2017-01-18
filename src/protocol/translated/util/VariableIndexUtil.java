package protocol.translated.util;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.Seconds;

import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;

public class VariableIndexUtil
{
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;
    
    
    private VariableIndexUtil() {}
    
    public static NumericRange getIndexRange(SpatialRange range, float[] values) {
	float startCoordinate = (float) range.getStartCoordinate();
	float endCoordinate = (float) range.getEndCoordinate();
	
	int startIndex = findIndex(values, startCoordinate);
	int endIndex = findIndex(values, endCoordinate);
	if (endIndex >= values.length) endIndex -= 1;
	
	if (startIndex >= values.length
		|| (endIndex == 0 && values[0] > endCoordinate)) {
	    throw new IllegalArgumentException("The given spatial range ("
		+ range
		+ ") is outside of the actual data range (["
		+ values[0] + ";;"
		+ values[values.length - 1] + "])");
	}
	
	return new NumericRange(startIndex, range.getStride(), endIndex);
    }
    
    public static NumericRange getIndexRange(NumericRange range, float[] values) {
	int startIndex = findIndex(values, range.getStart().floatValue());
	int endIndex = findIndex(values, range.getEnd().floatValue());
	if (endIndex >= values.length) endIndex -= 1;
	
	if (startIndex >= values.length
		|| (endIndex == 0 && values[0] > range.getEnd().floatValue())) {
	    throw new IllegalArgumentException("The given numeric range ("
		+ range
		+ ") is outside of the actual data range (["
		+ values[0] + ";;"
		+ values[values.length - 1] + "])");
	}
	
	
	return new NumericRange(startIndex, range.getStride(), endIndex);
    }
    
    public static NumericRange getIndexRange(TimeRange range, double[] values) {
	double convertedStart = timeToFloatSince1950(range.getStartTime());
	int startIndex = findIndex(values, convertedStart);
	double convertedEnd = timeToFloatSince1950(range.getEndTime());
	int endIndex = findIndex(values, convertedEnd);
	if (endIndex >= values.length) endIndex -= 1;
	
	if (startIndex >= values.length
		|| (endIndex == 0 && values[0] > convertedEnd)) {
	    throw new IllegalArgumentException("The given time range (["
		+ convertedStart + ";;"
		+ convertedEnd
		+ "]) is outside of the actual data range (["
		+ values[0] + ";;"
		+ values[values.length - 1] + "])");
	}
	
	return new NumericRange(startIndex, range.getStride(), endIndex);
    }
    
    public static DateTime timeFromFloatSince1950(double value) {
	int additionalDays = (int)value;
	double dayFraction = value - additionalDays;
	assert (dayFraction <= 1.0);
	int additionalSeconds = (int) (SECONDS_PER_DAY * dayFraction);
	
	DateTime converted =
		// the baseline is 1950-01-01 00:00:00
		new DateTime(1950, 1, 1, 0, 0, 0)
		// add the days since
		.plusDays(additionalDays)
		// add the remaining day fraction as seconds
		.plusSeconds(additionalSeconds);
	
	return converted;
    }
    
    public static double timeToFloatSince1950(DateTime dateTime) {
	DateTime baseline = new DateTime(1950, 1, 1, 0, 0, 0);
	int days = Days.daysBetween(baseline, dateTime).getDays();
	int seconds = Seconds.secondsBetween(baseline.plusDays(days), dateTime).getSeconds();
	assert (seconds <= SECONDS_PER_DAY);
	return (double) days + (double) seconds / SECONDS_PER_DAY;
    }
    
    private static int findIndex(float[] array, float value) {
	if (array.length == 0) {
	    throw new IllegalArgumentException("Empty array");
	}
	
	int startIndex = 0;
	int endIndex = array.length;
	while (startIndex < endIndex) {
	    int middleIndex = (startIndex + endIndex) / 2;
	    double middleValue = array[middleIndex];
	    if (value < middleValue) {
		endIndex = middleIndex;
	    } else if (value > middleValue) {
		startIndex = middleIndex + 1;
	    } else {
		startIndex = middleIndex;
		endIndex = middleIndex;
	    }
	}
	
	assert (startIndex == endIndex);
	return startIndex;
    }
    
    private static int findIndex(double[] array, double value) {
	int startIndex = 0;
	int endIndex = array.length;
	while (startIndex < endIndex) {
	    int middleIndex = (startIndex + endIndex) / 2;
	    double middleValue = array[middleIndex];
	    if (value < middleValue) {
		endIndex = middleIndex;
	    } else if (value > middleValue) {
		startIndex = middleIndex + 1;
	    } else {
		startIndex = middleIndex;
		endIndex = middleIndex;
	    }
	}
	
	return startIndex;
    }
}
