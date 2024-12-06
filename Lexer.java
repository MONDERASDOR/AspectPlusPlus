package com.aspectplusplus.core;

import java.util.*;

public class Lexer {
    private String source;
    private int position;
    private int line;
    private int column;
    
    private static final Map<String, TokenType> KEYWORDS;
    private static final Map<String, TokenType> OPERATORS;
    
    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("var", TokenType.VAR);
        KEYWORDS.put("val", TokenType.VAL);
        KEYWORDS.put("fun", TokenType.FUN);
        KEYWORDS.put("class", TokenType.CLASS);
        KEYWORDS.put("interface", TokenType.INTERFACE);
        KEYWORDS.put("object", TokenType.OBJECT);
        KEYWORDS.put("data", TokenType.DATA);
        KEYWORDS.put("sealed", TokenType.SEALED);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("when", TokenType.WHEN);
        KEYWORDS.put("match", TokenType.MATCH);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("foreach", TokenType.FOREACH);
        KEYWORDS.put("in", TokenType.IN);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("suspend", TokenType.SUSPEND);
        KEYWORDS.put("async", TokenType.ASYNC);
        KEYWORDS.put("await", TokenType.AWAIT);
        KEYWORDS.put("yield", TokenType.YIELD);
        
        OPERATORS = new HashMap<>();
        OPERATORS.put("+=", TokenType.PLUS_ASSIGN);
        OPERATORS.put("-=", TokenType.MINUS_ASSIGN);
        OPERATORS.put("*=", TokenType.MULTIPLY_ASSIGN);
        OPERATORS.put("/=", TokenType.DIVIDE_ASSIGN);
        OPERATORS.put("==", TokenType.EQUALS);
        OPERATORS.put("!=", TokenType.NOT_EQUALS);
        OPERATORS.put(">=", TokenType.GREATER_EQUAL);
        OPERATORS.put("<=", TokenType.LESS_EQUAL);
        OPERATORS.put("&&", TokenType.AND);
        OPERATORS.put("||", TokenType.OR);
        OPERATORS.put("->", TokenType.ARROW);
        OPERATORS.put("::", TokenType.DOUBLE_COLON);
        OPERATORS.put("?.", TokenType.SAFE_CALL);
        OPERATORS.put("?:", TokenType.ELVIS);
        OPERATORS.put("!!", TokenType.NOT_NULL);
    }
    
    public Lexer(String source) {
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }
    
    public List<AspectToken> tokenize() {
        List<AspectToken> tokens = new ArrayList<>();
        
        while (position < source.length()) {
            char current = source.charAt(position);
            
            if (Character.isWhitespace(current)) {
                skipWhitespace();
            } else if (current == '/' && peek() == '/') {
                skipLineComment();
            } else if (current == '/' && peek() == '*') {
                skipBlockComment();
            } else if (current == '"') {
                tokens.add(scanString());
            } else if (current == '`') {
                tokens.add(scanRawString());
            } else if (isOperatorStart(current)) {
                tokens.add(scanOperator());
            } else if (Character.isDigit(current)) {
                tokens.add(scanNumber());
            } else if (isIdentifierStart(current)) {
                tokens.add(scanIdentifier());
            } else if (current == '@') {
                tokens.add(scanAnnotation());
            } else {
                tokens.add(scanSymbol());
            }
        }
        
        tokens.add(new AspectToken(TokenType.EOF, "", line, column));
        return tokens;
    }
    
    private void skipWhitespace() {
        while (position < source.length() && Character.isWhitespace(source.charAt(position))) {
            if (source.charAt(position) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            position++;
        }
    }
    
    private void skipLineComment() {
        position += 2; // Skip //
        while (position < source.length() && source.charAt(position) != '\n') {
            position++;
        }
    }
    
    private void skipBlockComment() {
        position += 2; // Skip /*
        while (position < source.length() - 1 && 
               !(source.charAt(position) == '*' && source.charAt(position + 1) == '/')) {
            if (source.charAt(position) == '\n') {
                line++;
                column = 1;
            }
            position++;
        }
        position += 2; // Skip */
    }
    
    private AspectToken scanString() {
        StringBuilder builder = new StringBuilder();
        int startColumn = column;
        position++; // Skip opening quote
        
        while (position < source.length() && source.charAt(position) != '"') {
            if (source.charAt(position) == '\\') {
                position++;
                if (position < source.length()) {
                    builder.append(getEscapedChar(source.charAt(position)));
                }
            } else {
                builder.append(source.charAt(position));
            }
            position++;
            column++;
        }
        
        position++; // Skip closing quote
        return new AspectToken(TokenType.STRING, builder.toString(), line, startColumn);
    }
    
    private AspectToken scanRawString() {
        StringBuilder builder = new StringBuilder();
        int startColumn = column;
        position++; // Skip opening backtick
        
        while (position < source.length() && source.charAt(position) != '`') {
            builder.append(source.charAt(position));
            if (source.charAt(position) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            position++;
        }
        
        position++; // Skip closing backtick
        return new AspectToken(TokenType.STRING, builder.toString(), line, startColumn);
    }
    
    private AspectToken scanOperator() {
        StringBuilder builder = new StringBuilder();
        int startColumn = column;
        
        while (position < source.length() && isOperatorChar(source.charAt(position))) {
            builder.append(source.charAt(position));
            position++;
            column++;
        }
        
        String operator = builder.toString();
        TokenType type = OPERATORS.getOrDefault(operator, TokenType.IDENTIFIER);
        return new AspectToken(type, operator, line, startColumn);
    }
    
    private AspectToken scanNumber() {
        StringBuilder builder = new StringBuilder();
        int startColumn = column;
        boolean isFloat = false;
        
        while (position < source.length() && 
               (Character.isDigit(source.charAt(position)) || source.charAt(position) == '.')) {
            if (source.charAt(position) == '.') {
                if (isFloat) break;
                isFloat = true;
            }
            builder.append(source.charAt(position));
            position++;
            column++;
        }
        
        // Handle type suffixes (f, L, etc.)
        if (position < source.length() && isTypeSuffix(source.charAt(position))) {
            builder.append(source.charAt(position));
            position++;
            column++;
        }
        
        return new AspectToken(isFloat ? TokenType.DOUBLE : TokenType.INTEGER, 
                             builder.toString(), line, startColumn);
    }
    
    private AspectToken scanIdentifier() {
        StringBuilder builder = new StringBuilder();
        int startColumn = column;
        
        while (position < source.length() && 
               (Character.isLetterOrDigit(source.charAt(position)) || 
                source.charAt(position) == '_')) {
            builder.append(source.charAt(position));
            position++;
            column++;
        }
        
        String identifier = builder.toString();
        TokenType type = KEYWORDS.getOrDefault(identifier, TokenType.IDENTIFIER);
        return new AspectToken(type, identifier, line, startColumn);
    }
    
    private AspectToken scanAnnotation() {
        position++; // Skip @
        column++;
        int startColumn = column;
        
        StringBuilder builder = new StringBuilder("@");
        while (position < source.length() && 
               (Character.isLetterOrDigit(source.charAt(position)) || 
                source.charAt(position) == '.')) {
            builder.append(source.charAt(position));
            position++;
            column++;
        }
        
        return new AspectToken(TokenType.ANNOTATION, builder.toString(), line, startColumn);
    }
    
    private AspectToken scanSymbol() {
        char symbol = source.charAt(position);
        int startColumn = column;
        position++;
        column++;
        
        switch (symbol) {
            case '(': return new AspectToken(TokenType.LPAREN, "(", line, startColumn);
            case ')': return new AspectToken(TokenType.RPAREN, ")", line, startColumn);
            case '{': return new AspectToken(TokenType.LBRACE, "{", line, startColumn);
            case '}': return new AspectToken(TokenType.RBRACE, "}", line, startColumn);
            case '[': return new AspectToken(TokenType.LBRACKET, "[", line, startColumn);
            case ']': return new AspectToken(TokenType.RBRACKET, "]", line, startColumn);
            case ';': return new AspectToken(TokenType.SEMICOLON, ";", line, startColumn);
            case ',': return new AspectToken(TokenType.COMMA, ",", line, startColumn);
            case '.': return new AspectToken(TokenType.DOT, ".", line, startColumn);
            case ':': return new AspectToken(TokenType.COLON, ":", line, startColumn);
            default: return new AspectToken(TokenType.IDENTIFIER, String.valueOf(symbol), 
                                          line, startColumn);
        }
    }
    
    private char peek() {
        return position + 1 < source.length() ? source.charAt(position + 1) : '\0';
    }
    
    private boolean isOperatorStart(char c) {
        return "+-*/%=!<>&|?:".indexOf(c) != -1;
    }
    
    private boolean isOperatorChar(char c) {
        return "+-*/%=!<>&|?:".indexOf(c) != -1;
    }
    
    private boolean isTypeSuffix(char c) {
        return "fFLl".indexOf(c) != -1;
    }
    
    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }
    
    private char getEscapedChar(char c) {
        switch (c) {
            case 'n': return '\n';
            case 't': return '\t';
            case 'r': return '\r';
            case 'b': return '\b';
            case 'f': return '\f';
            case '\'': return '\'';
            case '"': return '"';
            case '\\': return '\\';
            default: return c;
        }
    }
} 