import org.junit.Test;

import protocol.CollectiveProtocol;
import protocol.OPeNDAPProtocol;



public class OPeNDAPTranslationTest
{
    	private static final String STD_SERVER = "http://nc-catalogue.scc.kit.edu/thredds";
    	private static final String STD_DATASET = "dataset/foo/bar.sc";

	@Test
	public void testOpenDapTranslation() {
		String query = "lat=[0:1:20]&lon=[12:2:42]&hig=[-10:3:10]";
		CollectiveProtocol collective = new CollectiveProtocol(
			STD_SERVER,
			STD_DATASET,
			query);
		OPeNDAPProtocol openDap = new OPeNDAPProtocol(collective);
		String translated = openDap.getTranslatedUrl().toString();
		System.out.println("OPeNDAP: " + translated);
	}
	
}
