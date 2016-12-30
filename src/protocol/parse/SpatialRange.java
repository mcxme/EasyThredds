package protocol.parse;

import java.util.Arrays;

public class SpatialRange implements Range
{
	public static final String SEPARATOR = ";";
	public static final String RANGE_START = "[";
	public static final String RANGE_END = "]";
    
    private double startCoordinate;
    private double endCoordinate;
    private NumericRange selection;
    
    public SpatialRange(double startCoordinate, double endCoordinate, NumericRange selection)
    {
	if (selection == null) {
	    throw new IllegalArgumentException("A selection is required");
	} else if (startCoordinate > endCoordinate) {
	    throw new IllegalArgumentException("The start coordinate is smaller than the end coordinate");
	}
	
	this.startCoordinate = startCoordinate;
	this.endCoordinate = endCoordinate;
	this.selection = selection;
    }
    
    public double getStartCoordinate() {
	return this.startCoordinate;
    }
    
    public double getEndCoordinate() {
	return this.endCoordinate;
    }
    
    public int getStride() {
	return this.selection.getStride();
    }
    
	@Override
	public NumericRange getSelection()
	{
		return selection;
	}
    
	/**
	 * Parses the textual representation of a spatial range.
	 * Input format:
	 * start coordinate | start index | stride | end index | end coordinate
	 * [sC ;  sI  ;    x    ;  eI ; eC]
	 * 
	 * A default stride is selected if the stride is omitted: [sC;sI;;eI;eC]
	 * If no end time is provided the end time is set equal to the start time: [sC;sI;;eI;]
	 * If no end point is provided the end point index is set equal to the start point index: [sC;sI;;;]
	 * If no start index is provided zero is assumed: [sC;;;;]
	 * If neither a start index nor an end index are provided the according separators can be omitted: [sC;x;eC]
	 */
	public static SpatialRange parse(String textual) {
		if (!textual.startsWith(RANGE_START) || !textual.endsWith(RANGE_END)) {
			throw new IllegalArgumentException("The spatial range has to be in brackets: [...]");
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
	
	public static SpatialRange parse(String[] parts) {
		if (parts.length != 5) {
		    throw new IllegalArgumentException("A time range has to be specified as 5 parts: [s;i;x;j;e]");
		}

		double startCoordinate = Double.parseDouble(parts[0]);
		double endCoordinate = (parts[4].isEmpty())? startCoordinate : Double.parseDouble(parts[4]);
		
		String[] selectionSubParts = Arrays.copyOfRange(parts, 1, 4);
		NumericRange selection = NumericRange.parse(selectionSubParts);
		
		return new SpatialRange(startCoordinate, endCoordinate, selection);
	}
    
}
