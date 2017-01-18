package protocol.translated.util;

import reader.IReader;

public class DimensionArray
{
    private static final String LATITUDE_VARIABLE = "lat";
    private static final String LONGITUDE_VARIABLE = "lon";
    private static final String ALTITUDE_VARIABLE = "lev";
    private static final String TIME_VARIABLE = "time";
    
    private float[] latData;
    private float[] lonData;
    private float[] lvlData;
    private long[] timeData;
    
    public DimensionArray(IReader reader) {
	latData = reader.readFloatArray(LATITUDE_VARIABLE);
	lonData = reader.readFloatArray(LONGITUDE_VARIABLE);
	lvlData = reader.readFloatArray(ALTITUDE_VARIABLE);
	timeData = reader.readLongArray(TIME_VARIABLE);
    }
    
    public float[] getLongitudeData() {
	return lonData;
    }

    public float[] getLatitudeData() {
	return latData;
    }
    
    public float[] getAltitudeData() {
	return lvlData;
    }

    public long[] getTimeData() {
	return timeData;
    }
    
    public boolean hasTimeDimension() {
	return this.timeData != null;
    }
    
    public boolean hasLongitudeDimension() {
	return this.lonData != null;
    }
    
    public boolean hasLatitudeDimension() {
	return this.latData != null;
    }
    
    public boolean hasAltitudeDimension() {
	return this.lvlData != null;
    }
}
