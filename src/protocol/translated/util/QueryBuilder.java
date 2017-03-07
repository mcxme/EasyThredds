package protocol.translated.util;

/**
 * This utility class helps building queries using a buffer,
 */
public class QueryBuilder
{
    public static final String AND = "&";
    public static final String ASSIGN = "=";
    public static final String VAR_LIST_SEPARATOR = ",";
    
    private StringBuilder builder;
    
    public QueryBuilder() {
	this.builder = new StringBuilder();
    }
    
    /**
     * Adds this key-value pair to the query in the format key=value (separators
     * are added if necessary)
     */
    public void add(String key, String value) {
	if (key == null || value == null) {
	    throw new IllegalArgumentException("neither key nor value can be null");
	}
	
	if (builder.length() != 0) {
	    builder.append(AND);
	}
	
	builder.append(key);
	builder.append(ASSIGN);
	builder.append(value);
    }

    /**
     * Adds this key-value pair to the query in the format key=value (separators
     * are added if necessary). The value is obtained throught the toString
     * method
     */
    public void add(String key, Object value) {
	if (value == null) {
	    throw new IllegalArgumentException("the value must not be null");
	}
	
	add(key, value.toString());
    }
    
    /**
     * Adds this key-value pair to the query in the format key=value (separators
     * are added if necessary). The value is a list of chained textual values.
     */
    public void add(String key, Iterable<?> values) {
	if (values == null) {
	    throw new IllegalArgumentException("the values must not be null");
	}
	
	StringBuilder valueBuilder = new StringBuilder();
	for (Object value : values) {
	    valueBuilder.append(value.toString());
	    valueBuilder.append(VAR_LIST_SEPARATOR);
	}
	
	if (valueBuilder.length() == 0) {
	    throw new IllegalArgumentException("Did not contain elements to be added to the query");
	}

	assert (VAR_LIST_SEPARATOR.length() == 1);
	valueBuilder.deleteCharAt(valueBuilder.length() - 1);
	add(key, valueBuilder);
    }
    
    /**
     * Appends the given string to the query without verification.
     */
    public void append(String value) {
	builder.append(value);
    }

    /**
     * Appends the given textual representation of the object to the query
     * without verification.
     */
    public void append(Object value) {
	if (value == null) {
	    throw new IllegalArgumentException("the value must not be null");
	}
	
	builder.append(value.toString());
    }
    
    
    public void appendInBrackets(String value) {
	appendInBrackets(value);
    }
    
    public void appendInBrackets(Object value) {
	builder.append("[");
	append(value);
	builder.append("]");
    }
    
    public void removeLastChar() {
	if (builder.length() > 0) {
	    builder.deleteCharAt(builder.length() - 1);
	}
    }
    
    @Override
    public String toString() {
	return builder.toString();
    }
}
