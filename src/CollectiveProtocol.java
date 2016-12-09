
public class CollectiveProtocol extends Protocol
{

	private int latStart;
	private int latStride;
	private int latEnd;
	
	private int lonStart;
	private int lonStride;
	private int lonEnd;
	
	private int hightStart;
	private int hightStride;
	private int hightEnd;
	
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
		
	}
	
	private void parseRange(String range) {
		
	}
	
	public int getLatStart()
	{
		return latStart;
	}
	public int getLatStride()
	{
		return latStride;
	}
	public int getLatEnd()
	{
		return latEnd;
	}
	public int getLonStart()
	{
		return lonStart;
	}
	public int getLonStride()
	{
		return lonStride;
	}
	public int getLonEnd()
	{
		return lonEnd;
	}
	public int getHightStart()
	{
		return hightStart;
	}
	public int getHightStride()
	{
		return hightStride;
	}
	public int getHightEnd()
	{
		return hightEnd;
	}
	
	
	
}
