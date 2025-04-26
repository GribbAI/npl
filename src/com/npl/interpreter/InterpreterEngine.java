package com.npl.interpreter;

import com.npl.ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterpreterEngine {
    private final Map<String, Object> variables = new HashMap<>();
    private final StringBuilder output = new StringBuilder();

    public void interpret(Program program) {
        for (Statement stmt : program.statements) {
            execute(stmt);
        }
    }

    public String getOutput() {
        return output.toString();
    }

    private void execute(Statement stmt) {
        if (stmt instanceof PrintStatement) {
            Object val = evaluate(((PrintStatement) stmt).expression);
            output.append(val).append("\n");
        } else if (stmt instanceof AssignmentStatement) {
            AssignmentStatement as = (AssignmentStatement) stmt;
            Object val = evaluate(as.expression);
            variables.put(as.variable, val);
        } else if (stmt instanceof IfStatement) {
            IfStatement is = (IfStatement) stmt;
            Object cond = evaluate(is.condition);
            if (isTruthy(cond)) {
                for (Statement s : is.thenBranch) execute(s);
            } else if (is.elseBranch != null) {
                for (Statement s : is.elseBranch) execute(s);
            }
        } else if (stmt instanceof WhileStatement) {
            WhileStatement ws = (WhileStatement) stmt;
            while (isTruthy(evaluate(ws.condition))) {
                for (Statement s : ws.body) execute(s);
            }
        }
    }

    private Object evaluate(Expression expr) {
        if (expr instanceof NumberExpression) {
            return ((NumberExpression) expr).value;
        } else if (expr instanceof StringExpression) {
            return ((StringExpression) expr).value;
        } else if (expr instanceof VariableExpression) {
            String name = ((VariableExpression) expr).name;
            if (!variables.containsKey(name)) {
                throw new RuntimeException("Undefined variable: " + name);
            }
            return variables.get(name);
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression) expr;
            Object left = evaluate(be.left);
            Object right = evaluate(be.right);
            String op = be.operator;

            if (left instanceof Double && right instanceof Double) {
                double l = (Double) left;
                double r = (Double) right;
                switch (op) {
                    case "+": return l + r;
                    case "-": return l - r;
                    case "*": return l * r;
                    case "/": return l / r;
                    case "==": return (l == r) ? 1.0 : 0.0;
                    case "!=": return (l != r) ? 1.0 : 0.0;
                    case "<":  return (l < r) ? 1.0 : 0.0;
                    case ">":  return (l > r) ? 1.0 : 0.0;
                    case "<=": return (l <= r) ? 1.0 : 0.0;
                    case ">=": return (l >= r) ? 1.0 : 0.0;
                }
            } else if (left instanceof String || right instanceof String) {
                if (op.equals("+")) {
                    return String.valueOf(left) + right;
                } else if (op.equals("==")) {
                    return left.equals(right) ? 1.0 : 0.0;
                } else if (op.equals("!=")) {
                    return !left.equals(right) ? 1.0 : 0.0;
                } else {
                    throw new RuntimeException("Unsupported string operation: " + op);
                }
            }
        }

        throw new RuntimeException("Unknown expression type: " + expr.getClass());
    }

    private boolean isTruthy(Object value) {
        if (value instanceof Double) {
            return (Double) value != 0.0;
        } else if (value instanceof String) {
            return !((String) value).isEmpty();
        }
        return false;
    }
}