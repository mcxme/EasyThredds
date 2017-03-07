package protocol.translated.util;

import org.joda.time.DateTime;

import protocol.reader.IReader;

/**
 * This wrapper class stores coordinate (dimension) data.
 * It takes readers for each dimensions and returns raw data.
 */
public class DimensionArray
{
    private static final String LATITUDE_VARIABLE = "lat";
    private static final String LONGITUDE_VARIABLE = "lon";
    private static final String ALTITUDE_VARIABLE = "lev";
    private static final String TIME_VARIABLE = "time";

    private float[] latData;
    private float[] lonData;
    private float[] lvlData;
    private double[] timeData;
    
    private IReader latReader;
    private IReader lonReader;
    private IReader levReader;
    private IReader timeReader;

    public DimensionArray(IReader reader)
    {
	if (reader.hasVariableWithName(LATITUDE_VARIABLE))
	    latData = reader.readFloatArray(LATITUDE_VARIABLE);
	if (reader.hasVariableWithName(LONGITUDE_VARIABLE))
	    lonData = reader.readFloatArray(LONGITUDE_VARIABLE);
	if (reader.hasVariableWithName(ALTITUDE_VARIABLE))
	    lvlData = reader.readFloatArray(ALTITUDE_VARIABLE);
	if (reader.hasVariableWithName(TIME_VARIABLE))
	    timeData = reader.readDoubleArray(TIME_VARIABLE);
	
	latReader = reader;
	lonReader = reader;
	levReader = reader;
	timeReader = reader;
    }
    
    public DimensionArray(IReader latReader, IReader lonReader, IReader lvlReader, IReader timeReader) {
	if (latReader != null && latReader.hasVariableWithName(LATITUDE_VARIABLE)) {
	    latData = latReader.readFloatArray(LATITUDE_VARIABLE);
	}
	
	if (lonReader != null && lonReader.hasVariableWithName(LONGITUDE_VARIABLE)) {
	    lonData = lonReader.readFloatArray(LONGITUDE_VARIABLE);
	}
	
	if (lvlReader != null && lvlReader.hasVariableWithName(ALTITUDE_VARIABLE)) {
	    lvlData = lvlReader.readFloatArray(ALTITUDE_VARIABLE);
	}
	
	if (timeReader != null && timeReader.hasVariableWithName(TIME_VARIABLE)) {
	    timeData = timeReader.readDoubleArray(TIME_VARIABLE);
	}
	
	this.latReader = latReader;
	this.lonReader = lonReader;
	this.levReader = lvlReader;
	this.timeReader = timeReader;
    }

    public void close() throws Exception {
	if (latReader != null) {
	    latReader.close();
	}
	if (lonReader != null && lonReader != latReader) {
	    lonReader.close();
	}
	if (levReader != null && levReader != lonReader && levReader != latReader) {
	    levReader.close();
	}
	if (timeReader != null && timeReader != levReader && timeReader != lonReader && timeReader != latReader) {
	    timeReader.close();
	}
    }
    
    public float[] getLongitudeData()
    {
	return lonData;
    }

    public float[] getLatitudeData()
    {
	return latData;
    }

    public float[] getAltitudeData()
    {
	return lvlData;
    }

    public float getAltitudeStart() {
	if (!hasAltitudeDimension()) {
	    throw new IllegalStateException("does not have an altitude dimension");
	}
	
	return lvlData[0];
    }

    public float getAltitudeEnd() {
	if (!hasAltitudeDimension()) {
	    throw new IllegalStateException("does not have an altitude dimension");
	}
	
	return lvlData[lvlData.length - 1];
    }

    public double[] getTimeData()
    {
	return timeData;
    }

    public DateTime getTimeStart() {
	if (!hasTimeDimension()) {
	    throw new IllegalStateException("does not have a time dimension");
	}
	
	return VariableIndexUtil.timeFromFloatSince1950(timeData[0]);
    }
    
    public DateTime getTimeEnd() {
	if (!hasTimeDimension()) {
	    throw new IllegalStateException("does not have a time dimension");
	}
	
	return VariableIndexUtil.timeFromFloatSince1950(timeData[timeData.length - 1]);
    }
    
    public boolean hasTimeDimension()
    {
	return this.timeData != null;
    }

    public boolean hasLongitudeDimension()
    {
	return this.lonData != null;
    }

    public boolean hasLatitudeDimension()
    {
	return this.latData != null;
    }

    public boolean hasAltitudeDimension()
    {
	return this.lvlData != null;
    }
}
