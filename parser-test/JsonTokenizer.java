import java.util.*;

public class JsonTokenizer {
    private String json;
    private int position;
    
    public List<Token> tokenize(String json) {
        this.json = json;
        this.position = 0;
        List<Token> tokens = new ArrayList<>();
        
        while(position < json.length()){
            skipWhitespace();
            if(position >= json.length()) break;

            char c = json.charAt(position);

            switch(c){
                case '{': tokens.add(new Token(TokenType.OBJECT_START, "{")); position++; break;
                case '}': tokens.add(new Token(TokenType.OBJECT_END, "}")); position++; break;
                case '[': tokens.add(new Token(TokenType.ARRAY_START, "[")); position++; break;
                case ']': tokens.add(new Token(TokenType.ARRAY_END, "]")); position++; break;
                case ',': tokens.add(new Token(TokenType.COMMA, ",")); position++; break;
                case ':': tokens.add(new Token(TokenType.COLON, ":")); position++; break;
                case '"': tokens.add(parseString()); break;
                default:
                    if(Character.isDigit(c)||c=='-'){
                        tokens.add(parseNumber());
                    } else if (c =='t' || c == 'f'){
                        tokens.add(parseBoolean());
                    } else if ( c == 'n'){
                        tokens.add(parseNull());
                    } else {
                        position++;
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
    
    private Token parseString() {
        position++;
        StringBuilder sb = new StringBuilder();

        while(position < json.length() && json.charAt(position)!='"'){
            char c = json.charAt(position);

            if(c == '\\' && position + 1 < json.length()){
                position++;
                char escaped = json.charAt(position);
                switch(escaped){
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
        position++;
        return new Token(TokenType.STRING, sb.toString());
    }
    
    private Token parseNumber() {
        StringBuilder sb = new StringBuilder();

        if(json.charAt(position) == '-'){
            sb.append('-');
            position++;
        }

        while(position < json.length()){
            char c = json.charAt(position);
            if(Character.isDigit(c) || c =='.'){
                sb.append(c);
                position++;
            } else {
                break;
            }
        }
        return new Token(TokenType.NUMBER, sb.toString());
    }
    
    private Token parseBoolean() {
        if (json.substring(position).startsWith("true")){
            position += 4;
            return new Token(TokenType.BOOLEAN, "true");
        } else if (json.substring(position).startsWith("false")){
            position += 5;
            return new Token(TokenType.BOOLEAN, "false");
        }
        throw new RuntimeException("Invalid boolean at position "+position);
    }
    
    private Token parseNull() {
        if (json.substring(position).startsWith("null")){
            position += 4;
            return new Token(TokenType.NULL, "null");
        }
        throw new RuntimeException("Invalid null at position "+position);
    }
}
