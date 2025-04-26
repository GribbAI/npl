package com.npl.ast;

public class AssignmentStatement extends Statement {
    public final String variable;
    public final Expression expression;
    public AssignmentStatement(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }
}