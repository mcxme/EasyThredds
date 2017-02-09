package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import com.amazonaws.services.kms.model.UnsupportedOperationException;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;

public class QueryGenerator
{
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    
    // altitude properties
    private static final double FULL_ALTITUDE_PROBABILITY = 0.25;
    private static final int MAX_ALTITUDE_LVL = 100;
    private static final int MAX_ALTITUDE_STRIDE = 10;
    
    // time properties
    private static final DateTime MIN_TIME = new DateTime(1990, 1, 1, 0, 0);
    private static final DateTime MAX_TIME = DateTime.now();
    private static final int MAX_TIME_STRIDE = 100;
    
    // spatial (longitude / latitude) properties
    private static final int MAX_SPATIAL_STRIDE = 10;
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = 0.0;
    private static final double MAX_LONGITUDE = 360.0;
    

    private static final int MIN_DIMS = 1;
    private static final int MAX_DIMS = 4;
    
    // 4D dataset: time, altitude, longitude, latitude
    private static final String TEST_DATASET_4D = "RC1SD-base-08/cloud";
    private static final String[] VARIABLE_4D = new String[] {"aclc", "rainflux", "snowflux"};
    
    // 3D dataset: time, longitude, latitude
    private static final String TEST_DATASET_3D = "RC1SD-base-08/ECHAM5";
    private static final String[] VARIABLE_3D = new String[] {"tsw", "glac", "alake", "sized"};
    
    // 2D dataset: time, altitude
    private static final String TEST_DATASET_2D = "RC1SD-base-08/ECHAM5";
    private static final String[] VARIABLE_2D = new String[] {"dhyam", "dhybm"};
    
    // 1D dataset: time
    private static final String TEST_DATASET_1D = "RC1SD-base-08/orbit";
    private static final String[] VARIABLE_1D = new String[] {"dec", "ra"};

    // avoid instantiation
    private QueryGenerator() {}
    
    public static CollectiveProtocol getRandCollective() {
	
	Random rand = new Random();
	int dims = MIN_DIMS + rand.nextInt(MAX_DIMS - MIN_DIMS + 1);
	String dataset = getDataset(dims);
	String variable = getRandVariable(dims);
	List<String> variables = new ArrayList<>();
	variables.add(variable);
	
	SpatialRange latRange = null;
	SpatialRange lonRange = null;
	NumericRange lvlRange = null;
	TimeRange timeRange = null;
	
	switch (dims) {
	case 4:
	    timeRange = getRandTimeRange();
	    lvlRange = getRandAltitudeRange();
	    latRange = getRandLatitudeRange();
	    lonRange = getRandLongitudeRange();
	case 3:
	    timeRange = getRandTimeRange();
	    latRange = getRandLatitudeRange();
	    lonRange = getRandLongitudeRange();
	    break;
	case 2:
	    timeRange = getRandTimeRange();
	    lvlRange = getRandAltitudeRange();
	    break;
	case 1:
	    timeRange = getRandTimeRange();
	    break;
	    default:
		throw new UnsupportedOperationException("Can only handle 1D, 2D, 3D and 4D");
	}

	return new CollectiveProtocol(THREDDS, dataset, lonRange, latRange, lvlRange, timeRange, variables);
    }
    
    private static String getDataset(int dims) {
	switch (dims) {
	case 1: return TEST_DATASET_1D;
	case 2: return TEST_DATASET_2D;
	case 3: return TEST_DATASET_3D;
	case 4: return TEST_DATASET_4D;
	default:
	    throw new IllegalArgumentException("Only supports dimensions between 1 and 4");
	}
    }
    
    private static String getRandVariable(int dims) {
	String[] vars = null;
	switch (dims) {
	case 1: vars = VARIABLE_1D; break;
	case 2: vars = VARIABLE_2D; break;
	case 3: vars = VARIABLE_3D; break;
	case 4: vars = VARIABLE_4D; break;
	default:
	    throw new IllegalArgumentException("Only supports dimensions between 1 and 4");
	}
	
	Random rand = new Random();
	int i = rand.nextInt(vars.length);
	return vars[i];
    }
    
    private static NumericRange getRandAltitudeRange() {
	Random rand = new Random();
	NumericRange lvlRange;
	
	int stride = Math.max(1, rand.nextInt(MAX_ALTITUDE_STRIDE));
	// full altitude range?
	if (rand.nextDouble() < FULL_ALTITUDE_PROBABILITY) {
	    lvlRange = new NumericRange(0, stride, MAX_ALTITUDE_LVL);
	} else {
	    int start = rand.nextInt(MAX_ALTITUDE_LVL);
	    int end = rand.nextInt(MAX_ALTITUDE_LVL);
	    if (start > end) {
		int tmp = start;
		start = end;
		end = tmp;
	    }
	    
	    lvlRange = new NumericRange(start, stride, end);
	}
	
	return lvlRange;
    }
    
    private static TimeRange getRandTimeRange() {
	Random rand = new Random();
	
	long startTimestamp = MIN_TIME.getMillis();
	long endTimestamp = MAX_TIME.getMillis();
	long randStartOffset = rand.nextLong() % (endTimestamp - startTimestamp);
	long randEndOffset = rand.nextLong() % (endTimestamp - startTimestamp);
	
	DateTime start = new DateTime(randStartOffset);
	DateTime end = new DateTime(randEndOffset);
	if (start.isAfter(end)) {
	    DateTime tmp = start;
	    start = end;
	    end = tmp;
	}
	
	int stride = Math.max(1, rand.nextInt(MAX_TIME_STRIDE));
	
	return new TimeRange(start, end, stride);
    }
    
    private static SpatialRange getRandLatitudeRange() {
	return getRandSpatialRange(MIN_LATITUDE, MAX_LATITUDE);
    }
    
    private static SpatialRange getRandLongitudeRange() {
	return getRandSpatialRange(MIN_LONGITUDE, MAX_LONGITUDE);
    }
    
    private static SpatialRange getRandSpatialRange(double min, double max) {
	Random rand = new Random();
	
	int stride = Math.max(1, rand.nextInt(MAX_SPATIAL_STRIDE));
	double start = min + rand.nextDouble() * (Math.abs(max - min));
	double end = min + rand.nextDouble() * (Math.abs(max - min));
	if (start > end) {
	    double tmp = start;
	    start = end;
	    end = tmp;
	}
	
	return new SpatialRange(start, end, stride);
    }
}
