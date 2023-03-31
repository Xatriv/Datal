# DATAL - Date And Time Assistance Language
### Author: Maksymilian Łazarski

## 0. Introduction
DATAL is a simple imperative, dynamically- and weakly-typed language with a built-in
support for datetime and duration types.

## 1. Requirements
### Functional
Language provides
* two types of explicit primitives: integer (`int`) and double-precision floating-point (`dbl`)
* datetime type (`dat`), which stores an unsigned int of seconds since UNIX epoch until specified point in time
* period type (`per`), which stores an unsigned int of seconds that represent a specified duration
* built-in members for `dat` and `per` types that allow for extracting time information in different units from the
variable
* boolean logic, e.g. negation conjunction and alternative
* comparisons, e.g. relational operators, equality, inequality
* functions of specified type (one of four previously mentioned)
* built-in print() function that may take multiple parameters
* built-in input() function that accepts text input provided in the command line
* lexical, syntactic and semantic error signaling, complete with pointing to the error location in source

Language ensures
* limited number of parameters (to 64)
* unique identifiers by appending new ones to the map (within each block of code)
* correct interpretation of newline characters through detection of its first occurrence and then recognition of only
  this character. Supported newline

### Nonfunctional
Language will be safe in that it will:
* limit the lengths of identifiers and string literals
* will accept input stream of potentially unlimited length, without storing everything in memory

Language will be convenient thanks to:
* Not stopping lexical or syntactic analysis due to errors, listing multiple error in a single run, until
  specified limit or EOF is reached


## 2. Grammar
### Terminal symbols
```
whitespace      [ |\n|\t]
eol             \n\r|\r\n|\n
digit           [0-9]
double          -?([0-9])+\.([0-9])+
int             -?[0-9]
dateType        [0-9]{1,6}[yY]:[0-9]{1,2}[mM]:[0-9]{1,2}[dD]:[0-9]{1,2}\':[0-9]{1,2}\"
yearUnit        [0-9]{1,6}[yY]
monthUnit       [0-9]{1,2}[mM]
dayUnit         [0-9]{1,8}[dD]
hourUnit        [0-9]{1,8}[hH]
minuteUnit      [0-9]{1,8}\'
secondUnit      [0-9]{1,8}\"
arithmOp        [\+\-\*\-]
andOp           and
orOp            or
gtOp            \>
ltOp            \<
leOp            \<\=
geOp            \>\=
eqOp            \.\=
neOp            \!\=
assignOp        \=
return          return
   
```

### Nonterminal symbols
```ebnf
(*Other terminal symbols that can't be easily expressed in regex*)
char            = ? every UTF-8 character ?;  

(*Non-terminal symbols*)

compOp          = ">"
                | "<"
                | "<="
                | ">="
                | "=="
                | "!=";

specialSymbol   = arithmOp | [\\\(\)\=.\,\!\[\]\(\)\{\}\<\>\|\:\;\'\"] TODO
 
escapeChar      = "\", ["n" | "r" | "t" | "\" | "]" ];

string          = "[", {escapeChar | char-"]"}, "]";

periodType      = dayUnit, [hourUnit], [minuteUnit], [secondUnit]
                | [dayUnit], hourUnit, [minuteUnit], [secondUnit]
                | [dayUnit], [hourUnit], minuteUnit, [secondUnit]
                | [dayUnit], [hourUnit], [minuteUnit], secondUnit;

type            = "int" 
                | "dbl"
                | "dat" 
                | "per";

literal         = int
                | double
                | dateType
                | periodType;


boolOp          = andOp
                | orOp;

ident           = char - (specialSymbol | digit), {char-specialSymbol};

definition      =  type, ident, ["=", (literal | ident)]; 



boolExpr        =  literal | digit , boolOp 
                | ["not"], boolExpr, [boolOp], boolExpr TODO pozbyć się  rekursji

arithmExpr      = expr, arithmOp, expr; TODO pozbyć się  rekursji

comp            = arithmExpr, compOp, arithmExpr; 


condStmt        = "if", "(", boolExpr, ")", "{", {statement}, "}", ["else", "{", {statement}, "}"];

loopStmt        = "while", "(", boolExpr, ")", "{", {statement}, "}";

funcCall        = ident, "(", {type, ident}, ")";

expr            = boolExpr
                | arithmExpr; TODO more

assignment      = ident, "=", 

param           = type, ident, {",", type, ident};

statement       = expr, ";"
                | definition , ";"
                | condition, ";"
                | funcCall, ";"     TODO recursion
                | condStmt          TODO recursion
                | loopStmt          TODO recursion
                | return statement; TODO more

function        = type, ident, "(", [param], ")", "{", {statement}, return, expr, "}"

program         = {function | statement};
```




## 3. Operators precedence and compatibility
| precedence | operator                       | Description                        | Associativity |
|------------|--------------------------------|------------------------------------|---------------|
| 0          | f()  <br /> :                  | Function call <br /> Member access | left          |
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
| dbl | dbl | dbl | !   | per |
| dat | !   | !   | !   | !   |
| per | per | per | !   | !   |

Division

| A\B | int | dbl | dat | per |
|-----|-----|-----|-----|-----|
| int | dbl | dbl | !   | !   |
| dbl | dbl | dbl | !   | !   |
| dat | !   | !   | !   | !   |
| per | per | per | !   | !   |

Note: both multiplication and division with `per` type may cause floating point error no larger than
1 second. That is because of how `dat` and `per` store their values (TODO see Chapter 1. Requirements-Functional) .
Also, one may notice that dat type doesn't support multiplication or division. It is to prevent confusion
with how the user may interpret this type versus how it's actually implemented, while the benefit of implementing
`*` or `/` for `dat` would be negligible.

Addition & Subtraction

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

Note: there is no explicit bool type in DATAL. It only serves purpose in conditional statements and loops.

## 4. Implementation
The functionality of the interpreter will be conducted by a series of modules forming a pipeline:
1. `SourceReader` - accesses the text-based source and reads it character by character, supplying the result to the next
   stage. It also maintains the current position in source, so it can be displayed when an error appears
2. `Lexer` - interface for
3. `CommentFilter` - another lexer with its sole purpose being to create comment tokens. These tokens store text value
   of the comment
4. `Parser` - It will perform construction of Abstract Syntax Tree

Additionally, there will be a series of modules that help maintain clean structure of code
1. `Token` - an enum, which holds information about token's value (if any)
   information about
2. `TokenValue` - TODO interface?
3. `ASTNode` - A module representing a node in Abstract Syntax Tree, created token by token.

In order for the interpreter to function correctly with incorrect input, error modules were created. They allow for
dynamic error signaling without stopping the analysis. Each of them is responsible for a single type of error and can
be passed to the `ErrorHandler`. These errors include (but are not limited to): `IntOverflowError`,
`DoubleOverflowError`, `DateFormatError`, `InvalidTokenError`, `UnlimitedStringLiteralError`, `UnmatchedBracesError`,
`UndefinedIdentifierError` etc.

Seeing as there are multiple parameters for different modules, two config files are provided: one for lexer and one for
parser.

Identifiers will be store in `IdentifierMap`, that are created for each code block. Once the code block stops executing, the map
is discarded. At least one map will be created for each program, and will store global identifiers, i.e. functions and
global variables.



## 5. Testing
Three types of automatic tests will be utilized to ensure correctness on every layer of the language interpreter.
These tests will focus equally on positive cases, where the code is interpreted correctly, and negative, where
an error is expected.

1. Unit tests:
    * Lexer - tokenize input code and check if the resulting tokens are as expected. This will include commend lexer
    * Parser - take a list of tokens and construct AST. Tests will pass if the tree has the same structure as expected
2. Integration tests
    * They will make use of the entire interpreter pipeline - text input through lexer, comment filter and parser.
      This is to ensure that every element is congruent to the rest.
3. Acceptance tests
    * Every code snipped in the following chapter will serve as a form of acceptance tests to ensure that it behaves
      as expected

## 6. Code snippets
Below there are listed different examples of code written in DATAL, both correct (which serve as an introduction to
the language) and incorrect (that demonstrate edge cases and their handling).

### Primitive types
DATAL provides two types of primitives integer and double-precision.
Even though DATAL has no explicit bool type, boolean logic is present.
String type does not exist, however string literal can be passed
to print function with square brackets as delimiters. There are 8 boolean
operators `and` - conjunction, `or` - alternative, `==` - equality,
`!=` - inequality, `<` - less than, `>` - more than, `<=` - less or equal,
`>=` - more or equal
```
int i = 10;
dbl d = 10,2;
i = i + d;              # i equals 20.2

if (i == 20,2 or i != 3) {
    print([Variable d equals: ], d, [!]); # >Variable d equals: 20.2!
}
```

### Dates
Datetime `dat` units are defined from largest to smallest. They need to have a positive year specified at the beginning.
Following units are optional and will default to 0 if not specified. A number of members are
available: `years`, `months`, `days`, `hours`, `minutes`, `seconds`.
```
dat d1 = 2023y:3m:27d:19h:31':10";

dat d2 = 2023y;         # January 1st, 2023 - 0:00:00
print(d2);              # >2023.01.01.00:00:00

print(d1.year);         # >2023
print(d1.month);        # >3
print(d1.day);          # >27
print(d1.hour);         # >19
print(d1.minut);        # >31
print(d1.second);       # >10
```

Incorrect uses
```
dat d3 = 2023y:10d;     # LEXICAL ERROR: (line 1, column 17) failed to construct a token
dat d4 - 1234567y;      # LEXICAL ERROR: (line 2, column 16) failed to construct a token
dat d5 1999y;           # SYNTAX ERROR: (line 3, column 8) unexpected token while building expression: datetime
dat d6 = 2023y:100m:10d:0h:0':0";    # SEMANTIC ERROR: (line 4, column 16) month unit may not exceed 12
dat d7 = 10.2;           # SYNTAX ERROR: (line 5, column 10) unexpected token while building assignment: double
```

### Periods
Period (per) type has similar format, but this time any of the time units may be missing, as long as at least one of
them is provided. However, they need to be provided in order from largest to smallest. A number of members are
available: `days`, `hours`, `minutes`, `seconds`. NOTE: these members do not work the same as in the case of datetime.
```
per p1 = 20d;           # 20 days
per p2 = 230d 3h 10' 5";   # 230 days 3 hours 10 minutes 5 seconds
print(p2);              # >230:03:10:05

print(p1.days);         # >230
print(p1.hours);        # >5523
print(p1.minutes);      # >331390
print(p1.seconds);      # >19883405
```
Incorrect uses
```
per p3 = 4d 7" 3m;      # SYNTAX ERROR: (line 1, column 14) invalid token TODO what type?
```

### Date arithmetics
```
dat d1 = 2023Y 1M 1D;
per p1 = 10D 1'; 
dat d2 = d1 + p1;       # 1023 1M 11D 1';
```

Incorrect uses:
```
per p1 = 10" 1D;        # SYNTAX ERROR: (line 1, column 15) invalid token TODO what type?
per p2 = 10Y;           # SYNTAX ERROR: (line 1, column 12) invalid token TODO what type?
```

### Conditions
```
dat d1 = 2023Y:1M:1D:0h:0':0";
dat d2 = 2024Y;
if (d1 < d2){
    if (d1 > 1999Y;){
        print(d2);      #> 2024.01.01.00:00:00
    }
}
```

Incorrect uses
```
if (int d1 = 2023Y){    # SYNTAX ERROR: (line 1, column 5) unexpected token while building expression: int
    # ...
}
```
```
if () {}                # SYNTAX ERROR: (line 1, column 5) token missing
```
```
if (1+2) {}             # SYNTAX ERROR: (line 1, column 8) unexpected token while building condition: int
```
### Loops
```
int i = 0;
while (i < 10 and 1 < 2){
    print(i);
    i = i + 1;
}
```

Incorrect uses
```
while (int i < 0){      # SYNTAX ERROR: unexpected token while building expression: int
    # ...
}
```

### Functions
```
# Function may be of either primitive type or datetime/period type
dat subtract_10_days(dat date) {
    per ten_days = 10d;
    dat new_date = date - ten_days;
    return new_date;
    return date;        # Anything after first return will be ignored
}
```
Incorrect uses

```
# There are no functions-within-functions in DATAL
int fun1() {
    dbl fun2(){}        # SYNTAX ERROR: (line 2, column 13) unexpected token while building function: function
}
```
```
# there are no first order functions in DATAL
dbl fun3() {}
int fun4() { 
    #this example will stop execution 
    return fun3;        # SEMANTIC ERROR: (line 3, column 15) function identifier returned
}
```
```
# hard limit for the number of parameters is set to 64 TODO should there be one?
int many_args(int a1, int a2, int a3, ..., int a65){
                        # SYNTAX ERROR: (line 1, column x) unexpected token while building parameters
    return 1;           # if there would be no { then the program would not break
}
```
```
# missing bracket
int fun1(){
    print([Unclosed]) 
                        # SYNTAX ERROR: (line 3, column 1) token missing
```
```
int fun1()(){           # SYNTAX ERROR: (line 1, column 11) unexpected token while building function: "("
}
```
```
fun1();                 # SEMANTIC ERROR: (line 1, column 1) unknown identifier
int fun1(){}
```
### Miscellaneous 
Correct users
```
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; int 1
=
3;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
```


```
var dat1 = 2023Y:10M:9D:20H:3':4";
var dat2 = 2023Y:12M:9D:20H:3':4";

var per1 = 2M;

var per2 = dat2 - dat1; 

print(per1.days);
print(per2.days);


----------
var dat1 = 2023Y:10M:9D:20H:3':4";
var dat2 = 2024Y:10M:9D:20H:3':4"; # rok przestępny

var per1 = dat2 - dat 1;
var per2 = 1Y

# nieco ponad 2 lata
per1 < per2; # true or false

per1 = per1 + 1D;
per1 < per2; # true or false
```

TODO
* co będzie u mnie w praktyce oznaczało silne typowanie
  * odp: będzie słabe
* jak deskryptywne powinny być komunikaty błędu? "Missing semicolon"/"Token expected"/"Syntax error (line x, col y)
  * odp: co ma być vs co jest podane (jaki token)
* jak nazwać błędy z warstwy semantycznej
  * odp: później
* w ebnf, czy można od jednego znaku odjąć więcej niż jeden (char-escapeChar)
  * odp: -
* te daty są trudne do wyrażenia w ebnf. Co zrobić np. z dniami w miesiącu albo liczbą dni w roku?
  * odp: 
* czy jest ok, żeby period był tylko w d h ' "?
  * odp: 
* powinienem pozwolić na dowolną ilość parametrów?
  * odp: 
* Czy pozwolić na zmienne globalne
  * odp: 
* mogę nie mieć w takim razie osobnych typów tylko `var` i `def`?
  * odp:
* Period będzie miał dwie możliwe postaci (albo abstrakcyjne wartości jednostek czasu, albo konkretne między różnymi 
  punktami) - zły design 
  * odp:
* Nie lepiej wprowadzić osobnego typu? I ten byłby już ograniczony