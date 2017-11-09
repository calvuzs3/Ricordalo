package org.varonesoft.luke.ricodarlo.database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.varonesoft.luke.ricodarlo.database.RicordaloProvider;

/**
 * Created by luke on 27/10/17.
 *
 * Data Access Object
 */
public abstract class DAO {


    /**
     * The id
     */
    private long _id = -1;


    /**
     * CONSTRUCTOR
     *
     * @param c Cursor
     */
    protected DAO(final Cursor c) {
    }

    /**
     * CONSTRUCTOR
     *
     * @param values values
     */
    protected DAO(final ContentValues values) {
    }

    /**
     * CONSTRUCTOR
     */
    protected DAO() {
    }

    // Getter and Setter
    public long get_id() {

        return _id;
    }

    public void set_id(final Uri uri) {

        set_id(Long.parseLong(uri.getLastPathSegment()));
    }

    public void set_id(long _id) {

        this._id = _id;
    }

    /**
     * @return Uri of the object
     */
    Uri getUri() {

        return Uri.withAppendedPath(getBaseUri(), Long.toString(_id));
    }

    /**
     * @return Uri of all objects
     */
    Uri getBaseUri() {

        return Uri.withAppendedPath(Uri.parse(RicordaloProvider.SCHEME + RicordaloProvider.AUTHORITY), getPath());
    }

    /**
     * UPDATE
     *
     * @param context Context
     * @param db      DbOpenHelper
     * @return true if updated
     */
    public synchronized boolean update(final Context context,
                                       final SQLiteDatabase db) {

        int result = 0;
        db.beginTransaction();

        try {

            if (_id > 0) {
                result += db.update(getTableName(), getContent(),
                        DAOHelper.whereId(null),
                        DAOHelper.whereIdArg(_id));
            }

            if (result > 0) {
                db.setTransactionSuccessful();
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }

        if (result > 0) {
            notifyProviderOnChange(context);
        }

        return result > 0;
    }

    /**
     * INSERT
     *
     * @param context Context
     * @param db      Db
     * @return Uri whit last path segment the id
     */
    public synchronized Uri insert(final Context context,
                                   final SQLiteDatabase db) {
        Uri retval = null;
        db.beginTransaction();
        try {
            beforeInsert(context, db);

            final long id = db.insert(getTableName(), null, getContent());

            if (id == -1) {
                throw new SQLException("Insert failed in " + getTableName());
            } else {
                _id = id;
                afterInsert(context, db);
                db.setTransactionSuccessful();
                retval = getUri();
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }

        if (retval != null) {
            notifyProviderOnChange(context);
        }
        return retval;
    }

    /**
     * REMOVE
     *
     * @param context Context
     * @param db      Db
     * @return int Number of rows affected
     */
    public synchronized int remove(final Context context,
                                   final SQLiteDatabase db) {
        final int result = db.delete(getTableName(),
                DAOHelper.whereId(null),
                DAOHelper.whereIdArg(_id));

        if (result > 1) {
            notifyProviderOnChange(context);
        }

        return result;
    }

    /**
     * As it says
     *
     * @param context Context
     */
    protected void notifyProviderOnChange(final Context context) {
        notifyProviderOnChange(context, getUri());
    }

    /**
     * Overloading
     *
     * @param context Context
     * @param uri     Uri
     */
    public static void notifyProviderOnChange(final Context context,
                                              final Uri uri) {
        try {
            context.getContentResolver().notifyChange(uri, null, false);
        } catch (UnsupportedOperationException e) {
            // Catch this for test suite. Mock provider cant notify
        }
    }


    protected void beforeInsert(final Context context, final SQLiteDatabase db) {

    }

    protected void afterInsert(final Context context, final SQLiteDatabase db) {

    }

    protected void beforeUpdate(final Context context, final SQLiteDatabase db) {

    }

    protected void afterUpdate(final Context context, final SQLiteDatabase db) {

    }

    protected void beforeRemove(final Context context, final SQLiteDatabase db) {

    }

    protected void afterRemove(final Context context, final SQLiteDatabase db) {

    }

    /**
     * Convenience method for normal operations.
     *
     * @param context Context
     * @return int Number of db-rows affected. Fail if < 1,
     */
    public abstract int save(final Context context);

    public abstract ContentValues getContent();

    protected abstract String getTableName();

    protected abstract String getPath();

    public abstract String getContentType();
}