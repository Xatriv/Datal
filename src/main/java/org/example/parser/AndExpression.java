package org.example.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndExpression implements Expression{
    List<Expression> expressions;
    AndExpression(Expression left, Expression right){
        expressions = new ArrayList<>(Arrays.asList(left, right));
    }
}
