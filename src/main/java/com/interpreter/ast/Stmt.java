package com.interpreter.ast;

import java.util.List;

public sealed interface Stmt permits
        Stmt.Assign,
        Stmt.If,
        Stmt.While,
        Stmt.Return,
        Stmt.FunDef,
        Stmt.Sequence {

    record Assign(String name, Expr value) implements Stmt {}

    record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {}

    record While(Expr condition, Stmt body) implements Stmt {}

    record Return(Expr value) implements Stmt {}

    record FunDef(String name, List<String> params, Stmt body) implements Stmt {}

    record Sequence(List<Stmt> statements) implements Stmt {}
}