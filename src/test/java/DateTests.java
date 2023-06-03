import org.example.types.Date;
import org.example.types.Period;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class DateTests {
    @Test
    void simpleDateTest() {
        var date = new Date(true, 2023, 5, 30, 23, 53, 10);
        assertTrue(date.isAD());
        assertEquals(2023, date.getYear());
        assertEquals(5, date.getMonth());
        assertEquals(30, date.getDay());
        assertEquals(23, date.getHour());
        assertEquals(53, date.getMinute());
        assertEquals(10, date.getSecond());
    }

    @Test
    void daysInMonthTest() {
        assertEquals(31, Date.daysInMonth.get(1));
        assertEquals(28, Date.daysInMonth.get(2));
        assertEquals(31, Date.daysInMonth.get(3));
        assertEquals(30, Date.daysInMonth.get(4));
        assertEquals(31, Date.daysInMonth.get(5));
        assertEquals(30, Date.daysInMonth.get(6));
        assertEquals(31, Date.daysInMonth.get(7));
        assertEquals(31, Date.daysInMonth.get(8));
        assertEquals(30, Date.daysInMonth.get(9));
        assertEquals(31, Date.daysInMonth.get(10));
        assertEquals(30, Date.daysInMonth.get(11));
        assertEquals(31, Date.daysInMonth.get(12));
    }

    @Test
    void negativeYearTest() {
        assertThrows(IllegalArgumentException.class, () -> new Date(true, -2023, 5, 30, 23, 53, 10));
    }

    @Test
    void negativeMonthTest() {
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, -5, 30, 23, 53, 10));
    }

    @Test
    void monthTooBigTest() {
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 13, 30, 23, 53, 10));
    }

    @Test
    void negativeDayTest() {
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 5, -30, 23, 53, 10));
    }

    @Test
    void dayTooBig28Test() {
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 2, 29, 23, 53, 10));
    }

    @Test
    void dayTooBig29Test() {
        //2024 - leap year
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2024, 2, 30, 23, 53, 10));
    }

    @Test
    void dayTooBig30Test() {
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 4, 31, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 6, 31, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 9, 31, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 11, 31, 23, 53, 10));
    }

    @Test
    void dayTooBig31Test() {
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 1, 32, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 3, 32, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 5, 32, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 7, 32, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 8, 32, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 10, 32, 23, 53, 10));
        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 12, 32, 23, 53, 10));
    }

    @Test
    void dateEqualTest() {
        var date1 = new Date(true, 2023, 5, 30, 23, 53, 10);
        var date2 = new Date(true, 2023, 5, 30, 23, 53, 10);
        assertTrue(date1.equals(date2));
//        assertThrows(IllegalArgumentException.class, () -> new Date(true, 2023, 5, 30, 23, 53, 10));
    }

    @Test
    void dateNotEqualMonthTest() {
        var date1 = new Date(true, 2023, 5, 30, 23, 53, 10);
        var date2 = new Date(true, 2023, 10, 30, 23, 53, 10);
        assertFalse(date1.equals(date2));
    }

    @Test
    void dateNotEqualDayTest() {
        var date1 = new Date(true, 2023, 5, 30, 23, 53, 10);
        var date2 = new Date(true, 2023, 5, 1, 23, 53, 10);
        assertFalse(date1.equals(date2));
    }

    @Test
    void dateNotEqualHourTest() {
        var date1 = new Date(true, 2023, 5, 30, 23, 53, 10);
        var date2 = new Date(true, 2023, 5, 30, 5, 53, 10);
        assertFalse(date1.equals(date2));
    }

    @Test
    void dateNotEqualMinuteTest() {
        var date1 = new Date(true, 2023, 5, 30, 23, 53, 10);
        var date2 = new Date(true, 2023, 5, 30, 23, 1, 10);
        assertFalse(date1.equals(date2));
    }

    @Test
    void dateNotEqualSecondTest() {
        var date1 = new Date(true, 2023, 5, 30, 23, 53, 10);
        var date2 = new Date(true, 2023, 5, 30, 23, 53, 1);
        assertFalse(date1.equals(date2));
    }

    @Test
    void timeSinceNewEraRegularTest() {
        //sec+min+h+d=2 591 590
        //month=10 368 000
        //year=63 808 128 000
        var date1 = new Date(true, 2023, 5, 30, 23, 53, 10);
        assertEquals(63821087590L, date1.secondsSinceNewEra());
    }

    @Test
    void timeSinceNewEraZeroTest() {
        //sec+min+h+d=2 591 590
        //month=10 368 000
        //year=63 808 128 000
        var date1 = new Date(true, 1, 1, 1, 0, 0, 0);
        assertEquals(0, date1.secondsSinceNewEra());
    }

    @Test
    void datesCompareEqualTest(){
        var date1 = new Date(true, 2023, 6, 1, 23, 5, 6);
        var date2 = new Date(true, 2023, 6, 1, 23, 5, 6);
        assertEquals(0, Date.compare(date1, date2));
    }

    @Test
    void datesCompareDifferentErasTest(){
        var date1 = new Date(false, 2023, 6, 1, 23, 5, 6);
        var date2 = new Date(true, 2023, 6, 1, 23, 5, 6);
        assertEquals(-1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareYearADTest(){
        var date1 = new Date(true, 2077, 6, 1, 23, 5, 6);
        var date2 = new Date(true, 2023, 6, 1, 23, 5, 6);
        assertEquals(1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareYearBCTest(){
        var date1 = new Date(false, 2077, 6, 1, 23, 5, 6);
        var date2 = new Date(false, 2023, 6, 1, 23, 5, 6);
        assertEquals(-1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareMonthADTest(){
        var date1 = new Date(true, 2023, 10, 1, 23, 5, 6);
        var date2 = new Date(true, 2023, 6, 1, 23, 5, 6);
        assertEquals(1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareMonthBCTest(){
        var date1 = new Date(false, 2023, 10, 1, 23, 5, 6);
        var date2 = new Date(false, 2023, 6, 1, 23, 5, 6);
        assertEquals(-1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareDayADTest(){
        var date1 = new Date(true, 2023, 6, 10, 23, 5, 6);
        var date2 = new Date(true, 2023, 6, 1, 23, 5, 6);
        assertEquals(1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareDayBCTest(){
        var date1 = new Date(false, 2023, 6, 10, 23, 5, 6);
        var date2 = new Date(false, 2023, 6, 1, 23, 5, 6);
        assertEquals(-1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareHourADTest(){
        var date1 = new Date(true, 2023, 6, 1, 23, 5, 6);
        var date2 = new Date(true, 2023, 6, 1, 22, 5, 6);
        assertEquals(1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareHourBCTest(){
        var date1 = new Date(false, 2023, 6, 1, 23, 5, 6);
        var date2 = new Date(false, 2023, 6, 1, 22, 5, 6);
        assertEquals(-1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareMinuteADTest(){
        var date1 = new Date(true, 2023, 6, 1, 23, 10, 6);
        var date2 = new Date(true, 2023, 6, 1, 23, 5, 6);
        assertEquals(1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareMinuteBCTest(){
        var date1 = new Date(false, 2023, 6, 1, 23, 10, 6);
        var date2 = new Date(false, 2023, 6, 1, 23, 5, 6);
        assertEquals(-1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareSecondADTest(){
        var date1 = new Date(true, 2023, 6, 1, 23, 5, 10);
        var date2 = new Date(true, 2023, 6, 1, 23, 5, 6);
        assertEquals(1, Date.compare(date1, date2));
    }

    @Test
    void datesCompareSecondBCTest(){
        var date1 = new Date(false, 2023, 6, 1, 23, 5, 10);
        var date2 = new Date(false, 2023, 6, 1, 23, 5, 6);
        assertEquals(-1, Date.compare(date1, date2));
    }

    @Test
    void dateFromStringTest(){
        Date date1 = Date.fromString("2023AD:6M:1D:23H:5':6\"");
        assertNotNull(date1);
        assertTrue(date1.isAD());
        assertEquals(2023, date1.getYear());
        assertEquals(6, date1.getMonth());
        assertEquals(1, date1.getDay());
        assertEquals(23, date1.getHour());
        assertEquals(5, date1.getMinute());
        assertEquals(6, date1.getSecond());
    }

    @Test
    void dateFromStringInvalidYearTest() {
        Date date1 = Date.fromString("2023XD:6M:1D:23H:5':6\"");
        assertNull(date1);
    }

    @Test
    void dateFromStringInvalidMonthTest() {
        Date date1 = Date.fromString("2023AD:6A:1D:23H:5':6\"");
        assertNull(date1);
    }

    @Test
    void dateFromStringInvalidDayTest() {
        Date date1 = Date.fromString("2023AD:6M:1A:23H:5':6\"");
        assertNull(date1);
    }

    @Test
    void dateFromStringInvalidHourTest() {
        Date date1 = Date.fromString("2023AD:6M:1D:23A:5':6\"");
        assertNull(date1);
    }

    @Test
    void dateFromStringInvalidMinuteTest() {
        Date date1 = Date.fromString("2023AD:6M:1D:23H:5\":6\"");
        assertNull(date1);
    }

    @Test
    void dateFromStringInvalidSecondTest() {
        Date date1 = Date.fromString("2023AD:6M:1D:23H:5':6'");
        assertNull(date1);
    }

    @Test
    void dateFromStringMissingSeparatorTest() {
        Date date1 = Date.fromString("2023AD:6M:1D:23H:5':6'");
        Date date2 = Date.fromString("2023AD6M:1D:23H:5':6'");
        Date date3 = Date.fromString("2023AD:6M1D:23H:5':6'");
        Date date4 = Date.fromString("2023AD:6M:1D23H:5':6'");
        Date date5 = Date.fromString("2023AD:6M:1D:23H5':6'");
        Date date6 = Date.fromString("2023AD:6M:1D:23H:5'6'");
        assertNull(date1);
        assertNull(date2);
        assertNull(date3);
        assertNull(date4);
        assertNull(date5);
        assertNull(date6);
    }

    @Test
    void periodFromStringTest(){
        Period period1 = Period.fromString("   2023y  6M 1D 23H 5' 6\" ");
        assertNotNull(period1);
        assertEquals(2023, period1.getYear());
        assertEquals(6, period1.getMonth());
        assertEquals(1, period1.getDay());
        assertEquals(23, period1.getHour());
        assertEquals(5, period1.getMinute());
        assertEquals(6, period1.getSecond());
    }

    @Test
    void periodFromStringInvalidUnitTest() {
        Date date1 = Date.fromString("2023XD ");
        assertNull(date1);
    }

    @Test
    void addPeriods() {
        Period period1 = new Period(1, 2, 3, 4, 5, 6, 0L);
        Period period2 = new Period(2, 3, 4, 5, 6, 7, 0L);
        Period result = period2.add(period1);
        assertEquals(3, result.getYear());
        assertEquals(5, result.getMonth());
        assertEquals(7, result.getDay());
        assertEquals(9, result.getHour());
        assertEquals(11, result.getMinute());
        assertEquals(13, result.getSecond());
    }

    @Test
    void subtractPeriods() {
        Period period1 = new Period(1, 2, 3, 4, 5, 6, 0L);
        Period period2 = new Period(2, 3, 4, 5, 6, 7, 0L);
        Period result = period2.subtract(period1);
        assertEquals(1, result.getYear());
        assertEquals(1, result.getMonth());
        assertEquals(1, result.getDay());
        assertEquals(1, result.getHour());
        assertEquals(1, result.getMinute());
        assertEquals(1, result.getSecond());
    }
}


