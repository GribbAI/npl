package com.npl.ast;

import java.util.List;

public class Program extends ASTNode {
    public final List<Statement> statements;
    public Program(List<Statement> statements) {
        this.statements = statements;
    }
}