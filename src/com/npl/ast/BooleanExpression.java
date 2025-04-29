package com.npl.ast;

public class BooleanExpression extends Expression {
    private final boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}