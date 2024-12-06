package com.aspectplusplus.core;

import java.util.*;

/**
 * Core module for Aspect++ language implementation
 */
public class AspectModule {
    private String moduleName;
    private List<AspectToken> tokens;
    private Map<String, AspectSymbol> symbolTable;
    private CompilationContext context;

    public AspectModule(String name) {
        this.moduleName = name;
        this.tokens = new ArrayList<>();
        this.symbolTable = new HashMap<>();
        this.context = new CompilationContext();
    }

    public void compile(String sourceCode) throws AspectCompilationException {
        try {
            Lexer lexer = new Lexer(sourceCode);
            tokens = lexer.tokenize();
            
            Parser parser = new Parser(tokens, context);
            AST ast = parser.parse();
            
            SemanticAnalyzer analyzer = new SemanticAnalyzer(ast, symbolTable);
            analyzer.analyze();
            
            CodeGenerator generator = new CodeGenerator(ast, context);
            generator.generate();
        } catch (Exception e) {
            throw new AspectCompilationException("Compilation failed: " + e.getMessage());
        }
    }
} 