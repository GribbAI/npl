package com.npl.ast;

import java.util.List;

public class ForStatement extends Statement {
    public final Statement initialization;
    public final Expression condition;
    public final Statement update;

    public final List<Statement> body;

    public ForStatement(Statement initialization, Expression condition, Statement update, List<Statement> body) {
        this.initialization = initialization;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }
}