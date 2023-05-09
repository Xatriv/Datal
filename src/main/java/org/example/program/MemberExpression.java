package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MemberExpression implements Expression {
    Expression object;
    Expression member;
}
