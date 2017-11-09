package org.varonesoft.luke.ricodarlo.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.varonesoft.luke.ricodarlo.Util.Log;

import static org.varonesoft.luke.ricodarlo.Constants.Service.ACTION_START_NOTIFICATION;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_ALERTID;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_COUNTER;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_PARAM1;
import static org.varonesoft.luke.ricodarlo.Constants.Service.EXTRA_PARAM2;

public class RicordaloReceiver extends BroadcastReceiver {

    // Tag
    private static final String TAG = RicordaloReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        /* Check */
        if (intent == null ){
            Log.d(TAG, "Received a null Intent");
            return;
        }

        /* Acquire the WakeLock */
        WakeLockIntentService.acquireLock( context);

        /* Wich action */
        final String action = intent.getAction();
        if (ACTION_START_NOTIFICATION.equals(action)) {

            // Get extras && check..
            Bundle extras = intent.getExtras();
            final String param1 = intent.getStringExtra(EXTRA_PARAM1);
            final String param2 = intent.getStringExtra(EXTRA_PARAM2);
            final Long id =       intent.getLongExtra(EXTRA_ALERTID, -1L);
            final int counter =   intent.getIntExtra(EXTRA_COUNTER, 0);

            // Set new intent
            Intent i = new Intent(context, RicordaloIntentService.class);
            i.setAction(ACTION_START_NOTIFICATION);
            i.putExtras(extras);

            // Log
            Log.d( TAG, String.format("Action [%s]", action ));
            Log.d( TAG, String.format("Extra [id=%d p1=%s p2=%s counter=%d]", id, param1, param2, counter));

            // Start
            context.startService(i);

        }else {

                Log.d( TAG, String.format("Unidentified Action [%s]", intent.getAction()));
        }
    }
}
