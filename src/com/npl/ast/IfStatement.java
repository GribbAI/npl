package com.npl.ast;

import java.util.List;

public class IfStatement extends Statement {
    public final Expression condition;
    public final List<Statement> thenBranch;
    public final List<Statement> elseBranch; // null
    public IfStatement(Expression condition, List<Statement> thenBranch, List<Statement> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}