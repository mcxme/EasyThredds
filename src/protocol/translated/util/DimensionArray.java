package protocol.translated.util;

import protocol.reader.IReader;

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

    public double[] getTimeData()
    {
	return timeData;
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
