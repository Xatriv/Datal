package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
public class Date {
    @Getter
    private boolean isAC;
    @Getter
    private int year;
    @Getter
    private int month;
    @Getter
    private int day;
    @Getter
    private int hour;
    @Getter
    private int minute;
    @Getter
    private int second;
}
