package com.aspectplusplus.core;

public class AspectSymbol {
    private String name;
    private String type;
    private int scope;
    private boolean isConstant;

    public AspectSymbol(String name, String type) {
        this.name = name;
        this.type = type;
        this.scope = 0;
        this.isConstant = false;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public void setConstant(boolean constant) {
        isConstant = constant;
    }
} 