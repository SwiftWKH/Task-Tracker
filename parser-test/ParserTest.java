import java.util.*;

public class ParserTest {
    public static void main(String[] args) {
        ParserTest test = new ParserTest();
        
        System.out.println("=== JSON Parser Test Suite ===\n");
        
        // Test tokenizer first
        test.testTokenizer();
        
        // Test parser
        test.testParser();
        
        System.out.println("\n=== All Tests Complete ===");
    }
    
    private void testTokenizer() {
        System.out.println("--- Tokenizer Tests ---");
        
        JsonTokenizer tokenizer = new JsonTokenizer();
        
        // Test 1: Simple string
        testTokenize(tokenizer, "\"hello\"", 
            Arrays.asList(new Token(TokenType.STRING, "hello")));
        
        // Test 2: Simple number
        testTokenize(tokenizer, "42", 
            Arrays.asList(new Token(TokenType.NUMBER, "42")));
        
        // Test 3: Boolean
        testTokenize(tokenizer, "true", 
            Arrays.asList(new Token(TokenType.BOOLEAN, "true")));
        
        // Test 4: Null
        testTokenize(tokenizer, "null", 
            Arrays.asList(new Token(TokenType.NULL, "null")));
        
        // Test 5: Empty object
        testTokenize(tokenizer, "{}", 
            Arrays.asList(
                new Token(TokenType.OBJECT_START, "{"),
                new Token(TokenType.OBJECT_END, "}")
            ));
        
        // Test 6: Simple object
        testTokenize(tokenizer, "{\"name\": \"John\"}", 
            Arrays.asList(
                new Token(TokenType.OBJECT_START, "{"),
                new Token(TokenType.STRING, "name"),
                new Token(TokenType.COLON, ":"),
                new Token(TokenType.STRING, "John"),
                new Token(TokenType.OBJECT_END, "}")
            ));
        
        // Test 7: Simple array
        testTokenize(tokenizer, "[1, 2, 3]", 
            Arrays.asList(
                new Token(TokenType.ARRAY_START, "["),
                new Token(TokenType.NUMBER, "1"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.NUMBER, "2"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.NUMBER, "3"),
                new Token(TokenType.ARRAY_END, "]")
            ));
    }
    
    private void testParser() {
        System.out.println("\n--- Parser Tests ---");
        
        JsonParser parser = new JsonParser();
        
        // Test 1: Simple string
        testParse(parser, "\"hello\"", "STRING:hello");
        
        // Test 2: Simple number
        testParse(parser, "42", "NUMBER:42");
        
        // Test 3: Boolean
        testParse(parser, "true", "BOOLEAN:true");
        
        // Test 4: Null
        testParse(parser, "null", "NULL:null");
        
        // Test 5: Empty object
        testParse(parser, "{}", "OBJECT:{}");
        
        // Test 6: Simple object
        testParse(parser, "{\"name\": \"John\"}", "OBJECT with name=John");
        
        // Test 7: Empty array
        testParse(parser, "[]", "ARRAY:[]");
        
        // Test 8: Simple array
        testParse(parser, "[1, 2, 3]", "ARRAY with 3 numbers");
        
        // Test 9: Complex nested structure
        testParse(parser, """
            {
                "name": "John",
                "age": 30,
                "active": true,
                "address": {
                    "city": "Boston"
                },
                "hobbies": ["reading", "coding"]
            }
            """, "Complex nested object");
    }
    
    private void testTokenize(JsonTokenizer tokenizer, String input, List<Token> expected) {
        try {
            List<Token> result = tokenizer.tokenize(input);
            
            System.out.print("Tokenize '" + input + "' -> ");
            
            if (result.equals(expected)) {
                System.out.println("✓ PASS");
            } else {
                System.out.println("✗ FAIL");
                System.out.println("  Expected: " + expected);
                System.out.println("  Got:      " + result);
            }
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + e.getMessage());
        }
    }
    
    private void testParse(JsonParser parser, String input, String description) {
        try {
            JsonValue result = parser.parse(input);
            
            System.out.print("Parse " + description + " -> ");
            
            if (result != null) {
                System.out.println("✓ PASS: " + result);
                
                // Additional validation for specific types
                if (result.getType() == JsonValue.Type.OBJECT && result.asObject() != null) {
                    Map<String, JsonValue> obj = result.asObject();
                    if (obj.containsKey("name")) {
                        System.out.println("    name = " + obj.get("name").asString());
                    }
                }
                
                if (result.getType() == JsonValue.Type.ARRAY && result.asArray() != null) {
                    List<JsonValue> arr = result.asArray();
                    System.out.println("    array size = " + arr.size());
                }
            } else {
                System.out.println("✗ FAIL: null result");
            }
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + e.getMessage());
        }
    }
}
