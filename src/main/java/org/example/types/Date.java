package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Date {
    @Getter
    private boolean isAD;
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
    @Override
    public String toString() {
        return (isAD ? "AD " : "BC ") + year + "."  + month + "." + day + " " + hour + ":" + minute + ":" + second;
    }
}
