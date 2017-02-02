package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.translated.util.DimensionArray;
import protocol.translated.util.QueryBuilder;
import protocol.translated.util.VariableReader;
import reader.CdmRemoteReader;
import reader.IReader;

public class CdmRemoteProtocol extends TranslatedProtocol
{

    public CdmRemoteProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getCdmRemoteUrlName();
    }    
    
    @Override
    protected IReader readerFactory()
    {
	return new CdmRemoteReader();
    }
    
    @Override
    public String getProtocolName() {
	return "CdmRemote";
    }
    
    @Override
    protected String getNetCdfName() {
	return "cdmremote";
//	throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    protected String getFileNameExtension()
    {
	return null;
    }
    
    @Override
    public boolean canTranslate(CollectiveProtocol collectiveProtocol)
    {
	return true;
    }

    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	TimeRange timeRange = protocol.getTimeRange();
	SpatialRange latRange = protocol.getLatitudeRange();
	SpatialRange lonRange = protocol.getLongitudeRange();
	NumericRange lvlRange = protocol.getHightRange();
	
	VariableReader variableReader = loadDimensionData(protocol);
	String datasetKey = getDataset();

	query.add("req", "data");
	if (protocol.getVariables().isEmpty()) {
	    throw new IllegalArgumentException("at least one variable has to be defined");
	}
	
	query.append("&var=");
	for (String var : protocol.getVariables()) {
	    // a(time, lev, lat, lon);b;c
	    query.append(var);
	    query.append("(");
	    if (protocol.hasTimeRangeDefined()) {
		NumericRange translatedTime = variableReader.getTimeIndexRange(datasetKey, timeRange);
		query.append(textualRange(translatedTime));
		query.append(",");
	    }
	    if (protocol.hasHightRange()) {
		NumericRange translatedLvl = variableReader.getAltitudeIndexRange(datasetKey, lvlRange);
		query.append(textualRange(translatedLvl));
		query.append(",");
	    }
	    if (protocol.hasLatitudeRange()) {
		NumericRange translatedLat = variableReader.getLatitudeIndexRange(datasetKey, latRange);
		query.append(textualRange(translatedLat));
		query.append(",");
	    }
	    if (protocol.hasLongitudeRange()) {
		NumericRange translatedLon = variableReader.getLongitudeIndexRange(datasetKey, lonRange);
		query.append(textualRange(translatedLon));
		query.append(",");
	    }
	    
	    query.removeLastChar();
	    query.append(")");
	    query.append(";");
	}
	query.removeLastChar();
	
	/*if (protocol.hasTimeRangeDefined()) {
	    query.add("time_start", timeRange.getStartTime());
	    query.add("time_end", timeRange.getEndTime());
	}
	
	if (protocol.hasLatitudeRange()) {
	    query.add("north", latRange.getEndCoordinate());
	    query.add("south", latRange.getStartCoordinate());
	}
	
	if (protocol.hasLongitudeRange()) {
	    query.add("east", lonRange.getStartCoordinate());
	    query.add("west", lonRange.getEndCoordinate());
	}*/
	
	query.add("accept", ConfigReader.getInstace().getCdmRemoteOutputFormat());
    }
    
    private String textualRange(NumericRange range) {
	assert (range != null);
	return String.format("%d:%d:%d",
		range.getStart(),
		range.getEnd(),
		range.getStride());
    }

    private VariableReader loadDimensionData(CollectiveProtocol protocol)
    {
	VariableReader variableReader = VariableReader.getInstance();
	String datasetKey = getDataset();
	// need to fetch the dataset?
	if (!variableReader.hasDataset(datasetKey)) {
	    
	    IReader latReader = null;
	    IReader lonReader = null;
	    IReader lvlReader = null;
	    IReader timeReader = null;
	    
	    if (protocol.hasLatitudeRange()) {
		latReader = singleVarReader("lat", datasetKey);
	    }
	    
	    if (protocol.hasLongitudeRange()) {
		lonReader = singleVarReader("lon", datasetKey);
	    }
	    
	    if (protocol.hasHightRange()) {
		lvlReader = singleVarReader("lev", datasetKey);
	    }
	    
	    if (protocol.hasTimeRangeDefined()) {
		timeReader = singleVarReader("time", datasetKey);
	    }
	    
	    DimensionArray dims = new DimensionArray(latReader, lonReader, lvlReader, timeReader);
	    variableReader.addDataset(datasetKey, dims);
	}

	return variableReader;
    }
    
    private IReader singleVarReader(String variableName, String datasetKey) {
	CdmRemoteReader reader = (CdmRemoteReader) readerFactory();
	reader.setUri(getDatasetBaseUrl(),
		singleVarRequest(variableName),
		datasetKey + "-" + variableName);
	reader.setVariable(variableName);
	return reader;
    }
    
    private static String singleVarRequest(String variableName) {
	QueryBuilder query = new QueryBuilder();
	query.add("req", "data");
	query.add("var", variableName);
	query.add("accept", ConfigReader.getInstace().getCdmRemoteOutputFormat());
	return query.toString();
    }
}
