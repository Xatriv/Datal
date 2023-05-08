package org.example.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrExpression implements Expression{
    List<Expression> expressions;
    OrExpression(Expression left, Expression right){
        expressions = new ArrayList<>(Arrays.asList(left, right));
    }
}
