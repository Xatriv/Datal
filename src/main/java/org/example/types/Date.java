package org.example.types;

import org.example.interpreter.ValueReference;

import java.util.Arrays;
import java.util.Hashtable;

public class Date {
    private final ValueReference isAD;
    private final ValueReference year;
    private final ValueReference month;
    private final ValueReference day;
    private final ValueReference hour;
    private final ValueReference minute;
    private final ValueReference second;

    public ValueReference getIsADReference(){
        return this.isAD;
    }
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

    public void setAD(Boolean isAD){
        this.isAD.setValue(isAD);
    }
    public void setYear(Integer year){
        this.year.setValue(year);
        verify();
    }
    public void setMonth(Integer month){
        this.month.setValue(month);
        verify();
    }
    public void setDay(Integer day){
        this.day.setValue(day);
        verify();
    }
    public void setHour(Integer hour){
        this.hour.setValue(hour);
        verify();
    }
    public void setMinute(Integer minute){
        this.minute.setValue(minute);
        verify();
    }
    public void setSecond(Integer second){
        this.second.setValue(second);
        verify();
    }

    private Boolean isAD(){
        return (Boolean) getIsADReference().getValue();
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

    public static Hashtable<Integer, Integer> daysInMonth = new Hashtable<>() {{
        put(1, 31);
        put(2, 28); // leap years are handled separately
        put(3, 31);
        put(4, 30);
        put(5, 31);
        put(6, 30);
        put(7, 31);
        put(8, 31);
        put(9, 30);
        put(10, 31);
        put(11, 30);
        put(12, 31);
    }};

    public static boolean isLeapYear(int year) {
        if (year % 400 == 0) return true;
        if (year % 100 == 0) return false;
        return year % 4 == 0;
    }

    public static int leapYearsUntil(int year) {
        return year / 4 - year / 100 + year / 400;
    }

    public static int compare(Date date1, Date date2) {
        int result = Boolean.compare((Boolean) date1.getIsADReference().getValue(), (Boolean) date2.getIsADReference().getValue());
        if ( result != 0) return result;
        int flip = ((Boolean) date1.getIsADReference().getValue()) ? 1 : -1;
        result = flip * Integer.compare((Integer) date1.getYearReference().getValue(), (Integer) date2.getYearReference().getValue());
        if ( result != 0) return result;
        result = Integer.compare((Integer) date1.getMonthReference().getValue(), (Integer) date2.getMonthReference().getValue());
        if ( result != 0) return result;
        result = Integer.compare((Integer) date1.getDayReference().getValue(), (Integer) date2.getDayReference().getValue());
        if ( result != 0) return result;
        result = Integer.compare((Integer) date1.getHourReference().getValue(), (Integer) date2.getHourReference().getValue());
        if ( result != 0) return result;
        result = Integer.compare((Integer) date1.getMinuteReference().getValue(), (Integer) date2.getMinuteReference().getValue());
        if ( result != 0) return result;
        return Integer.compare((Integer) date1.getSecondReference().getValue(), (Integer) date2.getSecondReference().getValue());
    }

    public static Date fromString(String string){
        if (string.length() == 0){
            return null;
        }
        int index = 0;
        Boolean isAd = null;
        Integer year = null;
        Integer month = null;
        Integer day = null;
        Integer hour = null;
        Integer minute = null;
        Integer second = null;
        int sum;
        for (int unitSymbol: Arrays.asList('y', 'm', 'd', 'h', '\'', '\"')) {
            sum = 0;
            while (index < string.length() && isDigit(string.charAt(index))) {
                int digit = string.charAt(index) - '0';
                if ((Integer.MAX_VALUE - digit) / 10 < sum) {
                    return null;
                }
                sum = sum * 10 + digit;
                index++;
            }
            if (isAd == null){
                if (index > string.length() - 3){
                    return null;
                }
                if ("yYaAbB".indexOf(string.charAt(index)) == -1 ){
                    return null;
                }
                if (Character.toLowerCase(string.charAt(index)) == 'y'){
                    index++;
                    isAd = true;
                } else if (Character.toLowerCase(string.charAt(index)) == 'a' && Character.toLowerCase(string.charAt(index+1)) == 'd') {
                    index += 2;
                    isAd = true;
                } else if (Character.toLowerCase(string.charAt(index)) == 'b' && Character.toLowerCase(string.charAt(index+1)) == 'c') {
                    index += 2;
                    isAd = false;
                } else {
                    return null;
                }
                if (string.charAt(index) != ':'){
                    return null;
                }
                index++;
                year = sum;
            } else if (Character.toLowerCase(string.charAt(index)) != unitSymbol ) {
                return null;
            } else {
                switch (unitSymbol){
                    case 'm':
                        month = sum;
                        break;
                    case 'd':
                        day = sum;
                        break;
                    case 'h':
                        hour = sum;
                        break;
                    case '\'':
                        minute = sum;
                        break;
                    case '"':
                        second = sum;
                        break;
                }
                index++;
                if (index < string.length() && string.charAt(index) != ':'){
                    return null;
                }
                index++;
            }
        }
        // noinspection DataFlowIssue
        return new Date(isAd, year, month, day, hour, minute, second);
    }

    private static boolean isDigit(int character){
        return '9' >= character && character >= '0';
    }


    @Override
    public String toString() {
        return (isAD() ? "AD " : "BC ") + getYear() + "." +
                (getMonth() < 10 ? 0 : "") + getMonth() + "." +
                (getDay() < 10 ? 0 : "") + getDay() + " " +
                (getHour() < 10 ? 0 : "") + getHour() + ":" +
                (getMinute() < 10 ? 0 : "") + getMinute() + ":" +
                (getSecond() < 10 ? 0 : "") + getSecond();
    }

    public Date(Boolean isAD, int year, int month, int day, int hour, int minute, int second) {
        this.isAD = new ValueReference(isAD);
        this.year = new ValueReference(year);
        this.month = new ValueReference(month);
        this.day = new ValueReference(day);
        this.hour = new ValueReference(hour);
        this.minute = new ValueReference(minute);
        this.second = new ValueReference(second);
        verify();
    }

    public void verify() {
        if (getYear() < 1) {
            throw new IllegalArgumentException("Invalid years value");
        }
        if (getMonth() <= 0 || 12 < getMonth()) {
            throw new IllegalArgumentException("Invalid month value");
        }
        if (getDay() <= 0 || (isLeapYear(getYear()) && getMonth() == 2 && getDay() > 29) || getDay() > daysInMonth.get(getMonth())) {
            throw new IllegalArgumentException("Invalid days value");
        }
        if (getHour() < 0 || 24 <= getHour()) {
            throw new IllegalArgumentException("Invalid hours value");
        }
        if (getMinute() < 0 || 60 <= getMinute()) {
            throw new IllegalArgumentException("Invalid minutes value");
        }
        if (getSecond() < 0 || 60 <= getSecond()) {
            throw new IllegalArgumentException("Invalid seconds value");
        }
    }

    public boolean equals(Date otherDate) {
        return compare(this, otherDate) == 0;
    }

    public long secondsSinceNewEra() {
        long seconds = getSecond();
        seconds += 60L * getMinute();
        seconds += 60L * 60L * getHour();
        seconds += 60L * 60L * 24L * (getDay() - 1);
        if (getMonth() >= 2)
            seconds += 60L * 60L * 24L * 31; //January
        if (getMonth() >= 3) {
            if (isLeapYear(getYear())) {
                seconds += 60L * 60L * 24L * 29; //Leap February
            } else {
                seconds += 60L * 60L * 24L * 28; //Regular February
            }
            for (int i = 3; i <= getMonth() - 1; i++) {
                seconds += 60L * 60L * 24L * daysInMonth.get(i);
            }
        }
        seconds += 60L * 60L * 24L * 366L * leapYearsUntil(getYear());
        long yearsInSeconds = 60L * 60L * 24L * 365L * (getYear() - leapYearsUntil(getYear()) - 1);
        if (!isAD()) yearsInSeconds *= -1;
        seconds += yearsInSeconds;
        return seconds;
    }

    private void addToYear(int years){
        int signedYear = getYear() * (isAD() ? 1 : -1);
        if (years > 0  && signedYear > 0){
            setYear(signedYear + years);
        } else if (years < 0 && signedYear > 0){
            if (signedYear + years >= 1){
                setYear(signedYear+years);
            } else {
                setAD(false);
                setYear((1 - (signedYear - years))); // signedYear = 4, years = -7, 1 - (4-7) =
            }
        } else if (years > 0 && signedYear < 0) {
            if (signedYear + years <= -1){
                setYear(signedYear+years);
            } else {
                setAD(true);
                setYear((1 - (signedYear - years)));
            }
        } else if (years < 0 && signedYear < 0) {
            setYear(signedYear - years);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void addToMonth(int months){
        int years = (getMonth() + months) / 12;
        addToYear(years);
        setMonth((getMonth() + months) % 12);
    }

    private void addToDay(int days){
        while (getDay() + days >= daysInMonth.get(getMonth())){
            days -= daysInMonth.get(getMonth()) - getDay();
            addToMonth(1);
            setDay(1);
        }
        setDay(getDay() + days);
    }

    private void addToHour(int hours){
        int days = (getHour() + hours) / 24;
        if (getHour() + hours < 0){
            days--;
        }
        addToDay(days);
        setHour((getHour() + hours) % 24);
    }

    private void addToMinute(int minutes){
        int hours = (getMinute() + minutes) / 60;
        if (getMinute() + minutes < 0){
            hours--;
        }
        addToHour(hours);
        setMinute((getMinute() + minutes) % 60);
    }

    private void addToSecond(int seconds){
        int minutes = (getSecond() + seconds) / 60;
        if (getSecond() + seconds < 0){
            minutes--;
        }
        addToMinute(minutes);
        setSecond((getSecond() + seconds) % 60);
    }

    private void addToSecond(long seconds){
        while (seconds + getSecond() >= 60){
            seconds -= 60 - getSecond();
            addToMinute(1);
            setSecond(0);
        }
        setSecond(getSecond() + Math.toIntExact(seconds));
    }


    public Date add(Period period) {
        Date original = new Date(isAD(), getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
        original.addToYear((Integer) period.getYearReference().getValue());
        original.addToMonth((Integer) period.getMonthReference().getValue());
        original.addToDay((Integer) period.getDayReference().getValue());
        original.addToHour((Integer) period.getHourReference().getValue());
        original.addToMinute((Integer) period.getMinuteReference().getValue());
        original.addToSecond((Integer) period.getSecondReference().getValue());
        original.addToSecond((Long) period.getAbsolutePeriodDifferenceReference().getValue());
        return original;
    }

    public Date subtract(Period period) {
        Date original = new Date(isAD(), getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
        original.addToYear(- (Integer) period.getYearReference().getValue());
        original.addToMonth(- (Integer) period.getMonthReference().getValue());
        original.addToDay(- (Integer) period.getDayReference().getValue());
        original.addToHour(- (Integer) period.getHourReference().getValue());
        original.addToMinute(- (Integer) period.getMinuteReference().getValue());
        original.addToSecond(- (Integer) period.getSecondReference().getValue());
        original.addToSecond(- (Long) period.getAbsolutePeriodDifferenceReference().getValue());
        return original;
    }

    public Period subtract(Date date) {
        return new Period(0, 0, 0, 0, 0, 0, secondsSinceNewEra() - date.secondsSinceNewEra() );
    }
}
