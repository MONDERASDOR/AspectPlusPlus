package com.aspectplusplus.core;

import java.util.*;

public class CompilationContext {
    private Map<String, Object> options;
    private List<String> errors;
    private List<String> warnings;

    public CompilationContext() {
        this.options = new HashMap<>();
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public void setOption(String key, Object value) {
        options.put(key, value);
    }

    public Object getOption(String key) {
        return options.get(key);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
} 