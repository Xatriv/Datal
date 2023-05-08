package org.example.parser;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class AdditiveExpression implements Expression{
    AdditiveOperator operator;
    Expression leftExpression;
    Expression rightExpression;
}
