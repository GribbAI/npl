package com.npl.ast;

public class NumberExpression extends Expression {
    public final double value;
    public NumberExpression(double value) {
        this.value = value;
    }
}