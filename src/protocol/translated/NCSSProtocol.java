package protocol.translated;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.reader.IReader;
import protocol.reader.NCSSReader;
import protocol.reader.ncssMeta.NCSSMetaDoubleReader;
import protocol.reader.ncssMeta.NCSSMetaFloatReader;
import protocol.reader.ncssMeta.NCSSMetaLongReader;
import protocol.reader.ncssMeta.NCSSMetaReader;
import protocol.translated.util.DimensionArray;
import protocol.translated.util.QueryBuilder;
import protocol.translated.util.VariableReader;
import service.ProtocolPicker.Protocol;

/**
 * Adapter class for the NetCdf Subset Service (NCSS) 
 */
public class NCSSProtocol extends TranslatedProtocol
{
    public static final boolean MAKE_OUTPUT_CF_COMPLIANT = true;
    
    public NCSSProtocol(CollectiveProtocol query)
    {
	super(query);
    }
    
    @Override
    public String getProtocolName() {
	return "NCSS";
    }
    
    @Override
    public Protocol getType()
    {
	return Protocol.Ncss;
    }
    
    @Override
    protected String getNetCdfName() {
	throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getNcssUrlName();
    }
    
    @Override
    protected IReader readerFactory()
    {
	return new NCSSReader();
    }
    
    @Override
    protected String getFileNameExtension()
    {
	return null;
    }

    @Override
    public boolean canTranslate(CollectiveProtocol protocol)
    {
	SpatialRange latRange = protocol.getLatitudeRange();
	SpatialRange lonRange = protocol.getLongitudeRange();

	// NCSS cannot differentiate longitude and latitude strides
	if (protocol.hasLongitudeRange() && protocol.hasLatitudeRange()
		&& (latRange.getStride() != lonRange.getStride()))
	{
	    return false;
	}

	// NCSS cannot specify altitude ranges but is only capable of traversing
	// the entire range (stride is possible) or a single level
	if (protocol.hasHightRange())
	{
	    VariableReader reader = getDimensionData();
	    if (!reader.isFullAltitudeRange(getDataset(), protocol.getHightRange())
		    && !reader.isSingleAltitudeLevel(getDataset(), protocol.getHightRange()))
	    {
		return false;
	    }
	}
	
	return true;
    }
    
    /**
     * Forms the query based on the Netcdf Subset Service Reference:
     * {@link http://www.unidata.ucar.edu/software/thredds/current/tds/reference/NetcdfSubsetServiceReference.html}
     */
    @Override
    protected void translateQuery(CollectiveProtocol protocol, QueryBuilder query)
    {
	if (!protocol.hasVariablesDefined()) {
	    throw new IllegalArgumentException("The query needs to specify variables to be fetched");
	}
	
	SpatialRange latRange = protocol.getLatitudeRange();
	SpatialRange lonRange = protocol.getLongitudeRange();
	
	
	// add all variables
	query.add("var", protocol.getVariables());
	
	if (protocol.hasLatitudeRange() && protocol.hasLongitudeRange()) {
	    if (latRange.isPoint() && lonRange.isPoint()) {
		// create the request point
		query.add("longitude", lonRange.getStartCoordinate());
		query.add("latitude", latRange.getStartCoordinate());
	    } else {
		// create the bounding box
		query.add("south", latRange.getStartCoordinate());
		query.add("north", latRange.getEndCoordinate());
		query.add("west", lonRange.getStartCoordinate());
		query.add("east", lonRange.getEndCoordinate());

		// take the minimum spatial stride
		int horizontalStride = Math.min(latRange.getStride(), lonRange.getStride());
		query.add("horizStride", horizontalStride);
	    }
	}
	
	
	if (protocol.hasHightRange()) {
	    NumericRange hightRange = protocol.getHightRange();
	    if (hightRange.isPoint()) {
		// The range is a single value -> single level
		query.add("vertCoord", hightRange.getStart());
	    } else {
		// The range has multiple values -> vertical stride
		query.add("vertStride", hightRange.getStride());
	    }
	}
	
	// add the time specification if defined
	if (protocol.hasTimeRangeDefined()) {
	    TimeRange timeRange = protocol.getTimeRange();
	    if (timeRange.isPoint()) {
		query.add("time", timeRange.getStartTime());
	    } else {
		query.add("time_start", timeRange.getStartTime());
		query.add("time_end", timeRange.getEndTime());
		query.add("time_stride", timeRange.getStride());
	    }
	}
	
	// add the output format
	query.add("accept", ConfigReader.getInstace().getNcssOutputFormat());
	// enforce CF compliance
	query.add("addLatLon", MAKE_OUTPUT_CF_COMPLIANT);
    }
    
    @Override
    protected DimensionArray downloadDimensionData(CollectiveProtocol protocol)
    {
	String location = getDatasetBaseUrl() + "/dataset.xml";
	Document doc;
	// download and parse the XML meta data
	try
	{
	    URL ncUrl = new URL(location);
	    HttpURLConnection connection = (HttpURLConnection) ncUrl.openConnection();
	    connection.setRequestMethod("GET");
	    connection.setRequestProperty("Accept", "application/xml");

	    InputStream xml = connection.getInputStream();
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    doc = dBuilder.parse(xml);
	    doc.getDocumentElement().normalize();
	} catch (IOException | ParserConfigurationException | SAXException e)
	{
	    throw new IllegalStateException("Could not fetch and parse the meta data xml from the NCSS dataset");
	}

	IReader latReader = null;
	IReader lonReader = null;
	IReader lvlReader = null;
	IReader timeReader = null;

	// process the XML document:
	// fetch all 'axis' elements which contain basic information (like type
	// and name)
	NodeList nodes = doc.getElementsByTagName("axis");
	for (int n = 0; n < nodes.getLength(); n++)
	{
	    Node node = nodes.item(n);
	    if (node.getNodeType() == Node.ELEMENT_NODE)
	    {
		Element axisElement = (Element) node;
		NodeList values = axisElement.getElementsByTagName("values");
		assert (values.getLength() == 1);
		assert (values.item(0).getNodeType() == Node.ELEMENT_NODE);
		Element valueElement = (Element) values.item(0);
		String name = axisElement.getAttribute("name");
		String type = axisElement.getAttribute("type");
		IReader reader;
		// process each axis elements as either...
		if (valueElement.hasAttribute("start")) {
		    // ... a range defined as start, increment and length
		    reader = getMetaReader(name, type, valueElement.getAttribute("start"), valueElement.getAttribute("increment"), valueElement.getAttribute("npts"));
		} else {
		    // ... a given list of values
		    Node content = valueElement.getFirstChild();
		    assert (content.getNodeType() == node.TEXT_NODE);
		    reader = getMetaReader(name, type, content.getNodeValue());
		}

		switch (name)
		{
		case "lat":
		    latReader = reader;
		    break;
		case "lon":
		    lonReader = reader;
		    break;
		case "lev":
		    lvlReader = reader;
		    break;
		case "time":
		    timeReader = reader;
		    break;
		default:
		    throw new UnsupportedOperationException("Unknown dimension variable: " + name);
		}
	    }
	}

	return new DimensionArray(latReader, lonReader, lvlReader, timeReader);

    }
    
    private static IReader getMetaReader(String name, String type, String txtStart, String txtIncrement, String txtN) {
	int n = Integer.parseInt(txtN);
	switch (type) {
	case "float":
	    return new NCSSMetaFloatReader(name, txtStart, txtIncrement, n);
	case "double":
	    return new NCSSMetaDoubleReader(name, txtStart, txtIncrement, n);
	case "long":
	    return new NCSSMetaLongReader(name, txtStart, txtIncrement, n);
	    default:
		throw new IllegalArgumentException("unknown data type '" + type + "'. Can only handle float, double, long");
	}
    }
    
    private static IReader getMetaReader(String name, String type, String txtData) {
	String[] data = txtData.split(" ");
	
	NCSSMetaReader reader;
	switch (type) {
	case "float":
	    reader = new NCSSMetaFloatReader(name, data.length);
	    break;
	case "double":
	    reader = new NCSSMetaDoubleReader(name, data.length);
	    break;
	case "long":
	    reader = new NCSSMetaLongReader(name, data.length);
	    break;
	    default:
		throw new IllegalArgumentException("unknown data type '" + type + "'. Can only handle float, double, long");
	}
	
	for (int i = 0; i < data.length; i++) {
	    reader.set(i, data[i]);
	}
	
	return reader;
    }

}
