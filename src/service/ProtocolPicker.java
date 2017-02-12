package service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.translated.CdmRemoteProtocol;
import protocol.translated.Dap4Protocol;
import protocol.translated.NCSSProtocol;
import protocol.translated.OPeNDAPProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.decision.DecisionTree;

public class ProtocolPicker
{
    
    public static int N_PROTOCOLS = 4;
    
    public enum Protocol {
	CdmRemote,
	OpenDap,
	Ncss,
	Dap4,
	Next,
	Random,
	None
    }
    
    private static int counter = 0;

    // do not allow instantiation
    private ProtocolPicker() {}
    
    /**
     * Chooses any of the implemented protocols that should work best for the given query.
     * Possible protocols: OPeNDAP, NCSS, CmdRemote
     */
    public static TranslatedProtocol pickBest(CollectiveProtocol query) {
	Protocol bestProtocol = DecisionTree.decide(query);
	if (bestProtocol == Protocol.None) {
	    throw new IllegalStateException("No suitable protocol found");
	}
	
	return pickByName(bestProtocol, query);
    }
    
    public static Set<Protocol> getProtocols() {
	Set<Protocol> protocols = new HashSet<>();
	protocols.add(Protocol.CdmRemote);
	protocols.add(Protocol.Ncss);
	protocols.add(Protocol.OpenDap);
	return protocols;
    }
    
    public static TranslatedProtocol pickNext(CollectiveProtocol query) {
	return pickByIndex(Math.abs(counter++) % N_PROTOCOLS, query);
    }
    
    public static TranslatedProtocol pickRandom(CollectiveProtocol query) {
	Random rand = new Random(System.currentTimeMillis());
	return pickByIndex(Math.abs(rand.nextInt()) % N_PROTOCOLS, query);
    }
    
    public static String getProtocolNameByIndex(int index) {
	TranslatedProtocol dummy = pickByIndex(index, new CollectiveProtocol("", "", ""));
	return dummy.getProtocolName();
    }
    
    public static synchronized TranslatedProtocol pickByName(Protocol protocol, CollectiveProtocol query) {
	TranslatedProtocol translated = null;
	switch (protocol) {
	case CdmRemote:
	    translated = new CdmRemoteProtocol(query);
	    break;
	case Ncss:
	    translated = new NCSSProtocol(query);
	    break;
	case OpenDap:
	    translated = new OPeNDAPProtocol(query);
	    break;
	case Dap4: translated = new Dap4Protocol(query); break;
	case Next: return pickNext(query);
	case Random: return pickRandom(query);
	default:
	    throw new UnsupportedOperationException("Unknown protocol " + protocol);
	}
	
	return translated;
    }
    
    public static TranslatedProtocol pickByIndex(int index, CollectiveProtocol query) {
	return pickByName(Protocol.values()[index], query);
    }
}
