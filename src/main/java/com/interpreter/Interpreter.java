package com.interpreter;

import com.interpreter.ast.Expr;
import com.interpreter.ast.Stmt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
    public Map<String, Integer> interpret(List<Stmt> stmts) {
        for (Stmt stmt : stmts) {
            execute(stmt, globalEnv);
        }
        return globalEnv;
    }
    // helper exception for the function return
    private static class ReturnException extends RuntimeException {
        final int value;
        ReturnException(int value) {
            super(null, null, true, false);
            this.value = value;
        }
    }

    // global : var mapped to its value
    private final Map<String, Integer> globalEnv = new HashMap<>();

    // functions map (name to definition)
    private final Map<String, Stmt.FunDef> functions = new HashMap<>();

    private int evaluate(Expr expr, Map<String, Integer> env) {
        return switch (expr) {

            case Expr.Number n -> n.value();

            case Expr.Variable v -> {
                if (env.containsKey(v.name())) yield env.get(v.name());
                if (globalEnv.containsKey(v.name())) yield globalEnv.get(v.name());
                throw new RuntimeException("Undefined variable: " + v.name());
            }

            case Expr.BinaryOp op -> {
                int left = evaluate(op.left(), env);
                int right = evaluate(op.right(), env);
                yield switch (op.operator().type()) {
                    case PLUS          -> left + right;
                    case MINUS         -> left - right;
                    case STAR          -> left * right;
                    case SLASH         -> left / right;
                    case LESS          -> left < right ? 1 : 0;
                    case LESS_EQUAL    -> left <= right ? 1 : 0;
                    case GREATER       -> left > right ? 1 : 0;
                    case GREATER_EQUAL -> left >= right ? 1 : 0;
                    case EQUAL_EQUAL   -> left == right ? 1 : 0;
                    default -> throw new RuntimeException("Unknown operator: " + op.operator());
                };
            }

            case Expr.Call call -> {
                Stmt.FunDef def = functions.get(call.name());
                if (def == null) throw new RuntimeException("Undefined function: " + call.name());
                // Create local environment with parameters mapped to arguments
                Map<String, Integer> localEnv = new HashMap<>();
                for (int i = 0; i < def.params().size(); i++) {
                    localEnv.put(def.params().get(i), evaluate(call.arguments().get(i), env));
                }

                //execute the body and catch the return value
                try {
                    execute(def.body(), localEnv);
                    throw new RuntimeException("Function " + call.name() + " did not return a value");
                } catch (ReturnException r) {
                    yield r.value;
                }
            }
        };
    }


    private void execute(Stmt stmt, Map<String, Integer> env) {
        switch (stmt) {
            case Stmt.Assign a -> env.put(a.name(), evaluate(a.value(), env));

            case Stmt.If i -> {
                int condition = evaluate(i.condition(), env);
                if (condition != 0) {
                    execute(i.thenBranch(), env);
                } else if (i.elseBranch() != null) {
                    execute(i.elseBranch(), env);
                }
            }

            case Stmt.While w -> {
                while (evaluate(w.condition(), env) != 0) {
                    execute(w.body(), env);
                }
            }

            case Stmt.Return r -> throw new ReturnException(evaluate(r.value(), env));

            case Stmt.FunDef f -> functions.put(f.name(), f);

            case Stmt.Sequence s -> {
                for (Stmt st : s.statements()) {
                    execute(st, env);
                }
            }
        }
    }
}