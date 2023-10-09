# DATAL - Date And Time Assistance Language

### Author: Maksymilian Łazarski

## 0. Introduction

DATAL is a simple imperative, dynamically- and weakly-typed language with a built-in support for datetime and period
types. The project involves a language interpreter that can be customized, depending on the needs of the user.

## 1. Requirements

### Functional

Language provides

* two types of primitives: integer and double-precision floating-point
* implicit boolean type, which is a result of multiple boolean operations, and which can be used in conditional
  expressions, loops and prints.
* date type, which stores a series of independent unsigned integers that represent a date composed of different time
  units from year (with era included) to second.
* period type, which stores an information about time difference between two points in time, or explicit difference
  specified by the user
* built-in members for datetime and period types that allow for extracting time information in different units from the
  variable
* boolean logic, i.e. negation, conjunction and alternative
* comparisons, i.e. relational operators
* functions, including:
    * built-in print() function that may take multiple parameters
    * built-in readString(), readInt(), readDouble(), readDate() and readPeriod() functions that accept text input
      provided in the command line
* lexical, syntactic and semantic error signaling, complete with pointing to the error location in source

Language ensures

* unique identifiers by appending new ones to the map (within each block of code)
* correct interpretation of newline characters through detection of its first occurrence and then recognition of only
  this character. Supported newline characters are: \n, \n\r, \r\n

### Nonfunctional

Language will be safe in that it will:

* allow for limiting the lengths of identifiers and string literals
* will accept input stream of potentially unlimited length, without storing everything in memory

Language will be convenient thanks to:

* Not stopping lexical or syntactic analysis due to low severity exceptions, listing multiple warnings in a single run,
  until specified limit or EOF is reached

## 2. Grammar

### Terminal symbols

```
whitespace      [ |\n|\t]
eol             \n\r|\r\n|\n
digit           [0-9]
intLiteral      0|-?[1-9][0-9]*
doubleLiteral   -?([0-9])+\.([0-9])+
dateLiteral     [1-9][0-9]*([yY]|AD|BC):[1-9][0-9]?[mM]:[1-9][0-9]?[dD]:[1-9][0-9]?[hH]:[1-9][0-9]?\':[1-9][0-9]?\"
yearUnit        [0-9][yY]
monthUnit       [0-9][mM]
dayUnit         [0-9][dD]
hourUnit        [0-9][hH]
minuteUnit      [0-9]\'
secondUnit      [0-9]\"
plus            \+
minus           \-
multiply        \*
divide          \\
andOp           and
orOp            or
notOp           not
assignOp        \=
memberOp        \.
separOp         \,
if              if
else            else
while           while
parOpen         "("
parClose        ")"
strDelimL       "["
strDelimR       "]"
blockOpen       "{"
blockClose      "}"
stmtEnd         ";"
return          return
escapeChar      \\[n|r|t|f|b|\\|]);
compOp          [\>|\<|\<\=|\>\=|\=\=|\!\=]
   
```

### Nonterminal symbols

```ebnf
(*Other terminal symbols that can't be easily expressed in regex*)
char            = ? every UTF-8 character ?;  

(*Non-terminal symbols*)
specialSymbol   = addOp | multOp | assignOp | memberOp | "\\" | "!" | ":" | ";" | "'" | "\"" | "," | "" | "" 
                | "(" | ")" | "[" | "]" | "{" | "}" | "<" | ">";
additiveOp      = plus | minus;
multiplOp       = multiply | divide 
negationOp      = notOp | minus; 

stringChar      = escapeChar | char;
stringLiteral   = strDelimL, {stringChar}, strDelimR;


durationLiteral = yearUnit, [monthUnit], [dayUnit], [hourUnit], [minuteUnit], [secondUnit]
                | monthUnit, [dayUnit], [hourUnit], [minuteUnit], [secondUnit]
                | dayUnit, [hourUnit], [minuteUnit], [secondUnit]
                | hourUnit, [minuteUnit], [secondUnit]
                | minuteUnit, [secondUnit]
                | secondUnit;
                
objectLiteral   = dateLiteral
                | durationLiteral;

simpleLiteral   = intLiteral
                | doubleLiteral
                | stringLiteral;


ident           = char - (specialSymbol | digit), {char-specialSymbol};
identOrFuncCall = ident, [parOpen, [args], parClose];

simpleValue     = simpleLiteral
                | parOpen, expr, parClose;                
objectValue     = objectLiteral
                | identOrFuncCall;
 
memberExpr      = objectValue, {memberOp, identOrFuncCall};
assignExpr      = memberExpr, [assignOp, expr];
negExpr         = [negOp], (memberExpr | simpleValue);
multExpr        = negExpr, {multOp, negExpr};
addExpr         = multExpr, {addOp, multExpr};
compExpr        = addExpr, [compOp, addExpr];
andExpr         = compExpr, {andOp, compExpr};
orExpr          = andExpr, {orOp, andExpr};
expr            = orExpr;

condStmt        = if, parOpen, expr, parClose, block, [else, block];
loopStmt        = while, parOpen, expr, parClose, block;

args            = expr, {separOp, expr};
returnStmt      = return, [expr], stmtEnd;

statement       = expr, stmtEnd
                | condStmt
                | loopStmt
                | returnStmt;

block           = blockOpen, {statement}, blockClose;

params          = ident, {separOp, ident};
function        = ident, parOpen, [params], parClose, block;

program         = {function};
```

## 3. Operators precedence and compatibility

| precedence | operator                       | Description                        | Associativity |
|------------|--------------------------------|------------------------------------|---------------|
| 0          | f()  <br /> .                  | Function call <br /> Member access | left          |
| 1          | not                            | Negation                           | right         |
| 2          | *  <br /> /                    | Multiplication <br /> Division     | left          |
| 3          | +  <br /> -                    | Addition <br /> Subtraction        | left          |
| 4          | == <br /> !=                   | Equality <br /> Inequality         | left          |
| 5          | < <br /> <= <br /> > <br /> => | Relational operators               | left          |
| 6          | and                            | Conjunction                        | left          |
| 7          | or                             | Alternative                        | left          |
| 8          | =                              | Assignment                         | right         |
| 9          | ,                              | Argument separator                 | left          |

#### Arithmetic operation compatibility

Table presents which DATAL types are compatible with each other. Each cell contains result data of an A[operator]B
expression (A is listed in the first column and B in the first row) type if they are compatible or "!" if
they aren't.

##### Multiplication

| A\B | int | dbl | dat | per |
|-----|-----|-----|-----|-----|
| int | int | dbl | !   | per |
| dbl | dbl | dbl | !   | !   |
| dat | !   | !   | !   | !   |
| per | per | !   | !   | !   |

Division

| A\B | int | dbl | dat | per |
|-----|-----|-----|-----|-----|
| int | dbl | dbl | !   | !   |
| dbl | dbl | dbl | !   | !   |
| dat | !   | !   | !   | !   |
| per | per | !   | !   | !   |

Note:
One may notice that dat type doesn't support multiplication or division. It is to prevent confusion
with how the user may interpret this type versus how it's actually implemented, while the benefit of implementing
`*` or `/` for datetime would be negligible.

Addition

| A\B | int | dbl | dat | per |
|-----|-----|-----|-----|-----|
| int | int | dbl | !   | !   |
| dbl | dbl | dbl | !   | !   |
| dat | !   | !   | !   | dat |
| per | !   | !   | !   | per |

Subtraction

| A\B | int | dbl | dat | per |
|-----|-----|-----|-----|-----|
| int | int | dbl | !   | !   |
| dbl | dbl | dbl | !   | !   |
| dat | !   | !   | !   | dat |
| per | !   | !   | !   | per |

Relational operators

| A\B | int  | dbl  | dat  | per  |
|-----|------|------|------|------|
| int | bool | bool | !    | !    |
| dbl | bool | bool | !    | !    |
| dat | !    | !    | bool | !    |
| per | !    | !    | !    | bool |

Note: there is no bool type in DATAL. It only serves purpose in conditional statements and loops.

## 4. Implementation

The functionality of the interpreter is conducted by a series of modules forming a pipeline:

1. `Source` - interface for accessing the text-based source and reading it character by character, supplying the result
   to the next stage
2. `CodeLexer` - performs lexical analysis. It also maintains the current position in source, so it can be displayed
   when an error appears
3. `CommentFilter` - another lexer with its sole purpose being to filter out comment tokens
4. `Parser` - It performs syntactic analysis and creates a tree of objects, that represents a logical structure of the
   program

Additionally, there is a series of modules that help maintain clean structure of code

1. `Token` - an interface for all types of tokens produced by the CodeLexer. Its implementations hold information about
   token's value (if any) and also `TokenType` in form of enum.
2. `TokenType` - enum which is used to determine the type of token by the Parser. Token types include but are not
   limited to: integers, doubles, dates, periods, and many others.
3. `Program` - a root of every program tree representation. It implements `Visitable` interface. It is essentially a
   list of functions
4. `Visitable` - interface used by `ProgramVisitor`, which together form a "Visitor pattern". Every child of any given
   node is visited recursively until terminal expressions are reached. It is implemented by every `Expression`
   and `Statement`
5. `Expression` - a structure that, when visited, will evaluate to something. It's result can then be used by
   other `Expressions` or `Statements`
6. `Statement` - a structure that represents an action, that a `Program` will perform. All 4 implementations are:
    * `ExpressionStatement` - any expression followed by a semicolon. This includes assignments and function calls.
    * `IfStatement` - conditional statement. It has an optional "else" block
    * `WhileStatement` - simple loop statement
    * `ReturnStatement` - a statement that ends execution of current function call and returns the value. The value may
      be null, but it will result in an Interpreter error if accessed.

There is no global scope in DATAL. Every statement has to be enclosed within a function. The entrypoint to every program
is `main()` function. It will throw an error if absent.

In order for the interpreter to function correctly with incorrect input, error modules were created. They allow for
dynamic error signaling without stopping the analysis. Each of them is responsible for a single type of error and can
be passed to the `ErrorHandler`. These errors are divided into 3 categories: `Errors`, `Warning`, `Infos`. `Errors` are
critical and reaching them will terminate the execution immediately. `Warnings` are temporarily acceptable exceptions,
and they can occur as many times as specified in `error.properties` file. `Infos` are harmless occurrences, which do not
contribute to overall error counter.

Seeing as there are multiple parameters for different modules, two config files are provided: one for lexer and one for
parser.

1. `lexer.properties` includes: max length of identifier, max length of string literal, max length of a comment
2. `parser.properties` includes: max number of parameters in a function, number of unknown tokens in a row before an
   error is thrown
3. `error.properties` includes: max number of warnings before an error

Identifiers are stored in `BlockContext`, that are created for each code block. A list of these blocks is assigned to
FunctionCallContext. Once the interpreter leaves the scope of that context, the map is discarded.

## 5. Testing

Three types of automatic tests will be utilized to ensure correctness on every layer of the language interpreter.
These tests will focus equally on positive cases, where the code is interpreted correctly, and negative, where
an error is expected.

1. Unit tests:
    * Lexer - tokenize input code and check if the resulting tokens are as expected. This will include commend lexer
    * Parser - take a list of tokens and construct AST. Tests will pass if the tree has the same structure as expected
2. Integration tests
    * They will make use of the entire interpreter pipeline - text input through lexer, comment filter, parser and
      interpreter. This is to ensure that every element is congruent to the rest.
3. Acceptance tests
    * Every code snipped in the following chapter will serve as a form of acceptance tests to ensure that it behaves
      as expected

## 6. Code snippets

Below there are listed different examples of code written in DATAL, both correct (which serve as an introduction to
the language) and incorrect (that demonstrate edge cases and their handling).

### Primitive types

DATAL provides two types of primitives integer and double-precision. Even though DATAL has no explicit bool type,
boolean logic is present. String type does not exist, however string literal can be passed to print function with square
brackets as delimiters. There are 8 boolean operators `and` - conjunction, `or` - alternative, `==` - equality,
`!=` - inequality, `<` - less than, `>` - more than, `<=` - less or equal, `>=` - more or equal

```
main() {
    i = 10;
    d = 10.2;
    i = i + d;              # i equals 20.2
    
    if (i == 20.2 or i != 3) {
        print([Variable d equals: ], d, [!]); # >Variable d equals: 20.2!
    }
}
```

### Dates

Datetime units are defined from largest to smallest. Each of them needs to be specified, when initialized. A number of
members are available: `years`, `months`, `days`, `hours`, `minutes`, `seconds`.

```
main() {
    
    d1 = 2023y:3m:27d:19h:31':10";
    print(d1);              # >2023.03.27.19:31:10
    
    print(d1.year);         # >2023
    print(d1.month);        # >3
    print(d1.day);          # >27
    print(d1.hour);         # >19
    print(d1.minute);       # >31
    print(d1.second);       # >10
}
```

Incorrect uses

```
d2 = 2023y:10d;         # LEXICAL ERROR: (line 1, column 17) failed to construct a token: dateLiteral
d3 = 1234567y:;         # LEXICAL ERROR: (line 2, column 16) failed to construct a token: dateLiteral
d4 1999y;               # SYNTAX ERROR: (line 3, column 8) unexpected token while building expression: dateLiteral
d5 = 2023y:100m:10d:0h:0':0";    # SEMANTIC ERROR: (line 4, column 16) month unit may not exceed 12
d6 = 10.2;              # SYNTAX ERROR: (line 5, column 10) unexpected token while building assignment: double
```

### Durations

Duration type has similar format, but this time any of the time units may be missing, as long as at least one of
them is provided. However, they need to be provided in order from largest to smallest. A number of members are
available: `years`, `months`, `days`, `hours`, `minutes`, `seconds`. NOTE: these members may mean different time spans,
when created through subtraction of two datetimes and through providing literals.

```
p1 = 20d;               # 20 days
p2 = 230d 3h 10' 5";    # 230 days 3 hours 10 minutes 5 seconds
print(p2);              # >230:03:10:05

print(p1.days);         # >230
print(p1.hours);        # >5523
print(p1.minutes);      # >331390
print(p1.seconds);      # >19883405
```

Incorrect uses

```
p3 = 4d 7" 3m;          # SYNTAX ERROR: (line 1, column 14) unexpected token while building durationLiteral: monthUnit
```

### Date arithmetics

```
d1 = 2023Y:1M:1D:0H:0':0";
p1 = 10D 1'; 
d2 = d1 + p1;           # 2023 1M 11D 1';
p2 = p1 / 2;            # 5d;
```

Incorrect uses:

```
p1 = 10Y 5Y:10M:3D:10h:20':30";
# SYNTAX ERROR: (line 1, column 15) unexpected token while building durationLiteral: dateLiteral
```

### Conditions

```
d1 = 2023Y:1M:1D:0h:0':0";
d2 = 2024Y:1M:1D:0h:0':0";
if (d1 < d2){
    if (d1 > 1999Y){
        print(d2);      #> 2024.01.01.00:00:00
    }
}
```

Incorrect uses

```
if (d1 = 2023Y){        # SYNTAX ERROR: (line 1, column 5) unexpected token while building expression: assignment
    # ...
}
```

```
if () {}                # SYNTAX ERROR: (line 1, column 5) expression missing
```

```
if (1+2) {}             # SYNTAX ERROR: (line 1, column 8) unexpected token while building condition: addExpr
```

### Loops

```
i = 0;
while (i < 10 and 1 < 2){
    print(i);
    i = i + 1;
}
```

Incorrect uses

```
while (int i < 0){      # SYNTAX ERROR: (line 1, column 8) unexpected token while building expression: int
    # ...
}
```

### Functions

```
# Function may be of either primitive type or datetime/duration type
subtract_10_days(date) {
    ten_days = 10d;
    new_date = date - ten_days;
    return new_date;
    return date;        # Anything after first return will be ignored
}
```

Incorrect uses

```
# There are no functions-within-functions in DATAL
fun1() {
    fun2(){}            # SYNTAX ERROR: (line 2, column 13) unexpected token while building function: function
}
```

```
# there are no first order functions in DATAL
fun3() {}
fun4() { 
    #this example will stop execution 
    return fun3;        # SEMANTIC ERROR: (line 3, column 15) function identifier returned
}
```

```
fun5(){
    return 1            # SYNTAX ERROR: (line 3, column 1) unexpected token while building returnStatement: "{"
}
```

```
# missing bracket
fun1(){
    print([Unclosed]) 
                        # SYNTAX ERROR: (line 3, column 1) token missing "{"
```

```
fun1()(){               # SYNTAX ERROR: (line 1, column 11) unexpected token while building function: "("
}
```

```
fun1();                 # SEMANTIC ERROR: (line 1, column 1) unknown identifier: fun1
fun1(){}
```