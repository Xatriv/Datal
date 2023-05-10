package org.example.types;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
public class Period {
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
