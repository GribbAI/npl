package com.npl.interpreter;

import com.npl.ast.*;
import com.npl.lexer.TokenType;

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
            List<Expression> expressions = ((PrintStatement) stmt).expressions;
            for (Expression expr : expressions) {
                Object val = evaluate(expr);
                output.append(val);
            }
            Object end = evaluate(((PrintStatement) stmt).end);
            output.append(end);
        } else if (stmt instanceof AssignmentStatement) {
            AssignmentStatement as = (AssignmentStatement) stmt;
            Object val = evaluate(as.expression);
            variables.put(as.variable, val);
        } else if (stmt instanceof IfStatement) {
            IfStatement is = (IfStatement) stmt;
            Object cond = evaluate(is.condition);
            if (isTruthy(cond)) {
                for (Statement s : is.thenBranch) {
                    execute(s);
                }
            } else if (is.elseBranch != null) {
                for (Statement s : is.elseBranch) {
                    execute(s);
                }
            }
        } else if (stmt instanceof WhileStatement) {
            WhileStatement ws = (WhileStatement) stmt;
            while (isTruthy(evaluate(ws.condition))) {
                for (Statement s : ws.body) {
                    execute(s);
                }
            }
        } else if (stmt instanceof ForStatement) {
            ForStatement fs = (ForStatement) stmt;
            execute(fs.initialization);
            while (isTruthy(evaluate(fs.condition))) {
                for (Statement s : fs.body) {
                    execute(s);
                }
                execute(fs.update);
            }
        } else if (stmt instanceof PostfixExpressionStatement) {
            PostfixExpressionStatement ps = (PostfixExpressionStatement) stmt;
            Object val = variables.get(ps.getVarName());
            if (val == null) {
                throw new RuntimeException("Undefined variable: " + ps.getVarName());
            }
            if (!(val instanceof Double)) {
                throw new RuntimeException("Only numbers can be incremented/decremented.");
            }
            double num = (Double) val;
            if (ps.getOp() == TokenType.INCREMENT) {
                variables.put(ps.getVarName(), num + 1);
            } else if (ps.getOp() == TokenType.DECREMENT) {
                variables.put(ps.getVarName(), num - 1);
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
        } else if (expr instanceof BooleanExpression) {
            return ((BooleanExpression) expr).getValue() ? 1.0 : 0.0;
        } else if (expr instanceof NoneExpression) {
            return null;
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression) expr;
            Object left = evaluate(be.left);
            Object right = evaluate(be.right);
            String op = be.operator;

            if (left instanceof Double && right instanceof Double) {
                double l = (Double) left;
                double r = (Double) right;
                switch (op) {
                    case "+":  return l + r;
                    case "-":  return l - r;
                    case "*":  return l * r;
                    case "/":  return l / r;
                    case "==": return (l == r) ? 1.0 : 0.0;
                    case "!=" : return (l != r) ? 1.0 : 0.0;
                    case "<":  return (l < r)  ? 1.0 : 0.0;
                    case ">":  return (l > r)  ? 1.0 : 0.0;
                    case "<=": return (l <= r) ? 1.0 : 0.0;
                    case ">=": return (l >= r) ? 1.0 : 0.0;
                    case "and": return (l != 0.0 && r != 0.0) ? 1.0 : 0.0;
                    case "or":  return (l != 0.0 || r != 0.0) ? 1.0 : 0.0;
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
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return value != null;
    }
}
