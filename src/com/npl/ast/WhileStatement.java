package com.npl.ast;

import java.util.List;

public class WhileStatement extends Statement {
    public final Expression condition;
    public final List<Statement> body;
    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }
}