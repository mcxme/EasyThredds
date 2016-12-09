package protocol;

public class CollectiveProtocol extends Protocol
{
	public static final String LATITUDE_VARIABLE = "lat";
	public static final String LONGITUDE_VARIABLE = "lon";
	public static final String HIGHT_VARIABLE = "hig";
	
	private Range latRange;
	private Range lonRange;
	private Range hightRange;
	
	public CollectiveProtocol(String query) {
		parseInput(query);
	}
	
	private void parseInput(String query) {
		// input format:
		// foo=[x:y:z] where
		// foo indicates a variable in {lat, lon, hig}
		// and [x:y:z] is the range [x, x + y, x + 2y, ..., z]
		// fields are separated by an &
		
		String[] varParts = query.split("&");
		for (int i = 0; i < varParts.length; i++) {
			parseDefinition(varParts[i]);
		}
	}
	
	private void parseDefinition(String definition) {
		String[] def = definition.split("=");
		if (def.length != 2) {
			throw new IllegalArgumentException("The definition is not well-formed: " + definition);
		}
		
		String variable = def[0];
		Range range = Range.parse(def[1]);
		
		switch (variable) {
		case LATITUDE_VARIABLE:
			latRange = range; break;
		case LONGITUDE_VARIABLE:
			lonRange = range; break;
		case HIGHT_VARIABLE:
			hightRange = range; break;
		default:
			throw new IllegalArgumentException("Unknown variable " + variable);
		}
	}
	
	public Range getLatitudeRange()
	{
		return latRange;
	}

	public Range getLongitudeRange()
	{
		return lonRange;
	}

	public Range getHightRange()
	{
		return hightRange;
	}
}
