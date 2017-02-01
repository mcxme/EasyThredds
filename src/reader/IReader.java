package reader;

public interface IReader extends AutoCloseable
{
    void setUri(String baseUri, String query);
    void setUri(String baseUri, String query, String baseNameIdentifier);
    
    /**
     * Fully iterates the data set and returns the size in bytes
     */
    long iterateAllData();
    
    boolean hasVariableWithName(String name);
    
    float[] readFloatArray(String variableName);
    double[] readDoubleArray(String variableName);    
    long[] readLongArray(String variableName);
    
    int[] variableShape(String variableName);
    int singleDimVariableLength(String variableName);
}
