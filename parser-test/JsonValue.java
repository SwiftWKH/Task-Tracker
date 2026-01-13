import java.util.*;

public class JsonValue {
    public enum Type { OBJECT, ARRAY, STRING, NUMBER, BOOLEAN, NULL }
    
    private Type type;
    private Object value;
    
    // Static factory methods
    public static JsonValue object(Map<String, JsonValue> map) {
        JsonValue jv = new JsonValue();
        jv.type = Type.OBJECT;
        jv.value = map;
        return jv;
    }
    
    public static JsonValue array(List<JsonValue> list) {
        JsonValue jv = new JsonValue();
        jv.type = Type.ARRAY;
        jv.value = list;
        return jv;
    }
    
    public static JsonValue string(String str) {
        JsonValue jv = new JsonValue();
        jv.type = Type.STRING;
        jv.value = str;
        return jv;
    }
    
    public static JsonValue number(String num) {
        JsonValue jv = new JsonValue();
        jv.type = Type.NUMBER;
        jv.value = num.contains(".") ? Double.parseDouble(num) : Integer.parseInt(num);
        return jv;
    }
    
    public static JsonValue bool(boolean b) {
        JsonValue jv = new JsonValue();
        jv.type = Type.BOOLEAN;
        jv.value = b;
        return jv;
    }
    
    public static JsonValue nullValue() {
        JsonValue jv = new JsonValue();
        jv.type = Type.NULL;
        jv.value = null;
        return jv;
    }
    
    // Getters
    public Map<String, JsonValue> asObject() { 
        return type == Type.OBJECT ? (Map<String, JsonValue>) value : null; 
    }
    
    public List<JsonValue> asArray() { 
        return type == Type.ARRAY ? (List<JsonValue>) value : null; 
    }
    
    public String asString() { 
        return type == Type.STRING ? (String) value : null; 
    }
    
    public Number asNumber() { 
        return type == Type.NUMBER ? (Number) value : null; 
    }
    
    public Boolean asBoolean() { 
        return type == Type.BOOLEAN ? (Boolean) value : null; 
    }
    
    public Type getType() { 
        return type; 
    }
    
    @Override
    public String toString() {
        return type + ":" + value;
    }
}
