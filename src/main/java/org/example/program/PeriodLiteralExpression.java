package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.types.Period;

import java.util.List;

@AllArgsConstructor
public class PeriodLiteralExpression implements Expression {
    @Getter
    List<Period> value;
}
