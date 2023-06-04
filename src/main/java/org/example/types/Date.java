package org.example.types;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Hashtable;

@Getter
@Setter
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
        if (date1.isAD() != date2.isAD()) return Boolean.compare(date1.isAD(), date2.isAD());
        int flip = date1.isAD() ? 1 : -1;
        if (date1.getYear() != date2.getYear()) return flip * Integer.compare(date1.getYear(), date2.getYear());
        if (date1.getMonth() != date2.getMonth()) return Integer.compare(date1.getMonth(), date2.getMonth());
        if (date1.getDay() != date2.getDay()) return Integer.compare(date1.getDay(), date2.getDay());
        if (date1.getHour() != date2.getHour()) return Integer.compare(date1.getHour(), date2.getHour());
        if (date1.getMinute() != date2.getMinute()) return Integer.compare(date1.getMinute(), date2.getMinute());
        if (date1.getSecond() != date2.getSecond()) return Integer.compare(date1.getSecond(), date2.getSecond());
        return 0;
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
        return (isAD ? "AD " : "BC ") +
                year + "." + (month < 10 ? 0 : "") +
                month + "." + (day < 10 ? 0 : "") +
                day + " " + (hour < 10 ? 0 : "") +
                hour + ":" + (minute < 10 ? 0 : "") +
                minute + ":" + (second < 10 ? 0 : "") + second;
    }

    public Date(Boolean isAD, int year, int month, int day, int hour, int minute, int second) {
        this.isAD = isAD;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        verify();
    }

    public void verify() {
        if (year < 1) {
            throw new IllegalArgumentException("Invalid years value");
        }
        if (month <= 0 || 12 < month) {
            throw new IllegalArgumentException("Invalid month value");
        }
        if (day <= 0 || (isLeapYear(year) && month == 2 && day > 29) || day > daysInMonth.get(month)) {
            throw new IllegalArgumentException("Invalid days value");
        }
        if (hour < 0 || 24 <= hour) {
            throw new IllegalArgumentException("Invalid hours value");
        }
        if (minute < 0 || 60 <= minute) {
            throw new IllegalArgumentException("Invalid minutes value");
        }
        if (second < 0 || 60 <= second) {
            throw new IllegalArgumentException("Invalid seconds value");
        }
    }

    public boolean equals(Date otherDate) {
        return isAD == otherDate.isAD()
                && year == otherDate.year
                && month == otherDate.month
                && day == otherDate.getDay()
                && hour == otherDate.getHour()
                && minute == otherDate.getMinute()
                && second == otherDate.getSecond();
    }

    public long secondsSinceNewEra() {
        long seconds = second;
        seconds += 60L * minute;
        seconds += 60L * 60L * hour;
        seconds += 60L * 60L * 24L * (day - 1);
        if (month >= 2)
            seconds += 60L * 60L * 24L * 31; //January
        if (month >= 3) {
            if (isLeapYear(year)) {
                seconds += 60L * 60L * 24L * 29; //Leap February
            } else {
                seconds += 60L * 60L * 24L * 28; //Regular February
            }
            for (int i = 3; i <= month - 1; i++) {
                seconds += 60L * 60L * 24L * daysInMonth.get(i);
            }
        }
        seconds += 60L * 60L * 24L * 366L * leapYearsUntil(year);
        long yearsInSeconds = 60L * 60L * 24L * 365L * (year - leapYearsUntil(year) - 1);
        if (!isAD) yearsInSeconds *= -1;
        seconds += yearsInSeconds;
        return seconds;
    }

    private void addToYear(int years){
        //TODO verify
        int signedYear = year * (isAD() ? 1 : -1);
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
        //TODO completely broken for negatives
        int years = (getMonth() + months) / 12;
        addToYear(years);
        setMonth((getMonth() + months) % 12);
    }

    private void addToDay(int days){
        //TODO completely broken for negatives
        while (getDay() + days >= daysInMonth.get(month)){
            days -= daysInMonth.get(getMonth()) - getDay();
            addToMonth(1);
            setDay(1);
        }
        setDay(getDay() + days);
    }

    private void addToHour(int hours){
        //TODO completely broken for negatives
        int days = (getHour() + hours) / 24;
        if (getHour() + hours < 0){
            days--;
        }
        addToDay(days);
        setHour((getHour() + hours) % 24);
    }

    private void addToMinute(int minutes){
        //TODO completely broken for negatives
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
        original.addToYear(period.getYear());
        original.addToMonth(period.getMonth());
        original.addToDay(period.getDay());
        original.addToHour(period.getHour());
        original.addToMinute(period.getMinute());
        original.addToSecond(period.getSecond());
        original.addToSecond(period.getAbsolutePeriodDifference());
        return original;
    }

    public Date subtract(Period period) {
        //TODO this is completely unhandled and untested
        Date original = new Date(isAD(), getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
        original.addToYear(-period.getYear());
        original.addToMonth(-period.getMonth());
        original.addToDay(-period.getDay());
        original.addToHour(-period.getHour());
        original.addToMinute(-period.getMinute());
        original.addToSecond(-period.getSecond());
        original.addToSecond(-period.getAbsolutePeriodDifference());
        return original;
    }

    public Period subtract(Date date) {
        return new Period(0, 0, 0, 0, 0, 0, secondsSinceNewEra() - date.secondsSinceNewEra() );
    }
}
