package com.aspectplusplus.core;

import java.util.*;

public class Parser {
    private List<AspectToken> tokens;
    private int current = 0;
    private CompilationContext context;

    public Parser(List<AspectToken> tokens, CompilationContext context) {
        this.tokens = tokens;
        this.context = context;
    }

    public AST parse() {
        AST ast = new AST();
        AST.ASTNode module = new AST.ASTNode(AST.NodeType.MODULE);
        
        while (!isAtEnd()) {
            try {
                module.addChild(parseDeclaration());
            } catch (Exception e) {
                context.addError("Parse error: " + e.getMessage());
                synchronize();
            }
        }
        
        ast.setRoot(module);
        return ast;
    }

    private AST.ASTNode parseDeclaration() {
        Set<String> modifiers = parseModifiers();
        
        if (match(TokenType.CLASS)) {
            return parseClassDeclaration(modifiers);
        } else if (match(TokenType.FUN)) {
            return parseFunctionDeclaration(modifiers);
        } else if (match(TokenType.VAR, TokenType.VAL)) {
            return parsePropertyDeclaration(modifiers);
        } else {
            throw new AspectCompilationException("Expected declaration");
        }
    }

    private AST.ASTNode parseFunctionDeclaration(Set<String> modifiers) {
        AST.ASTNode function = new AST.ASTNode(AST.NodeType.FUNCTION);
        function.setAttribute("modifiers", modifiers);
        
        String name = consume(TokenType.IDENTIFIER, "Expected function name").getLexeme();
        function.setValue(name);
        
        consume(TokenType.LPAREN, "Expected '(' after function name");
        if (!check(TokenType.RPAREN)) {
            do {
                function.addChild(parseParameter());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expected ')' after parameters");
        
        if (match(TokenType.COLON)) {
            function.addChild(parseType());
        }
        
        function.addChild(parseBlock());
        return function;
    }

    private AST.ASTNode parsePropertyDeclaration(Set<String> modifiers) {
        AST.ASTNode property = new AST.ASTNode(AST.NodeType.PROPERTY);
        property.setAttribute("modifiers", modifiers);
        property.setAttribute("mutable", previous().getType() == TokenType.VAR);
        
        String name = consume(TokenType.IDENTIFIER, "Expected property name").getLexeme();
        property.setValue(name);
        
        if (match(TokenType.COLON)) {
            property.addChild(parseType());
        }
        
        if (match(TokenType.ASSIGN)) {
            property.addChild(parseExpression());
        }
        
        return property;
    }

    private AST.ASTNode parseParameter() {
        AST.ASTNode param = new AST.ASTNode(AST.NodeType.PARAMETER);
        String name = consume(TokenType.IDENTIFIER, "Expected parameter name").getLexeme();
        param.setValue(name);
        
        consume(TokenType.COLON, "Expected ':' after parameter name");
        param.addChild(parseType());
        
        if (match(TokenType.ASSIGN)) {
            param.addChild(parseExpression());
        }
        
        return param;
    }

    private AST.ASTNode parseBlock() {
        AST.ASTNode block = new AST.ASTNode(AST.NodeType.BLOCK);
        
        consume(TokenType.LBRACE, "Expected '{' at start of block");
        
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            block.addChild(parseStatement());
        }
        
        consume(TokenType.RBRACE, "Expected '}' after block");
        
        return block;
    }

    private AST.ASTNode parseStatement() {
        if (match(TokenType.IF)) return parseIfStatement();
        if (match(TokenType.WHILE)) return parseWhileStatement();
        if (match(TokenType.FOR)) return parseForStatement();
        if (match(TokenType.RETURN)) return parseReturnStatement();
        
        return parseExpressionStatement();
    }

    private AST.ASTNode parseIfStatement() {
        AST.ASTNode ifNode = new AST.ASTNode(AST.NodeType.IF);
        
        consume(TokenType.LPAREN, "Expected '(' after 'if'");
        ifNode.addChild(parseExpression());
        consume(TokenType.RPAREN, "Expected ')' after if condition");
        
        ifNode.addChild(parseBlock());
        
        if (match(TokenType.ELSE)) {
            AST.ASTNode elseNode = new AST.ASTNode(AST.NodeType.ELSE);
            elseNode.addChild(parseBlock());
            ifNode.addChild(elseNode);
        }
        
        return ifNode;
    }

    private AST.ASTNode parseWhileStatement() {
        AST.ASTNode whileNode = new AST.ASTNode(AST.NodeType.WHILE);
        
        consume(TokenType.LPAREN, "Expected '(' after 'while'");
        whileNode.addChild(parseExpression());
        consume(TokenType.RPAREN, "Expected ')' after while condition");
        
        whileNode.addChild(parseBlock());
        return whileNode;
    }

    private AST.ASTNode parseForStatement() {
        AST.ASTNode forNode = new AST.ASTNode(AST.NodeType.FOR);
        
        consume(TokenType.LPAREN, "Expected '(' after 'for'");
        String variable = consume(TokenType.IDENTIFIER, "Expected variable name").getLexeme();
        forNode.setValue(variable);
        
        consume(TokenType.IN, "Expected 'in' after variable name");
        forNode.addChild(parseExpression());
        consume(TokenType.RPAREN, "Expected ')' after for condition");
        
        forNode.addChild(parseBlock());
        return forNode;
    }

    private AST.ASTNode parseReturnStatement() {
        AST.ASTNode returnNode = new AST.ASTNode(AST.NodeType.RETURN);
        
        if (!check(TokenType.SEMICOLON)) {
            returnNode.addChild(parseExpression());
        }
        
        return returnNode;
    }

    private AST.ASTNode parseExpressionStatement() {
        AST.ASTNode expr = parseExpression();
        return expr;
    }

    private AST.ASTNode parseExpression() {
        return parseLogicalOr();
    }

    private AST.ASTNode parseLogicalOr() {
        AST.ASTNode expr = parseLogicalAnd();
        
        while (match(TokenType.OR)) {
            TokenType operator = previous().getType();
            AST.ASTNode right = parseLogicalAnd();
            AST.ASTNode binary = new AST.ASTNode(AST.NodeType.BINARY_OP);
            binary.setValue(operator);
            binary.addChild(expr);
            binary.addChild(right);
            expr = binary;
        }
        
        return expr;
    }

    private AST.ASTNode parseLogicalAnd() {
        AST.ASTNode expr = parseEquality();
        
        while (match(TokenType.AND)) {
            TokenType operator = previous().getType();
            AST.ASTNode right = parseEquality();
            AST.ASTNode binary = new AST.ASTNode(AST.NodeType.BINARY_OP);
            binary.setValue(operator);
            binary.addChild(expr);
            binary.addChild(right);
            expr = binary;
        }
        
        return expr;
    }

    private AST.ASTNode parseEquality() {
        AST.ASTNode expr = parseComparison();
        
        while (match(TokenType.EQUALS, TokenType.NOT_EQUALS)) {
            TokenType operator = previous().getType();
            AST.ASTNode right = parseComparison();
            AST.ASTNode binary = new AST.ASTNode(AST.NodeType.BINARY_OP);
            binary.setValue(operator);
            binary.addChild(expr);
            binary.addChild(right);
            expr = binary;
        }
        
        return expr;
    }

    private AST.ASTNode parseComparison() {
        AST.ASTNode expr = parseAdditive();
        
        while (match(TokenType.LESS, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            TokenType operator = previous().getType();
            AST.ASTNode right = parseAdditive();
            AST.ASTNode binary = new AST.ASTNode(AST.NodeType.BINARY_OP);
            binary.setValue(operator);
            binary.addChild(expr);
            binary.addChild(right);
            expr = binary;
        }
        
        return expr;
    }

    private AST.ASTNode parseAdditive() {
        AST.ASTNode expr = parseMultiplicative();
        
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType operator = previous().getType();
            AST.ASTNode right = parseMultiplicative();
            AST.ASTNode binary = new AST.ASTNode(AST.NodeType.BINARY_OP);
            binary.setValue(operator);
            binary.addChild(expr);
            binary.addChild(right);
            expr = binary;
        }
        
        return expr;
    }

    private AST.ASTNode parseMultiplicative() {
        AST.ASTNode expr = parseUnary();
        
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MOD)) {
            TokenType operator = previous().getType();
            AST.ASTNode right = parseUnary();
            AST.ASTNode binary = new AST.ASTNode(AST.NodeType.BINARY_OP);
            binary.setValue(operator);
            binary.addChild(expr);
            binary.addChild(right);
            expr = binary;
        }
        
        return expr;
    }

    private AST.ASTNode parseUnary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            TokenType operator = previous().getType();
            AST.ASTNode expr = parseUnary();
            AST.ASTNode unary = new AST.ASTNode(AST.NodeType.UNARY_OP);
            unary.setValue(operator);
            unary.addChild(expr);
            return unary;
        }
        
        return parsePrimary();
    }

    private AST.ASTNode parsePrimary() {
        if (match(TokenType.INTEGER, TokenType.DOUBLE, TokenType.STRING, TokenType.BOOLEAN)) {
            return new AST.ASTNode(AST.NodeType.LITERAL, previous().getValue());
        }
        
        if (match(TokenType.IDENTIFIER)) {
            return new AST.ASTNode(AST.NodeType.IDENTIFIER, previous().getLexeme());
        }
        
        if (match(TokenType.LPAREN)) {
            AST.ASTNode expr = parseExpression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return expr;
        }
        
        throw new AspectCompilationException("Expected expression");
    }

    private Set<String> parseModifiers() {
        Set<String> modifiers = new HashSet<>();
        while (isModifier(peek())) {
            modifiers.add(advance().getLexeme());
        }
        return modifiers;
    }

    private boolean isModifier(AspectToken token) {
        return token.getType() == TokenType.PUBLIC ||
               token.getType() == TokenType.PRIVATE ||
               token.getType() == TokenType.PROTECTED ||
               token.getType() == TokenType.INTERNAL;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private AspectToken consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new AspectCompilationException(message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private AspectToken advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private AspectToken peek() {
        return tokens.get(current);
    }

    private AspectToken previous() {
        return tokens.get(current - 1);
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;
            switch (peek().getType()) {
                case CLASS:
                case FUN:
                case VAR:
                case VAL:
                case FOR:
                case IF:
                case WHILE:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    private AST.ASTNode parseClassDeclaration(Set<String> modifiers) {
        AST.ASTNode classNode = new AST.ASTNode(AST.NodeType.CLASS);
        classNode.setAttribute("modifiers", modifiers);
        
        String name = consume(TokenType.IDENTIFIER, "Expected class name").getLexeme();
        classNode.setValue(name);
        
        if (match(TokenType.GENERIC_START)) {
            classNode.addChild(parseTypeParameters());
        }
        
        if (match(TokenType.COLON)) {
            while (!check(TokenType.LBRACE)) {
                classNode.addChild(parseType());
                match(TokenType.COMMA);
            }
        }
        
        consume(TokenType.LBRACE, "Expected '{' after class header");
        
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            classNode.addChild(parseDeclaration());
        }
        
        consume(TokenType.RBRACE, "Expected '}' after class body");
        
        return classNode;
    }

    private AST.ASTNode parseType() {
        AST.ASTNode type = new AST.ASTNode(AST.NodeType.TYPE);
        type.setValue(consume(TokenType.IDENTIFIER, "Expected type name").getLexeme());
        
        if (match(TokenType.GENERIC_START)) {
            while (!check(TokenType.GENERIC_END)) {
                type.addChild(parseType());
                match(TokenType.COMMA);
            }
            consume(TokenType.GENERIC_END, "Expected '>' after type arguments");
        }
        
        return type;
    }

    private AST.ASTNode parseTypeParameters() {
        AST.ASTNode params = new AST.ASTNode(AST.NodeType.TYPE_PARAMETER);
        
        do {
            String name = consume(TokenType.IDENTIFIER, "Expected type parameter name").getLexeme();
            AST.ASTNode param = new AST.ASTNode(AST.NodeType.TYPE_PARAMETER);
            param.setValue(name);
            
            if (match(TokenType.COLON)) {
                while (!check(TokenType.COMMA) && !check(TokenType.GENERIC_END)) {
                    param.addChild(parseType());
                    match(TokenType.AND);
                }
            }
            
            params.addChild(param);
        } while (match(TokenType.COMMA));
        
        consume(TokenType.GENERIC_END, "Expected '>' after type parameters");
        return params;
    }
} 