import org.junit.Test;

import protocol.CollectiveProtocol;
import protocol.OPeNDAPProtocol;



public class OPeNDAPTranslationTest
{

	@Test
	public void testOpenDapTranslation() {
		String query = "lat=[0:1:20]&lon=[12:2:42]&hig=[-10:3:10]";
		CollectiveProtocol collective = new CollectiveProtocol(query);
		OPeNDAPProtocol openDap = new OPeNDAPProtocol();
		String translated = openDap.getTranslatedUrl(collective);
		System.out.println("OPeNDAP: " + translated);
	}
	
}
