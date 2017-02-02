package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader implements AutoCloseable
{
    // Property file:
    private static final String PROPERTIES_FILE_NAME = "config.properties";
    // Properties:
    private static final String THREDDS_CATALOGUE_URL_PROPERTY = "threddsCatalogueUrl";
    private static final String THREDDS_URL_PROPERTY = "threddsServerUrl";
    private static final String OPENDAP_URL_PROPERTY = "opendapUrlName";
    private static final String NCSS_URL_PROPERTY = "ncssUrlName";
    private static final String CDM_REMOTE_URL_PROPERTY = "cdmRemoteUrlName";
    private static final String DAP_4_URL_PROPERTY = "dap4UrlName";
    private static final String NCSS_OUTPUT_FORMAT = "nccsOutputFormat";
    private static final String CDM_REMOTE_OUTPUT_FORMAT = "cdmRemoteOutputFormat";
    
    private static ConfigReader instance;
    
    private InputStream input;
    private Properties props;
    
    private ConfigReader() {
	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	input = classLoader.getResourceAsStream(PROPERTIES_FILE_NAME);
	props = new Properties();
	try {
	    props.load(input);
	} catch (IOException e) {
	    throw new IllegalStateException("Could not load the properties file: " + PROPERTIES_FILE_NAME);
	}
    }
    
    public static final ConfigReader getInstace() {
	if (instance == null) {
	    instance = new ConfigReader();
	}
	
	return instance;
    }
    
    public String getThreddsUrl() {
	return props.getProperty(THREDDS_URL_PROPERTY);
    }
    
    public String getThreddsCatalogueUrl() {
	return props.getProperty(THREDDS_CATALOGUE_URL_PROPERTY);
    }
    
    public String getOpenDapUrlName() {
	return props.getProperty(OPENDAP_URL_PROPERTY);
    }
    
    public String getNcssUrlName() {
	return props.getProperty(NCSS_URL_PROPERTY);
    }
    
    public String getCdmRemoteUrlName() {
	return props.getProperty(CDM_REMOTE_URL_PROPERTY);
    }
    
    public String getDap4UrlName() {
	return props.getProperty(DAP_4_URL_PROPERTY);
    }

    public String getNcssOutputFormat() {
	return props.getProperty(NCSS_OUTPUT_FORMAT);
    }

    public String getCdmRemoteOutputFormat() {
	return props.getProperty(CDM_REMOTE_OUTPUT_FORMAT);
    }

    @Override
    public void close()
    {
	if (input != null) {
	    try {
		input.close();
	    } catch (IOException e) {
		throw new IllegalStateException("Could not close the input stream of the coniguration file");
	    }
	}
    }
}
