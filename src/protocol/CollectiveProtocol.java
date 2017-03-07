package protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.QueryBuilder;

/**
 * This class specifies the custom protocol in which the user describes the
 * queries. It can be translated into other supported protocols ({@link
 * protocol.translated.TranslatedProtocol}).
 */
public class CollectiveProtocol extends Protocol
{
    // Variable names
    public static final String LATITUDE_VARIABLE = "lat";
    public static final String LONGITUDE_VARIABLE = "lon";
    public static final String HIGHT_VARIABLE = "lev";
    public static final String VAR_VARIABLE = "var";
    public static final String TIME_VARIABLE = "time";

    // Subsetting and variable definitions
    private SpatialRange latRange;
    private SpatialRange lonRange;
    private NumericRange hightRange;
    private TimeRange timeRange;
    private List<String> variables;

    /**
     * Use this constructor to instantiate and parse the given query holding
     * subset and variable definitions
     */
    public CollectiveProtocol(String baseUrl, String dataset, String query)
    {
	super(dataset, baseUrl);
	this.variables = new ArrayList<String>();
	if (query != null)
	{
	    parseInput(query);
	}
    }

    /**
     * Use this constructor to instantiate using known (already parsed) subsets.
     */
    public CollectiveProtocol(String baseUrl, String dataset, SpatialRange lonRange, SpatialRange latRange, NumericRange lvlRange, TimeRange timeRange, List<String> variables)
    {
	super(dataset, baseUrl);
	this.variables = variables;
	this.latRange = latRange;
	this.lonRange = lonRange;
	this.timeRange = timeRange;
	this.hightRange = lvlRange;
    }

    private void parseInput(String query)
    {
	// input format:
	// foo=[x:y:z]&var=a,b,c where
	// foo indicates a variable in {lat, lon, lev, time}
	// and [x:y:z] is the range [x, x + y, x + 2y, ..., z]
	// fields are separated by an &

	// break down the query and parse each part individually
	String[] varParts = query.split("&");
	for (int i = 0; i < varParts.length; i++)
	{
	    parseDefinition(varParts[i]);
	}

	if (!hasVariablesDefined())
	{
	    throw new IllegalArgumentException("No variables defined");
	}
    }

    private void parseDefinition(String definition)
    {
	String[] def = definition.split("=");
	if (def.length != 2)
	{
	    throw new IllegalArgumentException("The definition is not well-formed: " + definition);
	}

	// delegate the parsing for each individual subset range to the according class
	String variable = def[0];
	switch (variable)
	{
	case LATITUDE_VARIABLE:
	    latRange = SpatialRange.parse(def[1]);
	    break;
	case LONGITUDE_VARIABLE:
	    lonRange = SpatialRange.parse(def[1]);
	    break;
	case HIGHT_VARIABLE:
	    hightRange = NumericRange.parse(def[1]);
	    break;
	case VAR_VARIABLE:
	    variables = Arrays.asList(def[1].split(","));
	    break;
	case TIME_VARIABLE:
	    timeRange = TimeRange.parse(def[1]);
	    break;
	default:
	    throw new IllegalArgumentException("Unknown variable " + variable);
	}
    }

    public boolean hasLatitudeRange()
    {
	return latRange != null;
    }

    public SpatialRange getLatitudeRange()
    {
	return latRange;
    }

    public void setLatitudeRange(SpatialRange latRange)
    {
	this.latRange = latRange;
    }

    public boolean hasLongitudeRange()
    {
	return lonRange != null;
    }

    public SpatialRange getLongitudeRange()
    {
	return lonRange;
    }

    public SpatialRange getLatRange()
    {
	return latRange;
    }

    public void setLongitudeRange(SpatialRange lonRange)
    {
	this.lonRange = lonRange;
    }

    public void setHightRange(NumericRange hightRange)
    {
	this.hightRange = hightRange;
    }

    public void setTimeRange(TimeRange timeRange)
    {
	this.timeRange = timeRange;
    }

    public void setVariables(List<String> variables)
    {
	this.variables = variables;
    }

    public boolean hasHightRange()
    {
	return hightRange != null;
    }

    public NumericRange getHightRange()
    {
	return hightRange;
    }

    public boolean hasVariablesDefined()
    {
	return variables != null && !variables.isEmpty();
    }

    public List<String> getVariables()
    {
	if (!hasVariablesDefined())
	{
	    return new LinkedList<String>();
	}

	return variables;
    }

    public int getNDimensions()
    {
	int dims = 0;
	if (hasLatitudeRange())
	    dims += 1;
	if (hasLongitudeRange())
	    dims += 1;
	if (hasHightRange())
	    dims += 1;
	if (hasTimeRangeDefined())
	    dims += 1;
	return dims;
    }

    public TimeRange getTimeRange()
    {
	return timeRange;
    }

    public boolean hasTimeRangeDefined()
    {
	return timeRange != null;
    }

    @Override
    public String toString()
    {
	QueryBuilder query = new QueryBuilder();
	if (!variables.isEmpty())
	    query.add(VAR_VARIABLE, variables);
	if (hasLongitudeRange())
	    query.add(LONGITUDE_VARIABLE, lonRange);
	if (hasLatitudeRange())
	    query.add(LATITUDE_VARIABLE, latRange);
	if (hasHightRange())
	    query.add(HIGHT_VARIABLE, hightRange);
	if (hasTimeRangeDefined())
	    query.add(TIME_VARIABLE, timeRange);
	return getBaseUrl() + "/" + getDataset() + "?" + query.toString();
    }
}
