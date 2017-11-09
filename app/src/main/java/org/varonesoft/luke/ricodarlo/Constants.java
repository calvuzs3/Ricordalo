package org.varonesoft.luke.ricodarlo;

/**
 * Created by luke on 29/10/17.
 */

public final class Constants {

    public final class Service {
        //
        // Actions that IntentService can perform
        private static final String BASE_ACTION = "org.varonesoft.ricordalo.action.";
        public static final String ACTION_START_NOTIFICATION = BASE_ACTION + "NOTIFY_ALARM";
        public static final String ACTION_SET_SNOOZE = BASE_ACTION + "SET_SNOOZE";
        public static final String ACTION_SET_ALARM = BASE_ACTION + "SET_ALARM";
        public static final String ACTION_CANCEL_ALARM = BASE_ACTION + "CANCEL_ALARM";
        public static final String ACTION_SET_ALL_ALARMS = BASE_ACTION + "SET_ALL_ALARMS";

        // Extra arguments
        private static final String BASE_EXTRA = "org.varonesoft.ricordalo.extra.";
        public static final String EXTRA_PARAM1 = BASE_EXTRA + "PARAM1";
        public static final String EXTRA_PARAM2 = BASE_EXTRA + "PARAM2";
        public static final String EXTRA_COUNTER = BASE_EXTRA + "COUNTER";
        public static final String EXTRA_ALERTID = BASE_EXTRA + "ALERTID";
    }
}
