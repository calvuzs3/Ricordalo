package org.varonesoft.luke.ricodarlo.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by luca on 14/04/16.
 *
 */
public class DateTimeFormatter {

    // Default format String
    private static String DEF_FORMAT_DATE = "dd/MM/yyyy";
    private static String DEF_FORMAT_TIME = "HH:mm";
    private static String DEF_FORMAT_DATETIME = "dd/MM/yyyy HH:mm";

    /**
     * Return date in specified format.
     *
     * @param millis Date in milliseconds
     * @return String representing date in default format gg/MM/yyyy
     */
    public static final String getDate(final long millis) {

        return getDate(millis, DEF_FORMAT_DATE);
    }

    /**
     * Return date in specified format.
     *
     * @param millis Date in milliseconds
     * @param format Date format
     * @return String representing date in specified format
     */
    public static final String getDate(final long millis, final String format) {

        // Create a DateFormatter object for displaying date in specified format.
        final SimpleDateFormat formatter = new SimpleDateFormat(format);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    /**
     * Return time in specified format.
     *
     * @param millis Date in milliseconds
     * @return String representing date in default format HH:mm
     */
    public static final String getTime(final long millis) {

        return getTime(millis, DEF_FORMAT_TIME);
    }

    /**
     * Return time in specified format.
     *
     * @param millis Date in milliseconds
     * @param format Date format
     * @return String representing date in specified format
     */
    public static String getTime(final long millis, final String format) {

        // Create a DateFormatter object for displaying date in specified format.
        final SimpleDateFormat formatter = new SimpleDateFormat(format);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    /**
     * Return datetime in specified format.
     *
     * @param millis Date in milliseconds
     * @return String representing date in default format
     */
    public static final String getDateTime(final long millis) {

        return getDateTime(millis, DEF_FORMAT_DATETIME);
    }

    /**
     * Return datetime in specified format.
     *
     * @param millis Date in milliseconds
     * @param format Date format
     * @return String representing date in specified format
     */
    public static final String getDateTime(final long millis, final String format) {

        // Create a DateFormatter object for displaying date in specified format.
        final SimpleDateFormat formatter = new SimpleDateFormat(format);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }
}
