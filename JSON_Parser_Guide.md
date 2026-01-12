# JSON Parsing Step-by-Step Tutorial

## Overview
Build a complete JSON parser from scratch in Java without external libraries. This tutorial shows every component needed with working examples.

### How It Works Together

At a high level, the parser pipeline has three stages:
- Tokenizer: scans characters and emits meaningful tokens (structure and values).
- Parser (recursive descent): consumes tokens according to JSON grammar and builds nested values.
- Value container (`JsonValue`): represents the parsed result uniformly (object, array, string, number, boolean, null).

Typical flow: input text → tokenizer → tokens → parser → `JsonValue` tree. Errors are raised when an unexpected token or invalid construct is encountered.

### Analogy

Imagine reading a blueprint:
- The tokenizer is a highlighter that marks symbols and measurements.
- The parser is the architect who assembles highlighted parts into rooms and floors following a strict plan.
- `JsonValue` is the finished building model you can walk through (objects as rooms, arrays as corridors of items, primitives as fixtures).

This complete implementation can parse any valid JSON structure while maintaining zero external dependencies.

## Step 1: Understanding JSON Structure

JSON has 6 data types:
```json
{
  "string": "hello",
  "number": 42,
  "boolean": true,
  "null": null,
  "array": [1, 2, 3],
  "object": {"key": "value"}
}
```

## Step 2: Create Token Types


First, define what pieces we need to recognize:

Use this just so the function knows what to call each variable type.

```java
enum TokenType {
    // Structure
    OBJECT_START,    // {
    OBJECT_END,      // }
    ARRAY_START,     // [
    ARRAY_END,       // ]
    COMMA,           // ,
    COLON,           // :
    
    // Values
    STRING,          // "hello"
    NUMBER,          // 42
    BOOLEAN,         // true/false
    NULL             // null
}

class Token {
    TokenType type;
    String value;
    
    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return type + ":" + value;
    }
}
```

**Example:**
```java
// Input: {"name": "John"}
// Tokens: [OBJECT_START:{, STRING:name, COLON::, STRING:John, OBJECT_END:}]
```

## Step 3: Build the Tokenizer

Break JSON string into tokens:

```java
class JsonTokenizer {
    private String json;
    private int position;
    
    public List<Token> tokenize(String json) {
        this.json = json;
        this.position = 0;
        List<Token> tokens = new ArrayList<>();
        
        while (position < json.length()) {
            skipWhitespace();
            if (position >= json.length()) break;
            
            char c = json.charAt(position);
            
            switch (c) {
                case '{': tokens.add(new Token(TokenType.OBJECT_START, "{")); position++; break;
                case '}': tokens.add(new Token(TokenType.OBJECT_END, "}")); position++; break;
                case '[': tokens.add(new Token(TokenType.ARRAY_START, "[")); position++; break;
                case ']': tokens.add(new Token(TokenType.ARRAY_END, "]")); position++; break;
                case ',': tokens.add(new Token(TokenType.COMMA, ",")); position++; break;
                case ':': tokens.add(new Token(TokenType.COLON, ":")); position++; break;
                case '"': tokens.add(parseString()); break;
                default:
                    if (Character.isDigit(c) || c == '-') {
                        tokens.add(parseNumber());
                    } else if (c == 't' || c == 'f') {
                        tokens.add(parseBoolean());
                    } else if (c == 'n') {
                        tokens.add(parseNull());
                    } else {
                        position++; // Skip unknown
                    }
            }
        }
        return tokens;
    }
    
    private void skipWhitespace() {
        while (position < json.length() && Character.isWhitespace(json.charAt(position))) {
            position++;
        }
    }
}
```

**Example:**
```java
JsonTokenizer tokenizer = new JsonTokenizer();
List<Token> tokens = tokenizer.tokenize("{\"age\": 25}");
// Result: [OBJECT_START:{, STRING:age, COLON::, NUMBER:25, OBJECT_END:}]
```

## Step 4: Parse Strings (Handle Escapes)

```java
private Token parseString() {
    position++; // Skip opening "
    StringBuilder sb = new StringBuilder();
    
    while (position < json.length() && json.charAt(position) != '"') {
        char c = json.charAt(position);
        
        if (c == '\\' && position + 1 < json.length()) {
            position++; // Skip \
            char escaped = json.charAt(position);
            switch (escaped) {
                case '"': sb.append('"'); break;
                case '\\': sb.append('\\'); break;
                case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'r': sb.append('\r'); break;
                default: sb.append(escaped);
            }
        } else {
            sb.append(c);
        }
        position++;
    }
    
    position++; // Skip closing "
    return new Token(TokenType.STRING, sb.toString());
}

### Step 4.1: Add Unicode escapes and strict escape validation

```java
private Token parseString() {
    position++; // Skip opening "
    StringBuilder sb = new StringBuilder();

    while (position < json.length() && json.charAt(position) != '"') {
        char c = json.charAt(position);
        if (c == '\\') {
            position++;
            if (position >= json.length()) {
                throw new JsonParseException("Unterminated escape", position);
            }
            char esc = json.charAt(position);
            switch (esc) {
                case '"': sb.append('"'); break;
                case '\\': sb.append('\\'); break;
                case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'r': sb.append('\r'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'u':
                    if (position + 4 >= json.length()) {
                        throw new JsonParseException("Incomplete unicode escape", position);
                    }
                    String hex = json.substring(position + 1, position + 5);
                    if (!hex.matches("[0-9A-Fa-f]{4}")) {
                        throw new JsonParseException("Invalid unicode escape", position);
                    }
                    sb.append((char) Integer.parseInt(hex, 16));
                    position += 4;
                    break;
                default:
                    throw new JsonParseException("Invalid escape character", position);
            }
        } else {
            sb.append(c);
        }
        position++;
    }

    if (position >= json.length()) {
        throw new JsonParseException("Unterminated string", position);
    }
    position++; // Skip closing "
    return new Token(TokenType.STRING, sb.toString());
}
```
```

**Example:**
```java
// Input: "Hello\nWorld"
// Output: Token(STRING, "Hello\nWorld") with actual newline
```

## Step 5: Parse Numbers

```java
private Token parseNumber() {
    StringBuilder sb = new StringBuilder();
    
    // Handle negative
    if (json.charAt(position) == '-') {
        sb.append('-');
        position++;
    }
    
    // Parse digits and decimal
    while (position < json.length()) {
        char c = json.charAt(position);
        if (Character.isDigit(c) || c == '.') {
            sb.append(c);
            position++;
        } else {
            break;
        }
    }
    
    return new Token(TokenType.NUMBER, sb.toString());
}

### Step 5.1: Enforce JSON number grammar (sign, int, frac, exp)

```java
private Token parseNumber() {
    int start = position;
    // JSON number regex parts: -?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]+)?
    if (json.charAt(position) == '-') position++;
    if (position < json.length() && json.charAt(position) == '0') {
        position++;
    } else {
        while (position < json.length() && Character.isDigit(json.charAt(position))) position++;
    }
    if (position < json.length() && json.charAt(position) == '.') {
        position++;
        if (position >= json.length() || !Character.isDigit(json.charAt(position))) {
            throw new JsonParseException("Missing digit after decimal point", position);
        }
        while (position < json.length() && Character.isDigit(json.charAt(position))) position++;
    }
    if (position < json.length() && (json.charAt(position) == 'e' || json.charAt(position) == 'E')) {
        position++;
        if (position < json.length() && (json.charAt(position) == '+' || json.charAt(position) == '-')) position++;
        if (position >= json.length() || !Character.isDigit(json.charAt(position))) {
            throw new JsonParseException("Missing digit in exponent", position);
        }
        while (position < json.length() && Character.isDigit(json.charAt(position))) position++;
    }

    String num = json.substring(start, position);
    return new Token(TokenType.NUMBER, num);
}
```
```

**Example:**
```java
// Input: -123.45
// Output: Token(NUMBER, "-123.45")
```

## Step 6: Parse Booleans and Null

```java
private Token parseBoolean() {
    if (json.substring(position).startsWith("true")) {
        position += 4;
        return new Token(TokenType.BOOLEAN, "true");
    } else if (json.substring(position).startsWith("false")) {
        position += 5;
        return new Token(TokenType.BOOLEAN, "false");
    }
    throw new RuntimeException("Invalid boolean at position " + position);
}

private Token parseNull() {
    if (json.substring(position).startsWith("null")) {
        position += 4;
        return new Token(TokenType.NULL, "null");
    }
    throw new RuntimeException("Invalid null at position " + position);
}
```

**Example:**
```java
// Input: true
// Output: Token(BOOLEAN, "true")
// Input: null  
// Output: Token(NULL, "null")
```

## Step 7: Create Value Container

Store any JSON value in a unified way:

```java
class JsonValue {
    enum Type { OBJECT, ARRAY, STRING, NUMBER, BOOLEAN, NULL }
    
    private Type type;
    private Object value;
    
    // Constructors
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
    public Map<String, JsonValue> asObject() { return (Map<String, JsonValue>) value; }
    public List<JsonValue> asArray() { return (List<JsonValue>) value; }
    public String asString() { return (String) value; }
    public Number asNumber() { return (Number) value; }
    public Boolean asBoolean() { return (Boolean) value; }
    public Type getType() { return type; }
}
```

**Example:**
```java
JsonValue name = JsonValue.string("John");
JsonValue age = JsonValue.number("25");
JsonValue active = JsonValue.bool(true);
```

## Step 8: Build the Parser

Convert tokens to JsonValue using recursive descent:

```java
class JsonParser {
    private List<Token> tokens;
    private int position;
    
    public JsonValue parse(String json) {
        JsonTokenizer tokenizer = new JsonTokenizer();
        this.tokens = tokenizer.tokenize(json);
        this.position = 0;
        return parseValue();
    }
    
    private JsonValue parseValue() {
        Token token = tokens.get(position);
        
        switch (token.type) {
            case OBJECT_START: return parseObject();
            case ARRAY_START: return parseArray();
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
                throw new RuntimeException("Unexpected token: " + token.type);
        }
    }
}
```

**Example:**
```java
JsonParser parser = new JsonParser();
JsonValue result = parser.parse("\"hello\"");
// Result: JsonValue with type STRING and value "hello"
```

## Step 9: Parse Objects

```java
private JsonValue parseObject() {
    Map<String, JsonValue> object = new HashMap<>();
    position++; // Skip {
    
    // Handle empty object
    if (tokens.get(position).type == TokenType.OBJECT_END) {
        position++;
        return JsonValue.object(object);
    }
    
    while (true) {
        // Parse key (must be string)
        Token keyToken = tokens.get(position);
        if (keyToken.type != TokenType.STRING) {
            throw new RuntimeException("Expected string key");
        }
        String key = keyToken.value;
        position++;
        
        // Expect colon
        if (tokens.get(position).type != TokenType.COLON) {
            throw new RuntimeException("Expected :");
        }
        position++;
        
        // Parse value
        JsonValue value = parseValue();
        object.put(key, value);
        
        // Check for comma or end
        Token next = tokens.get(position);
        if (next.type == TokenType.OBJECT_END) {
            position++;
            break;
        } else if (next.type == TokenType.COMMA) {
            position++;
            // Continue loop
        } else {
            throw new RuntimeException("Expected , or }");
        }
    }
    
    return JsonValue.object(object);
}
```

**Example:**
```java
// Input: {"name": "John", "age": 25}
// Result: JsonValue.object with map containing:
//   "name" -> JsonValue.string("John")
//   "age" -> JsonValue.number("25")
```

## Step 10: Parse Arrays

```java
private JsonValue parseArray() {
    List<JsonValue> array = new ArrayList<>();
    position++; // Skip [
    
    // Handle empty array
    if (tokens.get(position).type == TokenType.ARRAY_END) {
        position++;
        return JsonValue.array(array);
    }
    
    while (true) {
        // Parse value
        JsonValue value = parseValue();
        array.add(value);
        
        // Check for comma or end
        Token next = tokens.get(position);
        if (next.type == TokenType.ARRAY_END) {
            position++;
            break;
        } else if (next.type == TokenType.COMMA) {
            position++;
            // Continue loop
        } else {
            throw new RuntimeException("Expected , or ]");
        }
    }
    
    return JsonValue.array(array);
}
```

**Example:**
```java
// Input: [1, "hello", true]
// Result: JsonValue.array with list containing:
//   JsonValue.number("1")
//   JsonValue.string("hello") 
//   JsonValue.bool(true)
```

## Step 11: Complete Working Example

```java
public class JsonParserDemo {
    public static void main(String[] args) {
        String json = """
        {
            "name": "John Doe",
            "age": 30,
            "active": true,
            "address": {
                "street": "123 Main St",
                "city": "Boston"
            },
            "hobbies": ["reading", "coding"],
            "spouse": null
        }
        """;
        
        JsonParser parser = new JsonParser();
        JsonValue result = parser.parse(json);
        
        // Access the data
        Map<String, JsonValue> person = result.asObject();
        
        String name = person.get("name").asString();
        int age = person.get("age").asNumber().intValue();
        boolean active = person.get("active").asBoolean();
        
        Map<String, JsonValue> address = person.get("address").asObject();
        String city = address.get("city").asString();
        
        List<JsonValue> hobbies = person.get("hobbies").asArray();
        String firstHobby = hobbies.get(0).asString();
        
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("City: " + city);
        System.out.println("First hobby: " + firstHobby);
    }
}
```

**Output:**
```
Name: John Doe
Age: 30
City: Boston
First hobby: reading
```

## Step 12: Error Handling

Add proper error handling:

```java
class JsonParseException extends RuntimeException {
    private int position;
    
    public JsonParseException(String message, int position) {
        super(message + " at position " + position);
        this.position = position;
    }
}

### Step 12.1: Add line/column context for diagnostics

```java
private int line;
private int column;

private void advance(char c) {
    position++;
    if (c == '\n') { line++; column = 1; } else { column++; }
}

private JsonParseException error(String message) {
    return new JsonParseException(message + " (line " + line + ", col " + column + ")", position);
}
```

Integrate `advance` wherever `position++` is used to keep coordinates accurate and throw `error(...)` for richer messages.

// In parser methods:
private void expect(TokenType expected) {
    if (position >= tokens.size()) {
        throw new JsonParseException("Unexpected end of input", position);
    }
    
    Token token = tokens.get(position);
    if (token.type != expected) {
        throw new JsonParseException("Expected " + expected + " but got " + token.type, position);
    }
}
```

## Production hardening checklist
- Enforce single top-level value and fail on trailing non-whitespace characters.
- Set maximum nesting depth to avoid stack overflows.
- Add optional streaming/token-iterator mode for large inputs.
- Decide duplicate-key policy (first-wins, last-wins, or reject).
- Reject non-standard literals (`NaN`, `Infinity`) and invalid number forms.
- Add size limits (input length, string length, array/object entry counts) for safety.
