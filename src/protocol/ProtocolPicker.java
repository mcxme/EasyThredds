package protocol;

import java.util.Random;

import protocol.translated.CdmRemoteProtocol;
import protocol.translated.NCSSProtocol;
import protocol.translated.OPeNDAPProtocol;
import protocol.translated.TranslatedProtocol;

public class ProtocolPicker
{
    
    private static int counter = 0;

    /**
     * Chooses any of the implemented protocols that should work best for the given query.
     * Possible protocols: OPeNDAP, NCSS, CmdRemote
     */
    public static TranslatedProtocol pickBest(CollectiveProtocol query) {

	// TODO currently only picks a random protocol
	
	TranslatedProtocol translated = null;
	switch (Math.abs(counter++) % 3) {
	case 0:
	    translated = new OPeNDAPProtocol(query); break;
	case 1:
	    translated = new CdmRemoteProtocol(query); break;
	case 2:
	    translated = new NCSSProtocol(query); break;
	default:
	    throw new IllegalStateException();
	}
	
	assert (translated != null);
	return translated;
    }
    
}
