package util;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import protocol.reader.NetCdfReader;

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
}
