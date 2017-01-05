package service;

import java.util.Random;

import protocol.CollectiveProtocol;
import protocol.translated.CdmRemoteProtocol;
import protocol.translated.Dap4Protocol;
import protocol.translated.NCSSProtocol;
import protocol.translated.OPeNDAPProtocol;
import protocol.translated.TranslatedProtocol;

public class ProtocolPicker
{
    
    private static int counter = 0;
    public static int N_PROTOCOLS = 4;
    
    public enum Protocol {
	CdmRemote,
	Dap4,
	OpenDap,
	Ncss,
	Next,
	Random
    }
    
    /**
     * Chooses any of the implemented protocols that should work best for the given query.
     * Possible protocols: OPeNDAP, NCSS, CmdRemote
     */
    public static TranslatedProtocol pickBest(CollectiveProtocol query) {

	// TODO currently only picks a random protocol
	return null;
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
    
    public static TranslatedProtocol pickByName(Protocol protocol, CollectiveProtocol query) {
	int index;
	switch (protocol) {
	case CdmRemote: index = 0; break;
	case Ncss: index = 1; break;
	case OpenDap: index = 2; break;
	case Dap4: index = 3; break;
	case Next: return pickNext(query);
	case Random: return pickRandom(query);
	default:
	    throw new UnsupportedOperationException("Unknown protocol " + protocol);
	}
	
	return pickByIndex(index, query);
    }
    
    public static TranslatedProtocol pickByIndex(int index, CollectiveProtocol query) {
	TranslatedProtocol translated = null;
	switch (index) {
	case 0:
	    translated = new CdmRemoteProtocol(query); break;
	case 1:
	    translated = new NCSSProtocol(query); break;
	case 2:
	    translated = new OPeNDAPProtocol(query); break;
	case 3:
	    translated = new Dap4Protocol(query); break;
	default:
	    throw new IllegalStateException();
	}
	
	assert (translated != null);
	return translated;
    }
    
}
