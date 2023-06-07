package org.example.types;

import org.example.interpreter.ValueReference;


public class Period {
    private final ValueReference year;
    private final ValueReference month;
    private final ValueReference day;
    private final ValueReference hour;
    private final ValueReference minute;
    private final ValueReference second;
    private final ValueReference absolutePeriodDifference;

    public ValueReference getYearReference(){
        return year;
    }
    public ValueReference getMonthReference(){
        return month;
    }
    public ValueReference getDayReference(){
        return day;
    }
    public ValueReference getHourReference(){
        return hour;
    }
    public ValueReference getMinuteReference(){
        return minute;
    }
    public ValueReference getSecondReference(){
        return second;
    }

    public ValueReference getAbsolutePeriodDifferenceReference(){
        return absolutePeriodDifference;
    }

    public void setYear(Integer year){
        this.year.setValue(year);
    }
    public void setMonth(Integer month){
        this.month.setValue(month);
    }
    public void setDay(Integer day){
        this.day.setValue(day);
    }
    public void setHour(Integer hour){
        this.hour.setValue(hour);
    }
    public void setMinute(Integer minute){
        this.minute.setValue(minute);
    }
    public void setSecond(Integer second){
        this.second.setValue(second);
    }

    public void setAbsolutePeriodDifference(Long seconds){
        this.absolutePeriodDifference.setValue(seconds);
    }

    private Integer getYear(){
        return (Integer) getYearReference().getValue();
    }
    private Integer getMonth(){
        return (Integer) getMonthReference().getValue();
    }
    private Integer getDay(){
        return (Integer) getDayReference().getValue();
    }
    private Integer getHour(){
        return (Integer) getHourReference().getValue();
    }
    private Integer getMinute(){
        return (Integer) getMinuteReference().getValue();
    }
    private Integer getSecond(){
        return (Integer) getSecondReference().getValue();
    }

    private Long getAbsolutePeriodDifference(){
        return (Long) getAbsolutePeriodDifferenceReference().getValue();
    }

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

    public Period(int year, int month, int day, int hour, int minute, int second, long absolutePeriodDifference) {
        this.year = new ValueReference(year);
        this.month = new ValueReference(month);
        this.day = new ValueReference(day);
        this.hour = new ValueReference(hour);
        this.minute = new ValueReference(minute);
        this.second = new ValueReference(second);
        this.absolutePeriodDifference = new ValueReference(absolutePeriodDifference);
    }

    public Period(Date start, Date end) {
        this.year = new ValueReference(0);
        this.month = new ValueReference(0);
        this.day = new ValueReference(0);
        this.hour = new ValueReference(0);
        this.minute = new ValueReference(0);
        this.second = new ValueReference(0);
        this.absolutePeriodDifference = new ValueReference(end.secondsSinceNewEra() - start.secondsSinceNewEra());
    }

    public Period(Period period){
        this.year = new ValueReference(period.year.getValue());
        this.month = new ValueReference(period.month.getValue());
        this.day = new ValueReference(period.day.getValue());
        this.hour = new ValueReference(period.hour.getValue());
        this.minute = new ValueReference(period.minute.getValue());
        this.second = new ValueReference(period.second.getValue());
        this.absolutePeriodDifference = new ValueReference(period.absolutePeriodDifference.getValue());
    }

    @Override
    public String toString() {
        return getYear() + "." + getMonth() + "." + getDay() + " " + getHour() + ":" + getMinute() + ":"
                + getSecond() + "+" + getAbsolutePeriodDifference() + "sec";
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
