package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;

import com.amazonaws.services.kms.model.UnsupportedOperationException;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.TranslatedProtocol;
import protocol.translated.util.DimensionArray;
import protocol.translated.util.QueryBuilder;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;

/**
 * Randomly generates queries with different selectivity and different cardinality.
 */
public class QueryGenerator
{
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    
    // altitude properties
    private static final double FULL_ALTITUDE_PROBABILITY = 0.25;
    
    // time properties
    private static final int MAX_TIME_STRIDE = 100;
    
    // spatial (longitude / latitude) properties
    private static final int MAX_SPATIAL_STRIDE = 10;
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = 0.0;
    private static final double MAX_LONGITUDE = 360.0;
    

    public static final int MIN_DIMS = 4;
    public static final int MAX_DIMS = 4;
    
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
    
    public static CollectiveProtocol getSingleRandCollective() {
	return getNRandCollectives(1).iterator().next();
    }
    
    public static Set<CollectiveProtocol> getNRandCollectives(int n) {
	Set<CollectiveProtocol> randomCollectives = new HashSet<>(n);
	
	for (int i = 0; i < n; i++) {
	    randomCollectives.add(getRandCollective());
	}
	
	CleanUtil.cleanAll();
	return randomCollectives;
    }
    
    private static DimensionArray downloadData(String dataset, String variable, boolean latLon, boolean lev, boolean time) {
	QueryBuilder query = new QueryBuilder();
	query.add("var", variable);
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, dataset, query.toString());
	if (latLon) {
	    collective.setLatitudeRange(new SpatialRange(0, 1));
	    collective.setLongitudeRange(new SpatialRange(0, 1));
	}
	if (lev) {
	    collective.setHightRange(new NumericRange(1, 2));
	}
	if (time) {
	    collective.setTimeRange(new TimeRange(DateTime.now(), DateTime.now()));
	}
	
	TranslatedProtocol translated = ProtocolPicker.pickByName(Protocol.OpenDap, collective);
	return translated.getDimensionArray();
    }
    
    private static CollectiveProtocol getRandCollective() {
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
	DimensionArray dimData;
	
	switch (dims) {
	case 4:
	    dimData = downloadData(dataset, variable, true, true, true);
	    timeRange = getRandTimeRange(dimData);
	    lvlRange = getRandAltitudeRange(dimData);
	    latRange = getRandLatitudeRange();
	    lonRange = getRandLongitudeRange();
	    break;
	case 3:
	    dimData = downloadData(dataset, variable, true, false, true);
	    timeRange = getRandTimeRange(dimData);
	    latRange = getRandLatitudeRange();
	    lonRange = getRandLongitudeRange();
	    break;
	case 2:
	    dimData = downloadData(dataset, variable, false, true, true);
	    timeRange = getRandTimeRange(dimData);
	    lvlRange = getRandAltitudeRange(dimData);
	    break;
	case 1:
	    dimData = downloadData(dataset, variable, false, false, true);
	    timeRange = getRandTimeRange(dimData);
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
    
    private static NumericRange getRandAltitudeRange(DimensionArray dims) {
	assert (dims.hasAltitudeDimension());
	float minLvl = dims.getAltitudeStart();
	float maxLvl = dims.getAltitudeEnd();
	return getRandAltitudeRange((int)minLvl, (int)maxLvl);
    }
    
    private static NumericRange getRandAltitudeRange(int minLvl, int maxLvl) {
	Random rand = new Random();
	NumericRange lvlRange;
	
	int stride = Math.max(1, rand.nextInt(maxLvl - minLvl));
	// full altitude range?
	if (rand.nextDouble() < FULL_ALTITUDE_PROBABILITY) {
	    lvlRange = new NumericRange(minLvl, stride, maxLvl);
	} else {
	    int start = rand.nextInt(maxLvl - minLvl);
	    int end = rand.nextInt(maxLvl - minLvl);
	    if (start > end) {
		int tmp = start;
		start = end;
		end = tmp;
	    }
	    
	    lvlRange = new NumericRange(minLvl + start, minLvl + end, stride);
	}
	
	return lvlRange;
    }
    
    private static TimeRange getRandTimeRange(DimensionArray dims) {
	assert (dims.hasTimeDimension());
	DateTime minStart = dims.getTimeStart();
	DateTime maxEnd = dims.getTimeEnd();
	return getRandTimeRange(minStart, maxEnd);
    }
    
    private static TimeRange getRandTimeRange(DateTime minStart, DateTime maxEnd) {
	Random rand = new Random();
	long startTimestamp = minStart.getMillis();
	long endTimestamp = maxEnd.getMillis();
	long randStartOffset = Math.abs(rand.nextLong()) % (endTimestamp - startTimestamp);
	long randEndOffset = Math.abs(rand.nextLong()) % (endTimestamp - startTimestamp);
	
	DateTime start = new DateTime(startTimestamp + randStartOffset);
	DateTime end = new DateTime(startTimestamp + randEndOffset);
	if (start.isAfter(end)) {
	    DateTime tmp = start;
	    start = end;
	    end = tmp;
	}
	
	int stride = Math.max(1, rand.nextInt(MAX_TIME_STRIDE));
	assert (start.isAfter(minStart) || start.equals(minStart));
	assert (end.isBefore(endTimestamp) || end.equals(maxEnd));
	
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
