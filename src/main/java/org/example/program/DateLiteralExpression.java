package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.types.Date;

@AllArgsConstructor
public class DateLiteralExpression implements Expression{
    @Getter
    Date value;
}
