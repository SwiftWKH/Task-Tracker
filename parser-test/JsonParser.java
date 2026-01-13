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
        // TODO: Implement value parsing
        // This should handle all token types and call appropriate methods
        return null;
    }
    
    private JsonValue parseObject() {
        // TODO: Implement object parsing
        // Handle { key: value, key: value }
        return null;
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
