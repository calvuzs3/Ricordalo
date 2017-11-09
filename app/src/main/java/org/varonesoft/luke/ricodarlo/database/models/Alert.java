package org.varonesoft.luke.ricodarlo.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import org.varonesoft.luke.ricodarlo.database.RicordaloProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by luke on 28/10/17.
 * <p>
 * Alert
 */

public class Alert extends DAO {

    public static final String STRINGKEY_ALERT_ID = "stringkey_alert_id";
    // PATH
    public static final String PATH = "alerts";
    // DATABASE TABLE
    public static final String TABLE_NAME = RicordaloProvider.BASE_TABLE_NAME + PATH;
    // CONTENT TYPE
    public static final String CONTENT_TYPE = RicordaloProvider.BASE_CONTENT_TYPE + PATH;
    // BASE CODES
    public static final int BASEURICODE = 414001;
    public static final int BASEITEMCODE = 414011;
    // Uri
    public static final Uri URI = Uri.withAppendedPath(Uri.parse(RicordaloProvider.SCHEME + RicordaloProvider.AUTHORITY), PATH);
    // Database creation SQL statement
    public static final String DATABASE_CREATE = new StringBuilder("CREATE TABLE ")
            .append(TABLE_NAME).append("(")
            .append(Columns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(Columns.NAME).append(" TEXT NOT NULL, ")
            .append(Columns.DESC).append(" TEXT, ")
            .append(Columns.START_DATE).append(" INTEGER NOT NULL, ")
            .append(Columns.REPEAT_DAYS).append(" INTEGER NOT NULL, ")
            .append(Columns.REPEAT_HOURS).append(" INTEGER NOT NULL, ")
            .append(Columns.STATUS).append(" INTEGER NOT NULL, ")
            .append(Columns.TONE).append(" TEXT, ")
            .append(Columns.VIBRATE).append(" INTEGER NOT NULL, ")
            .append(Columns.ALARM_MODE).append(" INTEGER NOT NULL, ")
            .append(Columns.COUNTER).append(" INTEGER NOT NULL, ")
            .append(Columns.LAST_FIRED).append(" INTEGER NOT NULL); ")

            .toString();
    public static final int STATUS_OFF = 0x0000;
    public static final int STATUS_ACTIVATED = 0x0001;
    public static final int STATUS_SNOOZING = 0x0010;
    // TAG & STRINGS
    private static final String TAG = "DB Alert";
    // fields
    private String name;
    private String desc;
    private Long startDate;
    private Long rptDays;
    private Long rptHours;
    private int status;
    private String tone;
    private Long vibrate;
    private Long alarmMode;
    private int counter;
    private Long lastFired;

    // CONTRUCTORS
    public Alert() {
        // All empty
        this.name = "";
        this.desc = "";
        this.startDate = 0L;
        this.rptDays = 0L;
        this.rptHours = 0L;
        this.status = STATUS_OFF;
        this.tone = "";
        this.vibrate = 0L;
        this.alarmMode = 0L;
        this.counter = 0;
        this.lastFired = 0L;
    }

    public Alert(final Cursor c) {
        this.set_id(c.getLong(0));
        this.name = c.getString(1);
        this.desc = c.getString(2);
        this.startDate = c.getLong(3);
        this.rptDays = c.getLong(4);
        this.rptHours = c.getLong(5);
        this.status = c.getInt(6);
        this.tone = c.getString(7);
        this.vibrate = c.getLong(8);
        this.alarmMode = c.getLong(9);
        this.counter = c.getInt(10);
        this.lastFired = c.getLong(11);
    }

    public Alert(final Uri uri, final ContentValues values) {
        this(Long.parseLong(uri.getLastPathSegment()), values);
    }

    public Alert(final long id, final ContentValues values) {
        this(values);
        this.set_id(id);
    }

    public Alert(final ContentValues values) {
        name = values.getAsString(Columns.NAME);
        desc = values.getAsString(Columns.DESC);
        startDate = values.getAsLong(Columns.START_DATE);
        rptDays = values.getAsLong(Columns.REPEAT_DAYS);
        rptHours = values.getAsLong(Columns.REPEAT_HOURS);
        status = values.getAsInteger(Columns.STATUS);
        tone = values.getAsString(Columns.TONE);
        vibrate = values.getAsLong(Columns.VIBRATE);
        alarmMode = values.getAsLong(Columns.ALARM_MODE);
        counter = values.getAsInteger(Columns.COUNTER);
        lastFired = values.getAsLong(Columns.LAST_FIRED);
    }

    // Add to the matcherUris thi codes
    public static void addMatcherUris(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(RicordaloProvider.AUTHORITY, PATH, BASEURICODE);
        sURIMatcher.addURI(RicordaloProvider.AUTHORITY, PATH + "/#", BASEITEMCODE);
    }

    /**
     * Init
     * put some default values into it
     * Dont init() it
     *
     * @param database Db
     */
    public static String init(SQLiteDatabase database) {

        final StringBuilder sb = new StringBuilder("Alert.init()\n");
        final ContentValues cv = new ContentValues();

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        final String[] names = new String[]{"Cibo Pesci", "Cambio Acqua", "Innaffiare"};
        final String[] descs = new String[]{"Dare da mangiare ai pesci",
                "Cambiare acqua all acquario", "Innaffiare le piante"};
        final long[] rdays = new long[]{1L, 7L, 3L};

        for (int i = 0; i < 3; i++) {
            cv.clear();
            cv.put(Columns.NAME, names[i]);
            cv.put(Columns.DESC, descs[i]);
            cv.put(Columns.START_DATE, calendar.getTimeInMillis());
            cv.put(Columns.REPEAT_DAYS, rdays[i]);
            cv.put(Columns.REPEAT_HOURS, 0L);
            cv.put(Columns.TONE, "default");
            cv.put(Columns.VIBRATE, 0L);
            cv.put(Columns.STATUS, STATUS_OFF);
            cv.put(Columns.ALARM_MODE, 0L);      // Notification
            cv.put(Columns.COUNTER, 0);      // Notification Counter
            cv.put(Columns.LAST_FIRED, 0L);

            database.insert(TABLE_NAME, null, cv);
            sb.append(String.format("[name=%s]\n", names[i]));
        }
        return sb.toString();
    }

    /**
     * @return ContentValues
     */
    @Override
    public ContentValues getContent() {
        final ContentValues cv = new ContentValues();
        // Note that ID is NOT included here
        cv.put(Columns.NAME, name);
        cv.put(Columns.DESC, desc);
        cv.put(Columns.START_DATE, startDate);
        cv.put(Columns.REPEAT_DAYS, rptDays);
        cv.put(Columns.REPEAT_HOURS, rptHours);
        cv.put(Columns.STATUS, status);
        cv.put(Columns.TONE, tone);
        cv.put(Columns.VIBRATE, vibrate);
        cv.put(Columns.ALARM_MODE, alarmMode);
        cv.put(Columns.COUNTER, counter);
        cv.put(Columns.LAST_FIRED, lastFired);
        return cv;
    }

    /**
     * @return Table name
     */
    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    /**
     * @return Path
     */
    @Override
    protected String getPath() {
        return PATH;
    }

    /**
     * @return Content type
     */
    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    /**
     * Convenience method for normal operations.
     *
     * @param context Context
     * @return int Number of db-rows affected. Fail if < 1,
     */
    @Override
    public int save(Context context) {
        int result = 0;
        if (get_id() < 1) {
            final Uri uri = context.getContentResolver().insert(getBaseUri(), getContent());
            if (uri != null) {
                this.set_id(Long.parseLong(uri.getLastPathSegment()));
                result++;
            }
        } else {
            result += context.getContentResolver().update(getUri(),
                    getContent(), null, null);
        }
        return result;
    }

    public String getName() {
        return name;
    }

    /* Getters and Setters */

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getRptDays() {
        return rptDays;
    }

    public void setRptDays(Long rptDays) {
        this.rptDays = rptDays;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isVibrate() {
        return (this.vibrate == 1);
    }

    public boolean isActivated() {
        return (this.status == STATUS_ACTIVATED);
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public Long getVibrate() {
        return vibrate;
    }

    public void setVibrate(Long vibrate) {
        this.vibrate = vibrate;
    }

    public Long getAlarmMode() {
        return alarmMode;
    }

    public void setAlarmMode(Long alarmMode) {
        this.alarmMode = alarmMode;
    }

    public Long getLastFired() {
        return lastFired;
    }

    public void setLastFired(Long lastFired) {
        this.lastFired = lastFired;
    }

    public Long getRptHours() {
        return rptHours;
    }

    public void setRptHours(Long rptHours) {
        this.rptHours = rptHours;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    /* Inner Class Columns */
    public static class Columns implements BaseColumns {

        // Base of field's name
        private static final String _BASE = "al_";
        // Table FIELDS
        static final String NAME = _BASE + "name";
        static final String DESC = _BASE + "desc";
        static final String START_DATE = _BASE + "startDate";
        static final String REPEAT_DAYS = _BASE + "rpt_days";
        static final String REPEAT_HOURS = _BASE + "rpt_hours";
        public static final String STATUS = _BASE + "status";
        static final String TONE = _BASE + "tone";
        static final String VIBRATE = _BASE + "vibrate";
        static final String ALARM_MODE = _BASE + "mode";
        static final String COUNTER = _BASE + "counter";
        static final String LAST_FIRED = _BASE + "last";
        // Table Fields as Array
        public static final String[] FIELDS = {_ID, NAME, DESC, START_DATE, REPEAT_DAYS, REPEAT_HOURS,
                STATUS, TONE, VIBRATE, ALARM_MODE, COUNTER, LAST_FIRED};

        // This Class cannot be instantiated
        private Columns() {
        }
    }
}
