package service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import protocol.CollectiveProtocol;
import protocol.translated.CdmRemoteProtocol;
import protocol.translated.Dap4Protocol;
import protocol.translated.NCSSProtocol;
import protocol.translated.OPeNDAPProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.decision.DecisionTree;

/**
 * This service provides different mechanisms for selecting a protocol.
 */
public class ProtocolPicker
{
    
    public static int N_PROTOCOLS = 4;

    /**
     * An enumeration of all supported protocols plus some selection mechanisms.
     */
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
     * A simple decision tree is used {@link protocol.translated.decision.DecisionTree}
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
    
    /**
     * Picks protocols in a round robin fashion.
     */
    public static TranslatedProtocol pickNext(CollectiveProtocol query) {
	return pickByIndex(Math.abs(counter++) % N_PROTOCOLS, query);
    }
    
    /**
     * Picks a random protocol.
     */
    public static TranslatedProtocol pickRandom(CollectiveProtocol query) {
	Random rand = new Random(System.currentTimeMillis());
	return pickByIndex(Math.abs(rand.nextInt()) % N_PROTOCOLS, query);
    }
    
    /**
     * Finds the name of the protocol as indexed in {@link Protocol}.
     */
    public static String getProtocolNameByIndex(int index) {
	TranslatedProtocol dummy = pickByIndex(index, new CollectiveProtocol("", "", ""));
	return dummy.getProtocolName();
    }
    
    /**
     * Picks the indicated protocol.
     */
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
    
    /**
     * Picks the protocol as indexed in {@link Protocol}.
     */
    public static TranslatedProtocol pickByIndex(int index, CollectiveProtocol query) {
	return pickByName(Protocol.values()[index], query);
    }
}
