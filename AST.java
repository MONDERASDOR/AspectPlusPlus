package com.aspectplusplus.core;

import java.util.*;

public class AST {
    private ASTNode root;

    public AST() {
        this.root = null;
    }

    public ASTNode getRoot() {
        return root;
    }

    public void setRoot(ASTNode root) {
        this.root = root;
    }

    public static class ASTNode {
        private NodeType type;
        private Object value;
        private List<ASTNode> children;
        private Map<String, Object> attributes;

        public ASTNode(NodeType type) {
            this.type = type;
            this.children = new ArrayList<>();
            this.attributes = new HashMap<>();
        }

        public ASTNode(NodeType type, Object value) {
            this(type);
            this.value = value;
        }

        public NodeType getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public List<ASTNode> getChildren() {
            return children;
        }

        public void addChild(ASTNode child) {
            children.add(child);
        }

        public void setAttribute(String key, Object value) {
            attributes.put(key, value);
        }

        public Object getAttribute(String key) {
            return attributes.get(key);
        }
    }

    public static class Expression extends ASTNode {
        public Expression(NodeType type) {
            super(type);
        }

        public Expression(NodeType type, Object value) {
            super(type, value);
        }
    }

    public enum NodeType {
        // Program structure
        MODULE, IMPORT, PACKAGE,
        
        // Declarations
        CLASS, INTERFACE, FUNCTION, PROPERTY, CONSTRUCTOR,
        
        // Parameters and Types
        PARAMETER, TYPE, TYPE_PARAMETER, TYPE_ARGUMENT,
        
        // Statements
        BLOCK, IF, ELSE, WHILE, FOR, FOREACH, RETURN, THROW, TRY, CATCH,
        
        // Expressions
        BINARY_OP, UNARY_OP, CALL, MEMBER_ACCESS, ARRAY_ACCESS,
        LITERAL, IDENTIFIER, THIS, SUPER, NEW, LAMBDA,
        
        // Aspect-specific
        ASPECT, POINTCUT, ADVICE, JOIN_POINT,
        
        // Modern features
        WHEN, MATCH, CASE, COROUTINE, SUSPEND,
        STRING_TEMPLATE, SMART_CAST, NULL_CHECK,
        
        // Other
        ERROR
    }
} 