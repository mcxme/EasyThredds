package protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import protocol.parse.NumericRange;
import protocol.parse.TimeRange;

public class CollectiveProtocol extends Protocol
{
	public static final String LATITUDE_VARIABLE = "lat";
	public static final String LONGITUDE_VARIABLE = "lon";
	public static final String HIGHT_VARIABLE = "hig";
	public static final String VAR_VARIABLE = "var";
	public static final String TIME_VARIABLE = "time";
	
	private NumericRange latRange;
	private NumericRange lonRange;
	private NumericRange hightRange;
	private TimeRange timeRange;
	private List<String> variables;
	
	public CollectiveProtocol(String baseUrl, String dataset, String query) {
	    	super(dataset, baseUrl);
	    	this.variables = new ArrayList<String>();
		parseInput(query);
	}
	
	private void parseInput(String query) {
		// input format:
		// foo=[x:y:z]&var=a,b,c where
		// foo indicates a variable in {lat, lon, hig}
		// and [x:y:z] is the range [x, x + y, x + 2y, ..., z]
		// fields are separated by an &
		
		String[] varParts = query.split("&");
		for (int i = 0; i < varParts.length; i++) {
			parseDefinition(varParts[i]);
		}
		
		if (!hasVariablesDefined()) {
		    throw new IllegalArgumentException("No variables defined");
		}
	}
	
	private void parseDefinition(String definition) {
		String[] def = definition.split("=");
		if (def.length != 2) {
			throw new IllegalArgumentException("The definition is not well-formed: " + definition);
		}
		
		String variable = def[0];
		switch (variable) {
		case LATITUDE_VARIABLE:
			latRange = NumericRange.parse(def[1]); break;
		case LONGITUDE_VARIABLE:
			lonRange = NumericRange.parse(def[1]); break;
		case HIGHT_VARIABLE:
			hightRange = NumericRange.parse(def[1]); break;
		case VAR_VARIABLE:
		    variables = Arrays.asList(def[1].split(",")); break;
		case TIME_VARIABLE:
			timeRange = TimeRange.parse(def[1]); break;
		default:
			throw new IllegalArgumentException("Unknown variable " + variable);
		}
	}
	
	public NumericRange getLatitudeRange()
	{
		return latRange;
	}

	public NumericRange getLongitudeRange()
	{
		return lonRange;
	}

	public NumericRange getHightRange()
	{
		return hightRange;
	}
	
	public boolean hasVariablesDefined() {
	    return variables != null && !variables.isEmpty();
	}
	
	public List<String> getVariables() {
	    if (!hasVariablesDefined()) {
		return new LinkedList<String>();
	    }
	    
	    return variables;
	}
	
	public TimeRange getTimeRange() {
		return timeRange;
	}
	
	public boolean hasTimeRangeDefined() {
	    return timeRange != null;
	}
}
