package protocol.reader;

/**
 * Defines functionality for reading datasets returned by the supported protocols.
 */
public interface IReader extends AutoCloseable
{
    /**
     * Same as {@link #setUri(String, String, String)}} with a default
     * identifier.
     */
    void setUri(String baseUri, String query);

    /**
     * Setting the URI may lead to an immediate download of the according
     * dataset if a remote dataset is identified.
     * 
     * @param baseUri
     *            The base URI of the dataset e.g. remoteURL/dataset
     * @param query
     *            A query on the dataset that is supported by the according
     *            protocol
     * @param baseNameIdentifier
     *            An identifier used for storing and retrieving the dataset.
     */
    void setUri(String baseUri, String query, String baseNameIdentifier);
    
    /**
     * Fully iterates the data set and returns the size in bytes
     */
    long iterateAllData();

    /**
     * Checks whether the data set has a (dimension / normal) variable with the
     * given name.
     */
    boolean hasVariableWithName(String name);
    
    /**
     * Reads 1-dimensional data for the given variable into a float array.
     */
    float[] readFloatArray(String variableName);
    
    /**
     * Reads 1-dimensional data for the given variable into a double array.
     */

    double[] readDoubleArray(String variableName);    
    
    /**
     * Reads 1-dimensional data for the given variable into a long array.
     */

    long[] readLongArray(String variableName);
    
    /**
     * Gets the shape of the data set where the length determins the cardinality
     * and the numbers indicate the size for each dimension.
     */
    int[] variableShape(String variableName);
    
    /**
     * Gets the size of the 1-dimensional variable.
     */
    int singleDimVariableLength(String variableName);
}
