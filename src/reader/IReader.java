package reader;

public interface IReader extends AutoCloseable
{
    void setUri(String baseUri, String query);
    
    void iterateAllData();
}
