package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;
import org.example.types.Date;

@AllArgsConstructor
public class DateLiteralExpression implements Expression{
    @Getter
    Date value;
    @Getter
    Position position;
    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
