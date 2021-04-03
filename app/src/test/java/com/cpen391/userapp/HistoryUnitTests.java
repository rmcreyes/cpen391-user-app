package com.cpen391.userapp;

import com.cpen391.userapp.dashboardFragments.history.HistoryFragment;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, for the History section for processing information passed from backend
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HistoryUnitTests {

    /**
     *   Calculate a time duration that only has differences in hours (and is less than a day), and express in the format of HH:MM
     *   The difference in seconds are ROUNDED UP to minutes
     **/
    @Test
    public void duration_LessThanADay() {
        String st = "2021-03-27T08:25:25.165Z";
        String end = "2021-03-27T10:26:27.165Z";
        String duration = "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date startDate = format.parse(st);
            Date endDate = format.parse(end);
            duration = HistoryFragment.duration(startDate, endDate);
        } catch (ParseException e) {
        }
        /* Assert that the digits are extended with 0s*/
        assertEquals("02:02", duration);
    }

    /**
     *   Calculate a time duration that only has differences in minutes, and express in the format of HH:MM
     *   The difference in seconds are ROUNDED UP to minutes
     **/
    @Test
    public void duration_OnlyMinutes() {
        String st = "2021-03-27T08:25:25.165Z";
        String end = "2021-03-27T08:47:55.165Z";
        String duration = "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date startDate = format.parse(st);
            Date endDate = format.parse(end);
            duration = HistoryFragment.duration(startDate, endDate);
        } catch (ParseException e) {
        }
        /* Assert that the digits are extended with 0s*/
        assertEquals("00:23", duration);
    }


    /**
     *   Calculate a time duration that is one day (24 hours), and express in the format of HH:MM
     *   The difference in days should be translated into a time duration in Hour (ex 1 day = 24 hours).
     *   The difference in seconds are ROUNDED UP to minutes
     **/
    @Test
    public void duration_oneDay() {
        String st = "2021-03-27T08:25:25.165Z";
        String end = "2021-03-28T08:25:24.165Z";
        String duration = "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date startDate = format.parse(st);
            Date endDate = format.parse(end);
            duration = HistoryFragment.duration(startDate, endDate);
        } catch (ParseException e) {
        }
        assertEquals("24:00", duration);
    }

    /**
     *   Calculate a time duration that is greater than 24 hours, and express in the format of HH:MM
     *   The difference in days should be translated into a time duration in Hour (ex 1.5 day = 36 hours).
     *   The difference in seconds are ROUNDED UP to minutes
    **/
    @Test
    public void duration_MoreThanADay() {
        String st = "2021-03-27T08:25:25.165Z";
        String end = "2021-03-28T23:28:26.175Z";
        String duration = "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date startDate = format.parse(st);
            Date endDate = format.parse(end);
            duration = HistoryFragment.duration(startDate, endDate);
        } catch (ParseException e) {
        }
        assertEquals("39:04", duration);
    }

}