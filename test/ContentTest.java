import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import reader.IReader;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;
import util.CleanUtil;

public class ContentTest
{
    private static final String TEST_DATASET = "RC1SD-base-08/cloud";
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    private static final String VARIABLE = "aclc";
    private static final String QUERY = "var=" + VARIABLE + "&lev=[0:1:50000]&lat=[12.4;1;17.8]&lon=[4.0;;18.9]&time=[20/08/1992-20:00:00;1;22/02/1999-12:00:00]";
    
    @AfterClass
    public static void cleanUp() {
	CleanUtil.cleanAuxFiles();
    }
    
    @Test
    public void testOPeNDAPVariables() {
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, TEST_DATASET, QUERY);
	TranslatedProtocol opendap = ProtocolPicker.pickByName(Protocol.OpenDap, collective);
	IReader opendapReader = opendap.getReader();
	Assert.assertTrue(opendapReader.hasVariableWithName("time"));
	Assert.assertTrue(opendapReader.hasVariableWithName("lev"));
	Assert.assertTrue(opendapReader.hasVariableWithName("lat"));
	Assert.assertTrue(opendapReader.hasVariableWithName("lon"));
	Assert.assertTrue(opendapReader.hasVariableWithName(VARIABLE));
	Assert.assertEquals("Should be 4-dimensional", opendapReader.variableShape(VARIABLE).length, 4);
    }
    
    @Test
    public void testNCSSVariables() {
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, TEST_DATASET, QUERY);
	TranslatedProtocol ncss = ProtocolPicker.pickByName(Protocol.Ncss, collective);
	IReader ncssReader = ncss.getReader();
	Assert.assertTrue(ncssReader.hasVariableWithName("time"));
	Assert.assertTrue(ncssReader.hasVariableWithName("lev"));
	Assert.assertTrue(ncssReader.hasVariableWithName("lat"));
	Assert.assertTrue(ncssReader.hasVariableWithName("lon"));
	Assert.assertTrue(ncssReader.hasVariableWithName(VARIABLE));
	Assert.assertEquals("Should be 4-dimensional", ncssReader.variableShape(VARIABLE).length, 4);
    }
    
    @Test
    public void testNCSSEqualsOPeNDAP() {
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, TEST_DATASET, QUERY);
	
	TranslatedProtocol opendap = ProtocolPicker.pickByName(Protocol.OpenDap, collective);
	IReader opendapReader = opendap.getReader();
	
	TranslatedProtocol ncss = ProtocolPicker.pickByName(Protocol.Ncss, collective);
	IReader ncssReader = ncss.getReader();
	
	int[] opendapShape = opendapReader.variableShape(VARIABLE);
	int[] ncssShape = ncssReader.variableShape(VARIABLE);

	System.out.println("OPeNDAP: " + Arrays.toString(opendapShape));
	System.out.println("NCSS: " + Arrays.toString(ncssShape));
	
	Assert.assertEquals("Should have same cardinality", opendapShape.length, ncssShape.length);
	Assert.assertEquals("Should be 4-dimensional", opendapShape.length, 4);
	
	for (int d = 0; d < 4; d++) {
	    Assert.assertTrue("Size per dimension should not deviate by more than 1", Math.abs(opendapShape[d] - ncssShape[d]) <= 1);
	}
    }
    
    @Test
    public void testCdmRemoteEqualsOPeNDAP() {
	CollectiveProtocol collective = new CollectiveProtocol(THREDDS, TEST_DATASET, QUERY);
	
	TranslatedProtocol cdmRemote = ProtocolPicker.pickByName(Protocol.CdmRemote, collective);
	IReader cdmRemoteReader = cdmRemote.getReader();
	
	TranslatedProtocol opendap = ProtocolPicker.pickByName(Protocol.OpenDap, collective);
	IReader opendapReader = opendap.getReader();
	
	int[] opendapShape = opendapReader.variableShape(VARIABLE);
	int[] cdmRemoteShape = cdmRemoteReader.variableShape(VARIABLE);

	System.out.println("OPeNDAP: " + Arrays.toString(opendapShape));
	System.out.println("CdmRemote: " + Arrays.toString(cdmRemoteShape));
	
	Assert.assertEquals("Should have same cardinality", opendapShape.length, cdmRemoteShape.length);
	Assert.assertEquals("Should be 4-dimensional", opendapShape.length, 4);
	
	for (int d = 0; d < 4; d++) {
	    Assert.assertTrue("Size per dimension should not deviate by more than 1", Math.abs(opendapShape[d] - cdmRemoteShape[d]) <= 1);
	}
    }
}
