package com.npl.ast;

public class StringExpression extends Expression {
    public final String value;

    public StringExpression(String value) {
        this.value = value;
    }
}