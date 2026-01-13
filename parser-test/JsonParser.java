import java.util.*;

public class JsonParser {
    private List<Token> tokens;
    private int position;
    
    public JsonValue parse(String json) {
        JsonTokenizer tokenizer = new JsonTokenizer();
        this.tokens = tokenizer.tokenize(json);
        this.position = 0;
        
        if (tokens.isEmpty()) {
            throw new RuntimeException("Empty JSON");
        }
        
        return parseValue();
    }
    
    private JsonValue parseValue() {
        Token token = tokens.get(position);

        switch(token.type){
            case OBJECT_START: 
                return parseObject();
            case ARRAY_START: 
                return parseArray();
            case STRING:
                position++;
                return JsonValue.string(token.value);
            case NUMBER:
                position++;
                return JsonValue.number(token.value);
            case BOOLEAN:
                position++;
                return JsonValue.bool(Boolean.parseBoolean(token.value));
            case NULL:
                position++;
                return JsonValue.nullValue();
            default:
                throw new RuntimeException("Unexpected token: "+token.type);
        }
    }
    
    private JsonValue parseObject() {
        Map<String, JsonValue> object = new HashMap<>();
        position++;

        if(tokens.get(position).type == TokenType.OBJECT_END){
            position++;
            return JsonValue.object(object);
        }
    }
    
    private JsonValue parseArray() {
        // TODO: Implement array parsing  
        // Handle [ value, value, value ]
        return null;
    }
    
    private void expect(TokenType expected) {
        if (position >= tokens.size()) {
            throw new RuntimeException("Unexpected end of input, expected " + expected);
        }
        
        Token token = tokens.get(position);
        if (token.type != expected) {
            throw new RuntimeException("Expected " + expected + " but got " + token.type + " at position " + position);
        }
    }
    
    private Token currentToken() {
        if (position >= tokens.size()) {
            throw new RuntimeException("Unexpected end of input");
        }
        return tokens.get(position);
    }
    
    private void advance() {
        position++;
    }
}
