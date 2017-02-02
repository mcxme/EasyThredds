import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import reader.IReader;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;
import util.CleanUtil;

public class SizeTest
{
    private static final String TEST_DATASET = "RC1SD-base-08/cloud";
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    private static final String VARIABLE = "aclc";
    private static final String QUERY = "var=" + VARIABLE + "&lev=[0:5:50000]&lat=[9.767145;1;18.138971]&lon=[5.625;;19.6875]&time=[20/08/1992-00:00:00;1;22/02/1999-00:00:00]";
    
    @AfterClass
    public static void cleanUp() {
	CleanUtil.cleanAuxFiles();
    }
    
    @BeforeClass
    public static void init() {
	System.out.println("Measuring size for query: " + QUERY);
    }
    
    private void printSize(ProtocolPicker.Protocol protocol) {
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, TEST_DATASET, QUERY);
	TranslatedProtocol opendap = ProtocolPicker.pickByName(protocol, collective);
	IReader opendapReader = opendap.getReader();
	long bytes = opendapReader.iterateAllData();
	System.out.println(String.format("%s: %d bytes", protocol.toString(), bytes));
    }

    @Test
    public void testOpenDapSize() {
	printSize(Protocol.OpenDap);
    }
    
    @Test
    public void testNcssSize() {
	printSize(Protocol.Ncss);
    }
    
    @Test
    public void testCdmRemoteSize() {
	printSize(Protocol.CdmRemote);
    }
    
}
