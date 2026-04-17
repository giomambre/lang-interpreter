# lang-interpreter

A tree-walking interpreter for a small, dynamically-typed imperative language, written in Java 23 with Gradle.

---

## Language Overview

The language supports assignments, arithmetic, conditionals, loops, and first-class functions with recursion.
All values are integers. Comments use `//` syntax.

### Assignments

```
x = 2
y = (x + 2) * 2
```

### Conditionals

```
if x > 10 then y = 100 else y = 0
```

### Loops

```
while x < 3 do x = x + 1
```

### Functions

```
fun add(a, b) { return a + b }
result = add(2, 2)
```

### Recursion

```
fun fact(n) { if n <= 0 then return 1 else return n * fact(n - 1) }
a = fact(5)
```

### Multiple statements in a body

Statements inside `{}` or after `do` are separated by commas:

```
fun f(n) { r = 1, while n > 0 do r = r * n, n = n - 1 }
```

### Comments

Single-line comments using `//`:

```
// this is a comment
x = 2  // inline comment
```

---

## Supported Operators

| Category       | Operators              |
|----------------|------------------------|
| Arithmetic     | `+`, `-`, `*`, `/`     |
| Comparison     | `<`, `<=`, `>`, `>=`, `==` |
| Assignment     | `=`                    |

---

## Language Design Decisions

The task provides sample programs but no formal specification. The following decisions were made to fill the gaps:

- **Integer-only values.** All values are integers. There are no floats, strings, or booleans.
- **`true` is `1`.** The keyword `true` evaluates to `1`. Conditions are truthy if non-zero, falsy if zero — consistent with C semantics.
- **Local scope in functions.** Variables assigned inside a function body are local to that call and do not affect the global environment. Parameters are also local.
- **Global variables only in output.** At program termination, only global variables are printed. Function-local variables and function definitions are not included in the output.
- **Alphabetical output order.** Variables are printed in alphabetical order, since the task does not specify an order. This ensures deterministic, readable output.
- **Comma as statement separator.** Multiple statements inside `{}` bodies or after `do` are separated by `,`. This is inferred directly from the sample programs.
- **No `for`, `switch`, or unary operators.** These constructs do not appear in the sample programs and are not supported.
- **Single-line comments with `//`.** Chosen as a natural extension consistent with the Java/C family syntax style of the language.

---

## How to Build and Run

**Requirements:** Java 17+ and no additional dependencies (Gradle wrapper is included).

**Build:**

```bash
./gradlew build
```

**Run** (reads source program from standard input):

```bash
echo "x = 2
y = (x + 2) * 2" | ./gradlew --quiet run
```

On Windows PowerShell:

```powershell
Get-Content program.txt | .\gradlew --quiet run
```

**Output format:**

```
x: 2
y: 8
```

---

## How to Run Tests

```bash
./gradlew test
```

All 13 test cases are included in `InterpreterTest.java`, covering:
- Simple assignment and arithmetic
- If/then/else
- While with nested if and comma-separated body
- Function calls
- Recursive factorial
- Iterative factorial
- Single-line comments
- If without else branch (condition false — variable unchanged)
- Nested function calls (a function calling another function)
- Negative arithmetic results
- While loop with condition false from the start (body never executes)
- Multiple reassignments of the same variable
- Program consisting entirely of comments (empty output)

---

## Project Structure

```
src/
├── main/java/com/interpreter/
│   ├── Main.java           # Entry point — reads stdin, runs pipeline, prints output
│   ├── Lexer.java          # Tokenizes source text into a List<Token>
│   ├── Parser.java         # Builds an AST from the token list (recursive descent)
│   ├── Interpreter.java    # Walks the AST and executes the program
│   └── ast/
│       ├── Token.java      # Token record: type + optional value
│       ├── TokenType.java  # Enum of all token types
│       ├── Expr.java       # Sealed interface for expression nodes
│       └── Stmt.java       # Sealed interface for statement nodes
└── test/java/com/interpreter/
    └── InterpreterTest.java  # JUnit 5 tests for all sample programs
```

---

## Architecture

The interpreter is structured as a classic three-phase pipeline:

```
Source text  →  Lexer  →  List<Token>  →  Parser  →  AST  →  Interpreter  →  Output
```

### Lexer

Scans the source text character by character and produces a flat list of tokens. Handles multi-character operators (`==`, `<=`, `>=`) via one-character lookahead. Keywords are matched against a fixed map; everything else is an `IDENTIFIER`.

### Parser

Implements a hand-written **recursive descent parser**. Expression parsing uses a method hierarchy to encode operator precedence correctly:

```
parseComparison()  →  parseExpression()  →  parseTerm()  →  parsePrimary()
     <, <=, ...           +, -                  *, /          literals, calls
```

### AST Nodes

All AST node types are defined as `record` implementations of two **sealed interfaces** — `Expr` and `Stmt`. The `sealed` modifier guarantees that all cases are handled exhaustively in `switch` expressions, producing a compile-time error if a new node type is added without being handled.

### Interpreter

A tree-walking interpreter with two core methods:
- `evaluate(Expr, env)` — recursively evaluates an expression and returns an `int`
- `execute(Stmt, env)` — executes a statement, potentially modifying the environment

Function calls create an isolated local environment. `return` statements are implemented via a `ReturnException` — an unchecked exception that unwinds the call stack back to the call site cleanly without requiring `throws` declarations throughout the codebase.