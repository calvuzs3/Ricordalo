package org.varonesoft.luke.ricodarlo.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.varonesoft.luke.ricodarlo.Util.Log;
import org.varonesoft.luke.ricodarlo.database.models.*;

import java.util.ArrayList;

public class RicordaloProvider extends ContentProvider {

    // TAG
    private static String TAG = "Provider";

    // MUST
    public static final String AUTHORITY = "org.varonesoft.ricordalo.provider";
    public static final String SCHEME = "content://";
    public static final String BASE_CONTENT_TYPE = "vnd.android.cursor.item/vnd.ricordalo.";
    public static final String BASE_TABLE_NAME = "tbl_";

    // UriMatcher
    private static final UriMatcher mURIMatcher;

    // Static declarations
    // add Uris of the datamodels
    static {
        mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        Alert.addMatcherUris(mURIMatcher);
    }

    public RicordaloProvider() {
    }

    /*
     * Wrapper for deleting items
     */
    synchronized
    private int safeDeleteItem(final SQLiteDatabase db,
                               final String tableName, final Uri uri, final String selection,
                               final String[] selectionArgs) {
        db.beginTransaction();
        int result = 0;
        try {
            result += db.delete(
                    tableName,
                    DAOHelper.whereId(selection),
                    DAOHelper.joinArrays(selectionArgs,
                            new String[]{uri.getLastPathSegment()}));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result;
    }

    @Override
    synchronized
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = OpenHelper.getInstance(getContext()).getWritableDatabase();
        int result = 0;
        String id;

        switch (mURIMatcher.match(uri)) {
            case Alert.BASEITEMCODE:
                result += safeDeleteItem(sqlDB, Alert.TABLE_NAME, uri, selection, selectionArgs);
                break;
            case Alert.BASEURICODE:
                result += sqlDB.delete(Alert.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        if (result > 0) {
            DAO.notifyProviderOnChange(getContext(), uri);
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        switch (mURIMatcher.match(uri)) {
            case Alert.BASEITEMCODE:
            case Alert.BASEURICODE:
                return Alert.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri result = null;

        SQLiteDatabase sqlDB = OpenHelper.getInstance(getContext()).getWritableDatabase();

        sqlDB.beginTransaction();
        try {
            final DAO item;

            switch (mURIMatcher.match(uri)) {
                case Alert.BASEURICODE:
                    item = new Alert(values);
                    break;

                default:
                    throw new IllegalArgumentException("Insertion failed. URI: " + uri);
            }

            result = item.insert(getContext(), sqlDB);
            sqlDB.setTransactionSuccessful();

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            sqlDB.endTransaction();
        }

        if (result != null) {
            DAO.notifyProviderOnChange(getContext(), uri);
        }
        return result;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // We could use queryBuilder
        SQLiteDatabase sqlDB = OpenHelper.getInstance(getContext()).getReadableDatabase();

        Cursor result = null;
        final long id;

        // TODO check if the caller has requested a column which does not exists
        // abstract boolean checkColumns(Uri uri, String[] projection);
        //
        switch (mURIMatcher.match(uri)) {
            case Alert.BASEURICODE:
                result = sqlDB.query(Alert.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(),
                        Alert.URI);
                break;
            case Alert.BASEITEMCODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = sqlDB.query(Alert.TABLE_NAME, projection,
                        DAOHelper.whereId(selection),
                        DAOHelper.joinArrays(selectionArgs, new String[]{String.valueOf(id)}),
                        null, null, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;


            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return result;
    }

    @Override
    synchronized
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        //
        final SQLiteDatabase sqlDB = OpenHelper.getInstance(getContext()).getWritableDatabase();
        final Long id;
        final ArrayList<Uri> updateUris = new ArrayList<Uri>();
        int result = 0;

        sqlDB.beginTransaction();
        try {
            switch (mURIMatcher.match(uri)) {
                case Alert.BASEURICODE:
                    updateUris.add(Alert.URI);
                    result = sqlDB.update(Alert.TABLE_NAME,
                            values, selection, selectionArgs);
                    break;
                case Alert.BASEITEMCODE:
                    updateUris.add(Alert.URI);
                    id = Long.parseLong(uri.getLastPathSegment());
                    result = sqlDB.update(Alert.TABLE_NAME, values,
                            DAOHelper.whereId(selection),
                            DAOHelper.whereIdArg(id, selectionArgs));
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri.toString());
            }
            if (result >= 0)
                sqlDB.setTransactionSuccessful();
        } finally {
            sqlDB.endTransaction();
        }
        if (result > 0)
            for (Uri u : updateUris) {
                DAO.notifyProviderOnChange(getContext(), u);
            }
        return result;
    }

    /*
 * Destroy the database
 *
 * just in case we need to recreate it from scratch
 */
    public void destroyDatabase() {
        OpenHelper sqlDB = OpenHelper.getInstance(getContext());
        SQLiteDatabase db = OpenHelper.getInstance(getContext()).getWritableDatabase();
        sqlDB.onCreate(db);
        Log.d(TAG, "Database recreated.");
    }
}
