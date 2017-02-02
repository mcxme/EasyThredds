package service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import config.ConfigReader;
import service.ProtocolPicker.Protocol;

public class Thredds
{

    public static Set<Protocol> getSupportedProtocols(String dataset)
    {

	String catalogueUrl = ConfigReader.getInstace().getThreddsCatalogueUrl();
	if (catalogueUrl.endsWith(".html"))
	{
	    catalogueUrl = catalogueUrl.replace(".html", ".xml");
	}
	if (!catalogueUrl.endsWith(".xml"))
	{
	    throw new IllegalStateException("The catalogue URL should end with .html or .xml");
	}

	String datasetId = dataset.replace("/", "_");
	catalogueUrl += "?dataset=" + datasetId;

	HashSet<Protocol> supportedProtocols = new HashSet<>(ProtocolPicker.N_PROTOCOLS);

	try
	{
	    URL url = new URL(catalogueUrl);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestMethod("GET");
	    connection.setRequestProperty("Accept", "application/xml");

	    InputStream xml = connection.getInputStream();
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(xml);

	    doc.getDocumentElement().normalize();

	    NodeList nodes = doc.getElementsByTagName("service");
	    // iterate over all outer services available
	    for (int n = 0; n < nodes.getLength(); n++)
	    {
		Node node = nodes.item(n);
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
		    Element serviceElement = (Element) node;
		    // find the element that contains the available subsetting
		    // protocols
		    if (serviceElement.hasAttribute("name") && serviceElement.getAttribute("name").equals("subsetting"))
		    {
			// iterate over all available subsetting protocols
			NodeList protocols = serviceElement.getElementsByTagName("service");
			for (int p = 0; p < protocols.getLength(); p++)
			{
			    Node protocolNode = protocols.item(p);
			    if (protocolNode.getNodeType() == Node.ELEMENT_NODE)
			    {
				Element protocolElement = (Element) protocolNode;
				String textualProtocol = protocolElement.getAttribute("serviceType");
				// resolve each internally supported protocol
				switch (textualProtocol.toLowerCase())
				{
				case "opendap":
				    supportedProtocols.add(Protocol.OpenDap);
				    break;
				case "dap4":
				    supportedProtocols.add(Protocol.Dap4);
				    break;
				case "netcdfsubset":
				    supportedProtocols.add(Protocol.Ncss);
				    break;
				case "cdmremote":
				    supportedProtocols.add(Protocol.CdmRemote);
				    break;
				}
			    }
			}
		    }
		}
	    }

	} catch (MalformedURLException e)
	{
	    e.printStackTrace();
	} catch (IOException e)
	{
	    e.printStackTrace();
	} catch (SAXException e)
	{
	    e.printStackTrace();
	} catch (ParserConfigurationException e)
	{
	    e.printStackTrace();
	}

	return supportedProtocols;
    }

}
