package org.example.types;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class Period { //TODO check if actually private
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
}
