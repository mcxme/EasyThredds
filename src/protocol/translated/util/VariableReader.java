package protocol.translated.util;

import java.util.HashMap;
import java.util.Map;

import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import reader.IReader;
import ucar.nc2.NetcdfFile;

public class VariableReader
{
    private Map<String, DimensionArray> datasets;
    private static VariableReader instance;
    
    private VariableReader() {
	this.datasets = new HashMap<>();
    }
    
    public static VariableReader getInstance() {
	if (instance == null) {
	    instance = new VariableReader();
	}
	
	return instance;
    }
    
    public synchronized void clear() {
	datasets.clear();
    }

    public synchronized boolean hasDataset(String datasetBaseUrl) {
	return datasets.containsKey(datasetBaseUrl);
    }
    
    public synchronized void addDataset(String datasetBaseUrl, DimensionArray dims) {
	if (hasDataset(datasetBaseUrl))
	    throw new IllegalArgumentException("Already stored data for the given dataset");
	
	datasets.put(datasetBaseUrl, dims);
    }
    
    public synchronized NumericRange getLongitudeIndexRange(String datasetBaseUrl, SpatialRange valueRange) {
	if (datasetBaseUrl.contains("?")) {
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}
	
	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null) {
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension()) {
	    throw new IllegalStateException("The dataset does not have a longitude dimension");
	}
	
	float[] lonData = dims.getLongitudeData();
	return VariableIndexUtil.getIndexRange(valueRange, lonData);
    }
    
    public synchronized NumericRange getLatitudeIndexRange(String datasetBaseUrl, SpatialRange valueRange) {
	if (datasetBaseUrl.contains("?")) {
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}
	
	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null) {
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension()) {
	    throw new IllegalStateException("The dataset does not have a latitude dimension");
	}
	
	float[] latData = dims.getLatitudeData();
	NumericRange indexRange = VariableIndexUtil.getIndexRange(valueRange, latData);
	return indexRange;
    }

    public synchronized NumericRange getTimeIndexRange(String datasetBaseUrl, TimeRange valueRange) {
	if (datasetBaseUrl.contains("?")) {
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}
	
	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null) {
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension()) {
	    throw new IllegalStateException("The dataset does not have a  dimension");
	}
	
	double[] timeData = dims.getTimeData();
	return VariableIndexUtil.getIndexRange(valueRange, timeData);
    }
    
    public synchronized NumericRange getAltitudeIndexRange(String datasetBaseUrl, NumericRange valueRange) {
	if (datasetBaseUrl.contains("?")) {
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}
	
	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null) {
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension()) {
	    throw new IllegalStateException("The dataset does not have an altitude dimension");
	}
	
	float[] lvlData = dims.getAltitudeData();
	return VariableIndexUtil.getIndexRange(valueRange, lvlData);
    }
}
