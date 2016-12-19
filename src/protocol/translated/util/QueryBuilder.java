package protocol.translated.util;

public class QueryBuilder
{
    public static final String AND = "&";
    public static final String ASSIGN = "=";
    public static final String VAR_LIST_SEPARATOR = ",";
    
    private StringBuilder builder;
    
    public QueryBuilder() {
	this.builder = new StringBuilder();
    }
    
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
    
    public void add(String key, Object value) {
	if (value == null) {
	    throw new IllegalArgumentException("the value must not be null");
	}
	
	add(key, value.toString());
    }
    
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
	
	valueBuilder.deleteCharAt(valueBuilder.length() - VAR_LIST_SEPARATOR.length());
	add(key, valueBuilder);
    }
    
    public void append(String value) {
	builder.append(value);
    }
    
    public void append(Object value) {
	if (value == null) {
	    throw new IllegalArgumentException("the value must not be null");
	}
	
	builder.append(value.toString());
    }
    
    @Override
    public String toString() {
	return builder.toString();
    }
}
