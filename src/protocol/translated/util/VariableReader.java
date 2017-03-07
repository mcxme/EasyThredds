package protocol.translated.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;

/**
 * This class stores for each data set the dimensionality data in order to
 * validate or resolve ranges.
 */
public class VariableReader
{
    // mapping form data set to sttored dimension data
    private Map<String, DimensionArray> datasets;
    
    // singleton
    private static VariableReader instance;

    private VariableReader()
    {
	this.datasets = new HashMap<>();
    }

    public static VariableReader getInstance()
    {
	if (instance == null)
	{
	    instance = new VariableReader();
	}

	return instance;
    }

    public synchronized void close()
    {
	List<Throwable> errors = new LinkedList<>();

	for (DimensionArray dims : datasets.values())
	{
	    try {
		dims.close();
	    } catch (Throwable t) {
		errors.add(t);
	    }
	}

	if (!errors.isEmpty())
	{
	    // if there was any error return the first one
	    throw new IllegalStateException(errors.get(0));
	}
    }

    public synchronized void clear()
    {
	datasets.clear();
    }

    /**
     * Check whether the data set is already present.
     */
    public synchronized boolean hasDataset(String datasetBaseUrl)
    {
	return datasets.containsKey(datasetBaseUrl);
    }

    /**
     * Adds the data set with the given dimensionality data.
     */
    public synchronized void addDataset(String datasetBaseUrl, DimensionArray dims)
    {
	if (hasDataset(datasetBaseUrl))
	    throw new IllegalArgumentException("Already stored data for the given dataset");
	if (datasetBaseUrl.contains("?"))
	    throw new IllegalArgumentException("The dataset must be provided without a query");

	datasets.put(datasetBaseUrl, dims);
    }
    
    /**
     * Retrieves dimensionality data for the given dataset.
     */
    public synchronized DimensionArray getDataset(String datasetBaseUrl) {
	if (datasetBaseUrl.contains("?"))
	    throw new IllegalArgumentException("The dataset must be provided without a query");

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	
	return dims;
    }

    /**
     * Resolves the absolute longitude range into an index range.
     */
    public synchronized NumericRange getLongitudeIndexRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	DimensionArray dims = getDataset(datasetBaseUrl);
	if (!dims.hasLongitudeDimension()) {
	    throw new IllegalStateException("The dataset does not have a longitude dimension");
	}

	float[] lonData = dims.getLongitudeData();
	return VariableIndexUtil.getIndexRange(valueRange, lonData);
    }

    /**
     * Checks whether the given longitude range comprises the entire index range
     * (all values).
     */
    public synchronized boolean isFullLongitudeRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	DimensionArray dims = getDataset(datasetBaseUrl);
	if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a longitude dimension");
	}

	float[] lonData = dims.getLongitudeData();
	// the given absolute value range comprises the available value range if
	// the first value and the last value are outside of the available one
	return valueRange.getStartCoordinate() <= lonData[0] && valueRange.getEndCoordinate() >= lonData[lonData.length - 1];
    }

    /**
     * Resolves the absolute latitude range into an index range.
     */
    public synchronized NumericRange getLatitudeIndexRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	DimensionArray dims = getDataset(datasetBaseUrl);
	if (!dims.hasLongitudeDimension()) {
	    throw new IllegalStateException("The dataset does not have a latitude dimension");
	}

	float[] latData = dims.getLatitudeData();
	NumericRange indexRange = VariableIndexUtil.getIndexRange(valueRange, latData);
	return indexRange;
    }
    
    /**
     * Checks whether the given latitude range comprises the entire index range
     * (all values).
     */
    public synchronized boolean isFullLatitudeRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	DimensionArray dims = getDataset(datasetBaseUrl);
	if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a latitude dimension");
	}

	float[] latData = dims.getLatitudeData();
	// the given absolute value range comprises the available value range if
	// the first value and the last value are outside of the available one
	return valueRange.getStartCoordinate() <= latData[0] && valueRange.getEndCoordinate() >= latData[latData.length - 1];
    }

    /**
     * Resolves the absolute time range into an index range.
     */
    public synchronized NumericRange getTimeIndexRange(String datasetBaseUrl, TimeRange valueRange)
    {
	DimensionArray dims = getDataset(datasetBaseUrl);
	if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a  dimension");
	}

	double[] timeData = dims.getTimeData();
	return VariableIndexUtil.getIndexRange(valueRange, timeData);
    }

    /**
     * Resolves the absolute numerical range into an index range.
     */
    public synchronized NumericRange getAltitudeIndexRange(String datasetBaseUrl, NumericRange valueRange)
    {
	DimensionArray dims = getDataset(datasetBaseUrl);
	if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have an altitude dimension");
	}

	float[] lvlData = dims.getAltitudeData();
	return VariableIndexUtil.getIndexRange(valueRange, lvlData);
    }
    
    /**
     * Checks whether the given altitude range comprises the entire index range
     * (all values).
     */
    public synchronized boolean isFullAltitudeRange(String datasetBaseUrl, NumericRange valueRange)
    {
	DimensionArray dims = getDataset(datasetBaseUrl);
	if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have an altitude dimension");
	}

	float[] lvlData = dims.getAltitudeData();
	// the given absolute value range comprises the available value range if
	// the first value and the last value are outside of the available one
	return valueRange.getStart().floatValue() <= lvlData[0]
		&& valueRange.getEnd().floatValue() >= lvlData[lvlData.length - 1];
    }

    /**
     * Checks whether the given altitude range touches only a single altitude
     * level (comprises a single index).
     */
    public boolean isSingleAltitudeLevel(String datasetBaseUrl, NumericRange valueRange)
    {
	NumericRange indexRange = getAltitudeIndexRange(datasetBaseUrl, valueRange);
	return indexRange.isPoint();
    }

    /**
     * Given the query the (estimated) number of selected items based on the
     * selectivity of each dimension is returned.
     */
    public synchronized long getEstimatedQuerySelectionItems(CollectiveProtocol protocol) {
	String datasetBaseUrl = protocol.getDataset();
	long nItems = 1;
	if (protocol.hasLatitudeRange()) {
	    NumericRange latRange = getLatitudeIndexRange(datasetBaseUrl, protocol.getLatitudeRange());
	    nItems *= latRange.getIntExtent();
	}
	
	if (protocol.hasLongitudeRange()) {
	    NumericRange lonRange = getLongitudeIndexRange(datasetBaseUrl, protocol.getLongitudeRange());
	    nItems *= lonRange.getIntExtent();
	}
	
	if (protocol.hasHightRange()) {
	    NumericRange lvlRange = getAltitudeIndexRange(datasetBaseUrl, protocol.getHightRange());
	    nItems *= lvlRange.getIntExtent();
	}
	
	if (protocol.hasTimeRangeDefined()) {
	    NumericRange timeRange = getTimeIndexRange(datasetBaseUrl, protocol.getTimeRange());
	    nItems *= timeRange.getIntExtent();
	}
	
	return nItems;
    }
}
