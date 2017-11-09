package org.varonesoft.luke.ricodarlo.components;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.varonesoft.luke.ricodarlo.R;
import org.varonesoft.luke.ricodarlo.TaskerActivity;
import org.varonesoft.luke.ricodarlo.Util.DateTimeFormatter;
import org.varonesoft.luke.ricodarlo.Util.Log;
import org.varonesoft.luke.ricodarlo.database.models.Alert;
import org.varonesoft.luke.ricodarlo.database.models.DAOHelper;

import java.util.Calendar;

import static org.varonesoft.luke.ricodarlo.Constants.Service.ACTION_CANCEL_ALARM;
import static org.varonesoft.luke.ricodarlo.Constants.Service.ACTION_SET_ALARM;
import static org.varonesoft.luke.ricodarlo.Constants.Service.ACTION_SET_ALL_ALARMS;
import static org.varonesoft.luke.ricodarlo.Constants.Service.ACTION_SET_SNOOZE;
import static org.varonesoft.luke.ricodarlo.Constants.Service.ACTION_START_NOTIFICATION;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_ALERTID;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_COUNTER;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_PARAM1;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_PARAM2;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * ACTION_SET_ALARM
 * ACTION_START_NOTIFICATION
 */
public class RicordaloIntentService extends WakeLockIntentService {

    // Tag
    private static final String TAG = RicordaloIntentService.class.getSimpleName();

    // NotificationID should be replaced by mAlert.getId()
    private static final int NOTIFICATION_ID = 0x11000000;
    private static final int SNOOZE_MINUTES = 1;
    // Should be Toasted??
    private static boolean mShowAlertToast;
    // Notification
    private NotificationManager mNotificationManager;
    //Alarm
    private AlarmManager mAlarmManager;


    public RicordaloIntentService() {
        super(TAG);
    }

    /**
     * Helper Method
     * <p>
     * Starts this service to perform action ACTION_SET_ALARM.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    synchronized
    public static void startActionSetAlarm(final Context context, final Alert alert) {

        // Set Toast
        mShowAlertToast = true;

        //Set alarm
        final Intent intent = new Intent(context, RicordaloIntentService.class);
        intent.setAction(ACTION_SET_ALARM);
        intent.putExtra(EXTRA_ALERTID, alert.get_id());
        intent.putExtra(EXTRA_COUNTER, alert.getCounter());
        intent.putExtra(EXTRA_PARAM1, alert.getName());
        intent.putExtra(EXTRA_PARAM2, alert.getDesc());

        context.startService(intent);
    }

    /**
     * Helper Method
     * <p>
     * Starts this service to perform action ACTION_CANCEL_ALARM.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    synchronized
    public static void startActionCancelAlarm(final Context context, final Alert alert) {

        //Set alarm
        final Intent intent = new Intent(context, RicordaloIntentService.class);
        intent.setAction(ACTION_CANCEL_ALARM);
        intent.putExtra(EXTRA_ALERTID, alert.get_id());

        context.startService(intent);
    }

    /**
     * Helper Method
     * <p>
     * Starts this service to perform action ACTION_SET_SNOOZE.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    synchronized
    public static void startActionSetSnooze(Context context, Alert alert) {

        //Set alarm
        final Intent intent = new Intent(context, RicordaloIntentService.class);
        intent.setAction(ACTION_SET_SNOOZE);
        intent.putExtra(EXTRA_ALERTID, alert.get_id());
        intent.putExtra(EXTRA_COUNTER, alert.getCounter());
        intent.putExtra(EXTRA_PARAM1, alert.getName());
        intent.putExtra(EXTRA_PARAM2, alert.getDesc());

        context.startService(intent);
    }

    private static Calendar calculateNextAlarm(final Alert alert, @Nullable StringBuilder msg) {

        // Compare the date of the alert with now()
        // till it's prior, add repeat amounts
        final Calendar now, calendar;
        now = Calendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alert.getStartDate());

        while (now.compareTo(calendar) == 1) {

            //Finche la data allarme sara' inferiore alla data corrente..
            if (alert.getRptHours() != 0) {
                calendar.add(Calendar.HOUR_OF_DAY, alert.getRptHours().intValue());
            }
            if (alert.getRptDays() != 0) {
                calendar.add(Calendar.DAY_OF_YEAR, alert.getRptDays().intValue());
            }
        }

        if (msg != null) {
            msg.append(DateTimeFormatter.getDateTime(calendar.getTimeInMillis()));
        }

        Log.d(TAG, String.format("calculateNextAlarm() [date=%s]", DateTimeFormatter.getDateTime(calendar.getTimeInMillis())));
        return calendar;
    }

    private static Calendar calculateNextSnooze(final Alert alert, @Nullable StringBuilder msg) {

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, SNOOZE_MINUTES);

        if (msg != null) {
            msg.append(DateTimeFormatter.getDateTime(calendar.getTimeInMillis()));
        }

        Log.d(TAG, String.format("calculateNextSnooze() [date=%s]", DateTimeFormatter.getDateTime(calendar.getTimeInMillis())));
        return calendar;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mShowAlertToast = false;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mNotificationManager = null;
        mAlarmManager = null;
    }

    /**
     * The core
     *
     * @param intent Intent passed calling the service
     */
    @Override
    void handleIntent(Intent intent) {

        if (intent == null) {
            Log.e(TAG, "handleIntent() null intent");
            return;
        }

        final String action = intent.getAction();
        final Long id = intent.getLongExtra(EXTRA_ALERTID, -1L);
        final int counter = intent.getIntExtra(EXTRA_COUNTER, 0);
        final String param1 = intent.getStringExtra(EXTRA_PARAM1);
        final String param2 = intent.getStringExtra(EXTRA_PARAM2);

        // Log
        StringBuilder msg = new StringBuilder(String.format("Performing [action=%s] \n", action));
        msg.append(String.format("[id=%d]", id));

        if (ACTION_START_NOTIFICATION.equals(action)) {

            //
            msg.append(String.format("[param1=%s, param2=%s, counter=%d]", param1, param2, counter));
            Log.d(TAG, msg.toString());

            handleActionStartNotification(id, param1, param2, counter);


        } else if (ACTION_CANCEL_ALARM.equals(action)) {

            //
            Log.d(TAG, msg.toString());

            handleActionCancelAlarm(id);


        } else if (ACTION_SET_ALARM.equals(action)) {

            //
            Log.d(TAG, msg.toString());

            final StringBuilder toast = new StringBuilder("");
            handleActionSetAlarm(id, toast);
            if (mShowAlertToast)
                Toast.makeText(getApplicationContext(), toast.toString(), Toast.LENGTH_LONG).show();


        } else if (ACTION_SET_SNOOZE.equals(action)) {

            //
            Log.d(TAG, msg.toString());

            StringBuilder toast = new StringBuilder("");
            handleActionSetSnooze(id, toast);
            if (mShowAlertToast)
                Toast.makeText(getApplicationContext(), toast.toString(), Toast.LENGTH_LONG).show();


        } else if (ACTION_SET_ALL_ALARMS.equals(action)) {

            // Sets all alarms
            handleActionSetAllAlarms();
        }
    }

    /**
     * Handle action
     * <p>
     * Handle action ACTION_START_NOTIFICATION
     */
    private void handleActionStartNotification(@NonNull Long id, @NonNull String title,
                                               @NonNull String text, @NonNull int counter) {

        // Required to start a notification
        Intent intent = new Intent(this, TaskerActivity.class);
        intent.putExtra(EXTRA_ALERTID, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setTicker(title)
                        .setContentIntent(pi)
                        .setAutoCancel(false)
                        .setWhen(System.currentTimeMillis())
                        .setLights(0xff00ff00, 2000, 2000)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setNumber(counter);

        final int notificationId = NOTIFICATION_ID | id.intValue();

        // Log
        Log.d(TAG, String.format("notification [id=%d title=%s text=%s]", id, title, text));

        // Finally notify
        mNotificationManager.notify(notificationId, mBuilder.build());

        // If there is no user input ... we have to notify again, up to 3 times

        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(Alert.URI, String.valueOf(id)), null, null, null, null);
        if (cursor == null) return;
        if (!(cursor.moveToNext())) return;

        Alert alert = new Alert(cursor);
        if (alert.getCounter() < 3 ) {
            alert.setCounter(alert.getCounter() + 1);
            alert.setStatus(alert.getStatus() | Alert.STATUS_SNOOZING);
            alert.save(this);

            handleActionSetSnooze(alert, null);
        } else {
            alert.setCounter(0);
            alert.setStatus(( Alert.STATUS_ACTIVATED));
            alert.save(this);

            handleActionSetAlarm(alert, null);
        }
    }

    /**
     * Handle action
     * <p>
     * Handle action ACTION_CANCEL_ALARM
     */
    private void handleActionCancelAlarm(final Long id) {

        Log.d(TAG, "CancelAlarm");

//        // Get alert
//        Cursor cursor = getContentResolver().query(
//                Uri.withAppendedPath(Alert.URI, String.valueOf(id)), null, null, null, null);
//        if (cursor == null) return;
//
//        Alert alert;
//        if (cursor.moveToNext()) {
//            alert = new Alert(cursor);
//            cursor.close();
//        } else {
//            cursor.close();
//            return;
//        }

        Intent intent = new Intent(this, RicordaloReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION);
        intent.putExtra(EXTRA_ALERTID, id);
//        intent.putExtra(EXTRA_PARAM1, alert.getName());
//        intent.putExtra(EXTRA_PARAM2, alert.getDesc());

        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id.intValue(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            mAlarmManager.cancel(pi);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Handle action
     */
    private void handleActionSetSnooze(@NonNull Long id, @Nullable final StringBuilder msg) {

        // Get alert
        Alert alert;
        Cursor cursor = getContentResolver().query(
                Uri.withAppendedPath(Alert.URI, String.valueOf(id)), null, null, null, null);

        // If nothing we're done
        if (cursor == null) return;

        // Fetach data
        if (cursor.moveToNext()) {
            alert = new Alert(cursor);
            handleActionSetSnooze(alert, msg);
        }

        cursor.close();
    }

    /**
     * Handle action ACTION_SET_SNOOZE
     */
    private void handleActionSetSnooze(final Alert alert, final StringBuilder msg) {

        //Intent intent = new Intent(this, RicordaloReceiver.class);
        Intent intent = new Intent(this, RicordaloReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION);
        intent.putExtra(EXTRA_ALERTID, alert.get_id());
        intent.putExtra(EXTRA_COUNTER, alert.getCounter());
        intent.putExtra(EXTRA_PARAM1, alert.getName());
        intent.putExtra(EXTRA_PARAM2, alert.getDesc());

        final PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), (int) alert.get_id(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, calculateNextSnooze(alert, msg).getTimeInMillis(), pi);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * Handle action
     */
    private void handleActionSetAlarm(final Alert alert, final StringBuilder msg) {

        // Now we set the alarm,
        Intent intent = new Intent(this, RicordaloReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION);
        intent.putExtra(EXTRA_ALERTID, alert.get_id());
        intent.putExtra(EXTRA_COUNTER, alert.getCounter());
        intent.putExtra(EXTRA_PARAM1, alert.getName());
        intent.putExtra(EXTRA_PARAM2, alert.getDesc());

        final PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), (int )alert.get_id(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, calculateNextAlarm(alert, msg).getTimeInMillis(), pi);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Handle action ACTION_SET_ALARM
     */
    private void handleActionSetAlarm(final Long id, final StringBuilder msg) {

        // Log
        Log.d(TAG, " handleActionSetAlarm()");

        // Get alert
        Alert alert;
        Cursor cursor = getContentResolver().query(
                Uri.withAppendedPath(Alert.URI, String.valueOf(id)), null, null, null, null);

        // If nothing we're done
        if (cursor == null) return;

        // Fetach data
        if (cursor.moveToNext()) {
            alert = new Alert(cursor);
            handleActionSetAlarm(alert, msg);
        }
        cursor.close();
    }

    /**
     * Sets all alarms activated
     */
    private void handleActionSetAllAlarms() {

        // Log
        Log.d(TAG, " handleActionSetAllAlarms()");

        // Fetch all alarms activated
        String selection = DAOHelper.whereArgs(new String[]{Alert.Columns.STATUS});
        String[] selectionArgs = new String[]{"1"};

        Cursor cursor = getContentResolver().query(Alert.URI, null, selection, selectionArgs, null);

        // If nothing we're done
        if (cursor == null) return;

        // Foreach one set alarm
        while (cursor.moveToNext()) {

            Alert alert = new Alert(cursor);

            // TODO Check the last activated..
            // Calculate next alarm
            handleActionSetAlarm(alert, null);
        }
        cursor.close();
    }

}
