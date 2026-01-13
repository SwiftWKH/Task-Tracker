import java.util.*;

import javax.management.RuntimeErrorException;

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

        while(true){
            Token keyToken = tokens.get(position);
            if(keyToken.type != TokenType.STRING){
                throw new RuntimeException("Expected string key");
            }
            String key = keyToken.value;
            position++;

            if(tokens.get(position).type != TokenType.COLON){
                throw new RuntimeException("Expected :");
            }
            position++;

            JsonValue value = parseValue();
            object.put(key, value);

            Token next = tokens.get(position);
            if(next.type == TokenType.OBJECT_END){
                position++;
                break;
            } else if (next.type == TokenType.COMMA){
                position++;
            } else {
                throw new RuntimeException("Expected , or }");
            }
        }
        return JsonValue.object(object);
    }
    
    private JsonValue parseArray() {
        List<JsonValue> array = new ArrayList<>();
        position++;

        if(tokens.get(position).type == TokenType.ARRAY_END){
            position++;
            return JsonValue.array(array);
        }

        while(true){
            JsonValue value = parseValue();
            array.add(value);

            Token next = tokens.get(position);
            if (next.type == TokenType.ARRAY_END){
                position++;
                break;
            } else if (next.type == TokenType.COMMA){
                position++;
            } else {
                throw new RuntimeException("Expected , or ]");
            }
        }
        return JsonValue.array(array);
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
