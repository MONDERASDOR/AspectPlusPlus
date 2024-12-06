package com.aspectplusplus.core;

public class AspectCompilationException extends RuntimeException {
    public AspectCompilationException(String message) {
        super(message);
    }
    
    public AspectCompilationException(String message, Throwable cause) {
        super(message, cause);
    }
} 