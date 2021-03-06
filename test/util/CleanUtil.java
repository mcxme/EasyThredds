package util;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import protocol.reader.NetCdfReader;
import protocol.translated.decision.nodes.SelectByWeightedPerformanceNode;
import protocol.translated.util.VariableReader;

/**
 * Helps cleaning up stored data such as auxiliary files or caches.
 */
public class CleanUtil
{
    private CleanUtil() {}
    
    public static void cleanAuxFiles() {
	File baseDir = new File(NetCdfReader.BASE_DIRECTORY);
	if (baseDir.exists()) {
        	try
        	{
        	    FileUtils.deleteDirectory(baseDir);
        	} catch (IOException e)	{
        	    throw new IllegalStateException("Could not delete the base directory", e);
        	}
	}
    }
    
    public static void cleanStoredDimensionData() {
	VariableReader.getInstance().clear();
    }
    
    public static void cleanPerformanceData() {
	SelectByWeightedPerformanceNode.clean();
    }
    
    public static void cleanAll() {
	cleanAuxFiles();
	cleanStoredDimensionData();
	cleanPerformanceData();
    }
    
    public static void main(String[] args)
    {
	cleanAll();
    }
}
