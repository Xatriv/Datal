package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class Date {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
}
