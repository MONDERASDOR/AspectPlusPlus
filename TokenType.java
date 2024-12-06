package com.aspectplusplus.core;

public enum TokenType {
    // Modern Keywords
    VAR, VAL, FUN, CLASS, INTERFACE, OBJECT, DATA, SEALED,
    IF, ELSE, WHEN, MATCH, WHILE, FOR, FOREACH, IN, 
    RETURN, SUSPEND, ASYNC, AWAIT, YIELD,
    GET, SET, PRIVATE, PUBLIC, PROTECTED, INTERNAL,
    
    // Smart Cast & Null Safety
    NULL, SAFE_CALL, ELVIS, NOT_NULL,
    
    // Lambda and Functional
    LAMBDA, ARROW, PIPE, MAP, FILTER, REDUCE,
    
    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MOD,
    ASSIGN, EQUALS, NOT_EQUALS,
    GREATER, LESS, GREATER_EQUAL, LESS_EQUAL,
    AND, OR, NOT, XOR,
    PLUS_ASSIGN, MINUS_ASSIGN, MULTIPLY_ASSIGN, DIVIDE_ASSIGN,
    
    // Symbols
    LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET,
    SEMICOLON, COMMA, DOT, COLON, DOUBLE_COLON,
    
    // Advanced Types
    INTEGER, DOUBLE, STRING, BOOLEAN, CHAR,
    LIST, SET_TYPE, MAP_TYPE, TUPLE, RANGE,
    
    // Generics & Templates
    GENERIC_START, GENERIC_END, WHERE, IS, AS,
    
    // Coroutines & Async
    SCOPE, LAUNCH, ASYNC_START, AWAIT_EXPR,
    
    // Annotations & Metadata
    AT_SYMBOL, ANNOTATION, META_START, META_END,
    
    // Other
    IDENTIFIER, EOF, COMMENT, DOC_COMMENT
}