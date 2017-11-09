package org.varonesoft.luke.ricodarlo.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.varonesoft.luke.ricodarlo.Util.Log;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.content.Intent.ACTION_TIMEZONE_CHANGED;
import static android.content.Intent.ACTION_TIME_CHANGED;
import static org.varonesoft.luke.ricodarlo.Constants.Service.ACTION_SET_ALL_ALARMS;


/**
 * This receiver has to set all alarms again
 *
 * <action android:name="android.intent.action.BOOT_COMPLETED"/>
 * <action android:name="android.intent.action.TIMEZONE_CHANGED"/>
 * <action android:name="android.intent.action.TIME_SET"/>
 */
public class BootReceiver extends BroadcastReceiver {

    /* TAG */
    private static final String TAG = BootReceiver.class.getSimpleName();

    /* Core */
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null ){
            Log.d(TAG, "Received a null Intent");
            return;
        }

        /* Acquire the WakeLock */
        WakeLockIntentService.acquireLock( context);

        /* Wich action */
        final String action = intent.getAction();

        Log.d(TAG, String.format("Broadcasted Action [%s]", intent.getAction() ));

        if (ACTION_BOOT_COMPLETED.equals(action)) {

            // Set all alarms
            Intent i = new Intent(context, RicordaloIntentService.class);
            i.setAction(ACTION_SET_ALL_ALARMS);

            context.startService(i);
        } else if ( ACTION_TIMEZONE_CHANGED.equals(action) || ACTION_TIME_CHANGED.equals(action)) {

            // Reset all alarms

            // Set all alarms again

            // TODO cut this part once its done
            WakeLockIntentService.releaseLock( context);
        }
    }
}
