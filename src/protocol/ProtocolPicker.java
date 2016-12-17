package protocol;

public class ProtocolPicker
{

    /**
     * Chooses any of the implemented protocols that should work best for the given query.
     * Possible protocols: OPeNDAP, NCSS, CmdRemote
     */
    public static TranslatedProtocol pickBest(CollectiveProtocol query) {

	// TODO currently only forwards to OPeNDAP
	TranslatedProtocol translated = new OPeNDAPProtocol(query);
	
	return translated;
    }
    
}
