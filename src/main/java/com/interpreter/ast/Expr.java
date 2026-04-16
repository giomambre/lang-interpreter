package com.interpreter.ast;

import java.util.List;

public sealed interface Expr permits
        Expr.Number,
        Expr.Variable,
        Expr.BinaryOp,
        Expr.Call {

    record Number(int value) implements Expr {}

    record Variable(String name) implements Expr {}

    record BinaryOp(Expr left, Token operator, Expr right) implements Expr {}

    record Call(String name, List<Expr> arguments) implements Expr {}
}