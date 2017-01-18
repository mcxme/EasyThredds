package reader;

public interface IReader extends AutoCloseable
{
    void setUri(String baseUri, String query);
    
    /**
     * Fully iterates the data set and returns the size in bytes
     */
    long iterateAllData();
    
    float[] readFloatArray(String variableName);
    
    long[] readLongArray(String variableName);
}
