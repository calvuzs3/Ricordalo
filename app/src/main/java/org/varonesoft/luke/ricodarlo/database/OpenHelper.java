package org.varonesoft.luke.ricodarlo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.varonesoft.luke.ricodarlo.Util.Log;
import org.varonesoft.luke.ricodarlo.database.models.Alert;


/**
 * Created by luke on 27/10/17.
 * <p>
 * Open Helper extension.
 */

public class OpenHelper extends SQLiteOpenHelper {

    // Database properties
    public static String DATABASE_NAME = "ricordami.db";
    public static int DATABASE_VERSION = 1;
    // TAG
    private static String TAG = "OpenHelper";
    // Self reference, it'a singleton
    private static OpenHelper db;

    // Private constructor
    private OpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Never keep a reference to this, get a reference instead
    public static OpenHelper getInstance(final Context context) {
        if (db == null) {
            db = new OpenHelper(context);
        }
        return db;
    }

    /*
     * Private init()
     *
     * Initialize the database with common values
     */
    private static void init(SQLiteDatabase database) {
        StringBuilder msg = new StringBuilder();
        msg.append("init() ");
        database.beginTransaction();
        try {
            Alert.init(database);
            msg.append("done.");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        Log.d(TAG, msg.toString());
    }

    /*
     * This method create the db if it doesn't exists
     *
     * it calls onCreate() eventually.
     */
    @Override
    public void onOpen(SQLiteDatabase database) {
        super.onOpen(database);
        if (!database.isReadOnly()) {
            // Enable foreign key constraints
            // This requires android sdk >=16
            database.setForeignKeyConstraintsEnabled(true);
        }
    }

    /*
     * Method is called during creation of the database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {

        StringBuilder msg = new StringBuilder("onCreate() \n");

        database.execSQL("DROP TABLE IF EXISTS " + Alert.TABLE_NAME);
        msg.append("DROPPED TABLE " + Alert.TABLE_NAME  + "\n");

        database.execSQL(Alert.DATABASE_CREATE);
        msg.append(Alert.DATABASE_CREATE + "\n");

        String init = Alert.init(database);
        msg.append(init + "\ndone.");

        Log.d(TAG, msg.toString());
    }

    /*
     * Method is called during an upgrade of the database,
     *
     * e.g. if you increase the database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldV, int newV) {
        // Empty
        Log.d(TAG, "onUpgrade() Nothing to do here now.. You shouldn't see this !");
    }

}

