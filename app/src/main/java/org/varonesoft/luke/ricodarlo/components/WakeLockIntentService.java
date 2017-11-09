package org.varonesoft.luke.ricodarlo.components;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import org.varonesoft.luke.ricodarlo.Util.Log;

/**
 * Simply get a wakeLock statically
 * <p>
 * Created by luke on 06/11/17.
 */

public abstract class WakeLockIntentService extends IntentService {

    /* TAG */
    private static final String TAG = WakeLockIntentService.class.getSimpleName();

    /* Lock name */
    private static final String LOCK_NAME = "org.varonesoft.ricordalo.static";

    /* WakeLock */
    private static PowerManager.WakeLock mLock = null;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WakeLockIntentService(String name) {
        super(name);
    }

    synchronized
    private static PowerManager.WakeLock getLock(Context context) {
        if (mLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME);
            mLock.setReferenceCounted(false);
        }
        return mLock;
    }

    public static void acquireLock(Context context) {
        getLock(context).acquire();
    }

    public static void releaseLock(Context context) {
        getLock(context).release();
    }

    @Override
    final
    protected void onHandleIntent(@Nullable Intent intent) {

        try {

            // Perform task
            handleIntent(intent);

        } finally {

            if (mLock != null) {
                synchronized (mLock) {

                    // sanity check for null as this is a public method
                    if (mLock.isHeld()) {
                        Log.d(TAG, "Releasing wakelock");
                        try {
                            mLock.release();
                        } catch (Throwable th) {
                            // ignoring this exception, probably wakeLock was already released
                            Log.d(TAG, "Already released wakelock");
                        }
                    } else {
                        // should never happen during normal workflow
                        Log.e(TAG, "Wakelock reference is null");
                    }
                }
            }
        }
    }

    /**
     * Override this function as it is onHandleIntent(Intent intent)
     *
     * @param intent the intent passed to this service
     */
    abstract void handleIntent(Intent intent);
}
