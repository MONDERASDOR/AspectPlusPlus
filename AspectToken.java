package com.aspectplusplus.core;

public class AspectToken {
    private TokenType type;
    private String value;
    private int line;
    private int column;

    public AspectToken(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getLexeme() { return value; }
} 