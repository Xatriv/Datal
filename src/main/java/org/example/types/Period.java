package org.example.types;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class Period {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private Integer second;
    private Long absolutePeriodDifference;

    public static int compare(Period period1, Period period2) {
        // compare looks at absolutePeriodDifference because comparing individual units may lead to ambiguous evaluations
        // e.g. 1M -30D == 0M
        return Long.compare(period1.getAbsolutePeriodDifference(), period2.getAbsolutePeriodDifference());
    }

    private static boolean isDigit(int character) {
        return '9' >= character && character >= '0';
    }

    public static Period fromString(String string) {
        string = string.strip();
        if (string.length() == 0 || !isDigit(string.charAt(0))) {
            return null;
        }
        int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
        int index = 0;
        int sum;
        while (index < string.length()){
            sum = 0;
            while (index < string.length() && Character.isWhitespace(string.charAt(index))){
                index++;
            }
            while (index < string.length() && isDigit(string.charAt(index))) {
                int digit = string.charAt(index) - '0';
                if ((Integer.MAX_VALUE - digit) / 10 < sum) {
                    return null;
                }
                sum = sum * 10 + digit;

                index += 1;
            }
            if (index >= string.length()){
                return null;
            }
            switch (Character.toLowerCase(string.charAt(index))){
                case 'y':
                    year += sum; break;
                case 'm':
                    month += sum; break;
                case 'd':
                    day += sum; break;
                case 'h':
                    hour += sum; break;
                case '\'':
                    minute += sum; break;
                case '"':
                    second += sum; break;
            }
            index++;
        }
        return new Period(year, month, day, hour, minute, second, 0L);
    }


    public Period(Date start, Date end) {
        this.absolutePeriodDifference = end.secondsSinceNewEra() - start.secondsSinceNewEra();
    }

    @Override
    public String toString() {
        return year + "." + month + "." + day + " " + hour + ":" + minute + ":" + second + "+"
                + getAbsolutePeriodDifference() + "sec";
    }

    private Period add(Period period, boolean flip){
        Period original = new Period(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond(), getAbsolutePeriodDifference());
        original.setYear(original.getYear() + (flip ? -1 : 1) * period.getYear());
        original.setMonth(original.getMonth() + (flip ? -1 : 1) * period.getMonth());
        original.setDay(original.getDay() + (flip ? -1 : 1) * period.getDay());
        original.setHour(original.getHour() + (flip ? -1 : 1) * period.getHour());
        original.setMinute(original.getMinute() + (flip ? -1 : 1) * period.getMinute());
        original.setSecond(original.getSecond() + (flip ? -1 : 1) * period.getSecond());
        original.setAbsolutePeriodDifference(original.getAbsolutePeriodDifference() + (flip ? -1 : 1) * period.getAbsolutePeriodDifference());
        return original;
    }

    public Period add(Period period){
        return add(period, false);
    }

    public Period subtract(Period period){
        return add(period, true);
    }

    public Period multiply(Integer factor) {
        Period original = new Period(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond(), getAbsolutePeriodDifference());
        original.setYear(original.getYear() * factor);
        original.setMonth(original.getMonth() * factor);
        original.setDay(original.getDay() * factor);
        original.setHour(original.getHour() * factor);
        original.setMinute(original.getMinute() * factor);
        original.setSecond(original.getSecond() * factor);
        original.setAbsolutePeriodDifference(original.getAbsolutePeriodDifference() * factor);
        return original;
    }

    public Period divide(Integer factor) {
        Period original = new Period(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond(), getAbsolutePeriodDifference());
        original.setYear(original.getYear() / factor);
        original.setMonth(original.getMonth() / factor);
        original.setDay(original.getDay() / factor);
        original.setHour(original.getHour() / factor);
        original.setMinute(original.getMinute() / factor);
        original.setSecond(original.getSecond() / factor);
        original.setAbsolutePeriodDifference(original.getAbsolutePeriodDifference() / factor);
        return original;
    }
}
