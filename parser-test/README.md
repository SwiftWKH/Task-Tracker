# JSON Parser Implementation Guide

## Your Task
Implement the missing methods in `JsonTokenizer.java` and `JsonParser.java`.

## Getting Started

1. **Compile and run tests:**
   ```bash
   cd parser-test
   javac *.java
   java ParserTest
   ```

2. **Start with tokenizer:**
   - Implement `tokenize()` method in `JsonTokenizer.java`
   - Handle each character type: `{`, `}`, `[`, `]`, `,`, `:`, `"`, digits, letters
   - Use the helper methods: `parseString()`, `parseNumber()`, etc.

3. **Then implement parser:**
   - Implement `parseValue()` method in `JsonParser.java`
   - Implement `parseObject()` and `parseArray()` methods
   - Use the helper methods: `expect()`, `currentToken()`, `advance()`

## Test Strategy

The test suite will show you:
- ✓ PASS - Your implementation works
- ✗ FAIL - Shows expected vs actual output
- ✗ ERROR - Shows exception message

Start with simple cases and work up to complex nested structures.

## Files You Need to Implement

- `JsonTokenizer.java` - Convert string to tokens
- `JsonParser.java` - Convert tokens to JsonValue objects

## Files Already Complete

- `TokenType.java` - Enum of all token types
- `Token.java` - Token representation
- `JsonValue.java` - Value container with type safety
- `ParserTest.java` - Comprehensive test suite
- `*.json` - Sample test files

## Implementation Order

1. **Tokenizer basics:** Handle `{`, `}`, `[`, `]`, `,`, `:`
2. **Simple values:** Strings, numbers, booleans, null
3. **Parser basics:** Parse individual values
4. **Objects:** Parse key-value pairs
5. **Arrays:** Parse lists of values
6. **Edge cases:** Empty objects/arrays, escaped strings

## Testing Your Work

Run `java ParserTest` after each step to see your progress. The tests start simple and get progressively more complex.

Good luck!
