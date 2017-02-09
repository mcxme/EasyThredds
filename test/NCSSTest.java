import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.nodes.TextNode;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.reader.NetCdfReader;
import protocol.translated.TranslatedProtocol;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;
import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;

public class NCSSTest
{

    private static final String TEST_DATASET = "RC1SD-base-08/cloud";
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    
    
    @Test
    public void testNCSSFetchDimensions() {
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, TEST_DATASET, null);
	TranslatedProtocol ncss = ProtocolPicker.pickByName(Protocol.Ncss, collective);
	
	try {
	    String location = ncss.getDatasetBaseUrl() + "/dataset.xml";
	    URL ncUrl = new URL(location);
	    HttpURLConnection connection = (HttpURLConnection) ncUrl.openConnection();
	    connection.setRequestMethod("GET");
	    connection.setRequestProperty("Accept", "application/xml");

	    InputStream xml = connection.getInputStream();
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(xml);
	    doc.getDocumentElement().normalize();
	    
	    NodeList nodes = doc.getElementsByTagName("axis");
	    for (int n = 0; n < nodes.getLength(); n++)
	    {
		Node node = nodes.item(n);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    Element axisElement = (Element) node;
		    NodeList values = axisElement.getElementsByTagName("values");
		    assert (values.getLength() == 1);
		    assert (values.item(0).getNodeType() == Node.ELEMENT_NODE);
		    Element valueElement = (Element)values.item(0);
		    String name = axisElement.getAttribute("name");
		    String type = axisElement.getAttribute("type");
		    if (valueElement.hasAttribute("start")) {
			printIncrementValues(name, type,
				valueElement.getAttribute("start"),
				valueElement.getAttribute("increment"),
				valueElement.getAttribute("npts"));
		    } else {
			Node content = valueElement.getFirstChild();
			assert (content.getNodeType() == node.TEXT_NODE);
//			TextNode textualContents = (TextNode) content;
			printValues(name, type,
				content.getNodeValue());
		    }
		}
	    }
	    
	} catch (IOException | ParserConfigurationException | SAXException e) {
	    throw new IllegalArgumentException("Could not parse the xml file for dimensions", e);
	}
    }
    
    private static void printIncrementValues(String name, String type,
	    String txtStart, String txtIncrement, String txtN) {
	double start = Double.parseDouble(txtStart);
	double increment = Double.parseDouble(txtIncrement);
	int n = Integer.parseInt(txtN);
	
	System.out.println(name + " (" + type + "): ");
	for (int i = 0; i < n; i++) {
	    System.out.print(start + " ");
	    start += increment;
	}
	
	System.out.println();
    }
    
    private static void printValues(String name, String type, String textualValues) {
	String[] values = textualValues.split(" ");
	System.out.println(name + " (" + type + "): ");
	for (String val : values) {
	    System.out.print(val + " ");
	}
	
	System.out.println();
    }
    
}
