package com.npl.ast;

public class BinaryExpression extends Expression {
    public final Expression left;
    public final String operator;
    public final Expression right;
    
    public BinaryExpression(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}