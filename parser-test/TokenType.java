public enum TokenType {
    // Structure tokens
    OBJECT_START,    // {
    OBJECT_END,      // }
    ARRAY_START,     // [
    ARRAY_END,       // ]
    COMMA,           // ,
    COLON,           // :
    
    // Value tokens
    STRING,          // "hello"
    NUMBER,          // 42
    BOOLEAN,         // true/false
    NULL             // null
}
