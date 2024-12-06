package com.aspectplusplus.compiler;

import java.util.*;

public class CompilerConfig {
    private OptimizationLevel optimizationLevel;
    private TargetPlatform targetPlatform;
    private Set<CompilerFeature> enabledFeatures;
    private Map<String, String> compilerOptions;
    
    public enum OptimizationLevel {
        NONE, BASIC, AGGRESSIVE, ULTRA
    }
    
    public enum TargetPlatform {
        JVM, NATIVE, WASM, LLVM
    }
    
    public enum CompilerFeature {
        COROUTINES,
        TAIL_RECURSION,
        INLINE_FUNCTIONS,
        REIFIED_GENERICS,
        SMART_CASTING,
        PATTERN_MATCHING,
        META_PROGRAMMING,
        HOT_RELOAD,
        DEBUG_SYMBOLS,
        SOURCE_MAPS
    }
    
    private CompilerConfig() {
        this.enabledFeatures = EnumSet.allOf(CompilerFeature.class);
        this.compilerOptions = new HashMap<>();
    }
    
    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static class Builder {
        private final CompilerConfig config;
        
        private Builder() {
            config = new CompilerConfig();
        }
        
        public Builder optimizationLevel(OptimizationLevel level) {
            config.optimizationLevel = level;
            return this;
        }
        
        public Builder targetPlatform(TargetPlatform platform) {
            config.targetPlatform = platform;
            return this;
        }
        
        public CompilerConfig build() {
            return config;
        }
    }
} 