package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;
import org.example.types.Period;

import java.util.List;

@AllArgsConstructor
public class PeriodLiteralExpression implements Expression {
    @Getter
    List<Period> value;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
