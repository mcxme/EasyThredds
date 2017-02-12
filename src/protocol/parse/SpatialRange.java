package protocol.parse;

import java.util.Arrays;

public class SpatialRange extends Range
{
	public static final String SEPARATOR = ";";
	public static final String RANGE_START = "[";
	public static final String RANGE_END = "]";
    
    private double startCoordinate;
    private double endCoordinate;
    
    public SpatialRange(double startCoordinate, double endCoordinate) {
	this(startCoordinate, endCoordinate, Range.STD_STRIDE);
    }
    
    public SpatialRange(double startCoordinate, double endCoordinate, int stride)
    {
	super(stride);
	
	if (startCoordinate > endCoordinate) {
	    throw new IllegalArgumentException("The start coordinate is smaller than the end coordinate");
	} else if (stride < 1) {
	    
	}
	
	this.startCoordinate = startCoordinate;
	this.endCoordinate = endCoordinate;
    }
    
    public double getStartCoordinate() {
	return this.startCoordinate;
    }
    
    public double getEndCoordinate() {
	return this.endCoordinate;
    }
    
    @Override
    public boolean isPoint()
    {
	return startCoordinate == endCoordinate;
    }
    
	@Override
	public String toString() {
	    return RANGE_START + startCoordinate
		    + SEPARATOR + stride
		    + SEPARATOR + endCoordinate
		    + RANGE_END;
	}
    
    
	/**
	 * Parses the textual representation of a spatial range.
	 * Input format:
	 * start coordinate | stride | end coordinate
	 * [start           ;    x   ;   end]
	 * 
	 * A default stride is selected if the stride is omitted: [s;;e]
	 * If no end coordinate is provided the end coordinate is set equal to the start coordinate: [s;x;]
	 */
	public static SpatialRange parse(String textual) {
		if (!textual.startsWith(RANGE_START) || !textual.endsWith(RANGE_END)) {
			throw new IllegalArgumentException("The spatial range has to be in brackets: [...]");
		}
		
		String noBrackets = textual.substring(1, textual.length() - 1);
		String[] parts = noBrackets.split(SEPARATOR, -1);
		return parse(parts);
	}
	
	public static SpatialRange parse(String[] parts) {
		if (parts.length != 3) {
		    throw new IllegalArgumentException("A time range has to be specified as 3 parts: [s;x;e]");
		}

		double startCoordinate = Double.parseDouble(parts[0]);
		double endCoordinate = (parts[2].isEmpty())? startCoordinate : Double.parseDouble(parts[2]);
		int stride = parseStride(parts[1]);
		
		return new SpatialRange(startCoordinate, endCoordinate, stride);
	}
    
}
