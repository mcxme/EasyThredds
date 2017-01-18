package protocol.translated.util;

import java.io.IOException;

import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;

public class VariableIndexUtil
{
    private VariableIndexUtil() {}
    
    public static NumericRange getIndexRange(SpatialRange range, float[] values) {
	int startIndex = findIndex(values, (float)range.getStartCoordinate());
	int endIndex = findIndex(values, (float)range.getEndCoordinate());
	return new NumericRange(startIndex, range.getStride(), endIndex);
    }
    
    public static NumericRange getIndexRange(NumericRange range, float[] values) {
	int startIndex = findIndex(values, range.getStart().floatValue());
	int endIndex = findIndex(values, range.getEnd().floatValue());
	return new NumericRange(startIndex, range.getStride(), endIndex);
    }
    
    public static NumericRange getIndexRange(TimeRange range, long[] values) {
	// TODO
	return null;
    }
    
    private static int findIndex(float[] array, float value) {
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
    
    private static int findIndex(long[] array, long value) {
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
