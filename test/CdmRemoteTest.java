import org.junit.Test;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import reader.IReader;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;

public class CdmRemoteTest
{
    private static final String TEST_DATASET = "RC1SD-base-08/cloud";
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    private static final String VARIABLE = "aclc";
    private static final String QUERY = "var=" + VARIABLE + "&lev=[0:5:50000]&lat=[12.4;1;17.8]&lon=[4.0;;18.9]&time=[20/08/1992-20:00:00;1;22/02/1999-12:00:00]";
    
    @Test
    public void testIterateFull() {
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, TEST_DATASET, QUERY);
	TranslatedProtocol cdmRemote = ProtocolPicker.pickByName(Protocol.CdmRemote, collective);
	IReader cdmRemoteReader = cdmRemote.getReader();
	long bytes = cdmRemoteReader.iterateAllData();
	System.out.println("CdmRemote read " + bytes + " bytes");
	System.out.println("query: " + cdmRemote.getTranslatedHttpUrl().toString());
    }
}
