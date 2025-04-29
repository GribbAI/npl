package com.npl.ast;

import java.util.List;

public class PrintStatement extends Statement {
    public final List<Expression> expressions;
    public final Expression end;

    public PrintStatement(List<Expression> expressions, Expression end) {
        this.expressions = expressions;
        this.end = end;
    }
}