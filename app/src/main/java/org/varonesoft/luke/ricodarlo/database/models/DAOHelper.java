package org.varonesoft.luke.ricodarlo.database.models;

import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by luke on 29/10/17.
 *
 * Some functions
 */

public final class DAOHelper {


    /**
     * Append "where id is ?" to string
     */
    public static String whereId(final String orgWhere) {
        final StringBuilder sb = new StringBuilder();
        if (orgWhere != null) {
            sb.append("(");
            sb.append(orgWhere);
            sb.append(") AND ");
        }
        sb.append(BaseColumns._ID).append(" IS ?");
        return sb.toString();
    }


    /**
     *
     * @param _id long The value to return as an array
     * @return array args
     */
    public static String[] whereIdArg( final long _id ) {

        return new String[] { Long.toString(_id) };
    }


    /**
     * Append the id argument to array
     */
    public static String[] whereIdArg(final long _id,
                                      final String[] args) {

        if (args == null) {
            return whereIdArg(_id);
        }
        else {
            return DAOHelper.joinArrays(args, whereIdArg(_id));
        }
    }

    /**
     * Append "$ARG IS ?" to string
     */
    public static String whereArgs(final String[] args) {
        final StringBuilder sb = new StringBuilder();
        if ((args != null) && (args.length > 0)) {
            for (int i = 0; i < args.length ; i++) {
                sb.append("(");
                sb.append(args[i]);
                sb.append(" IS ?)");
                if (i<args.length-1)
                    sb.append(" AND ");
            }
        }
        return sb.toString();
    }

    /**
     * It add a prefix to the values of the array with a dot
     * @param prefix String prefix
     * @param array array of String values
     * @return new Array
     */
    public static String[] prefixDottedArray(final String prefix, final String[] array) {
        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = "" + prefix + "." + array[i];
        }
        return result;
    }

    public static String[] prefixArray(final String prefix, final String[] array) {
        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = "" + prefix + array[i];
        }
        return result;
    }

    public static String[] joinArrays(final String[]... arrays) {
        final ArrayList<String> list = new ArrayList<String>();
        for (final String[] array : arrays) {
            if (array != null) {
                for (final String txt : array) {
                    list.add(txt);
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Example: [] -> "" [a] -> "a" [a, b] -> "a,b"
     */
    // public static String arrayToCommaString(final String[] array) {
    // return arrayToCommaString("", array);
    // }

    public static String arrayToCommaString(final long... array) {
        StringBuilder result = new StringBuilder();
        for (final long val : array) {
            final String txt = Long.toString(val);
            if (result.length() > 0) result.append(",");
            result.append(txt);
        }
        return result.toString();
    }

    public static String arrayToCommaString(final String... array) {
        return arrayToCommaString("", array);
    }

    /**
     * Example (prefix=t.): [] -> "" [a] -> "t.a" [a, b] -> "t.a,t.b"
     */
    public static String arrayToCommaString(final String prefix,
                                            final String[] array) {
        return arrayToCommaString(prefix, array, "");
    }

    /**
     * Example (prefix=t., suffix=.45): [] -> "" [a] -> "t.a.45" [a, b] ->
     * "t.a.45,t.b.45"
     *
     * In addition, the txt itself can be referenced using %1$s in either prefix
     * or suffix. The prefix can be referenced as %2$s in suffix, and
     * vice-versa.
     *
     * So the following is valid:
     *
     * (prefix='t.', suffix=' AS %2$s%1$s')
     *
     * [listId] -> t.listId AS t.listId
     */
    protected static String arrayToCommaString(final String pfx,
                                               final String[] array, final String sfx) {
        StringBuilder result = new StringBuilder();
        for (final String txt : array) {
            if (result.length() > 0) result.append(",");
            result.append(String.format(pfx, txt, sfx));
            result.append(txt);
            result.append(String.format(sfx, txt, pfx));
        }
        return result.toString();
    }

    /**
     * Second and Third value is wrapped in '' ticks, NOT the first.
     */
    protected static String asEmptyCommaStringExcept(final String[] asColumns,
                                                     final String exceptCol1, final String asValue1,
                                                     final String exceptCol2, final String asValue2,
                                                     final String exceptCol3, final String asValue3) {
        StringBuilder result = new StringBuilder();
        for (final String colName : asColumns) {
            if (result.length() > 0) result.append(",");

            if (colName.equals(exceptCol2)) {
                result.append("'").append(asValue2).append("'");
            }
            else if (colName.equals(exceptCol3)) {
                result.append("'").append(asValue3).append("'");
            }
            else if (colName.equals(exceptCol1)) {
                result.append(asValue1);
            }
            else {
                result.append("null");
            }
        }
        return result.toString();
    }

    /**
     * Third and Fourth value is wrapped in '' ticks, NOT the first and second.
     */
    protected static String asEmptyCommaStringExcept(final String[] asColumns,
                                                     final String exceptCol1, final String asValue1,
                                                     final String exceptCol2, final String asValue2,
                                                     final String exceptCol3, final String asValue3,
                                                     final String exceptCol4, final String asValue4) {
        StringBuilder result = new StringBuilder();
        for (final String colName : asColumns) {
            if (result.length() > 0) result.append(",");

            if (colName.equals(exceptCol3)) {
                result.append("'").append(asValue3).append("'");
            }
            else if (colName.equals(exceptCol4)) {
                result.append("'").append(asValue4).append("'");
            }
            else if (colName.equals(exceptCol2)) {
                result.append(asValue2);
            }
            else if (colName.equals(exceptCol1)) {
                result.append(asValue1);
            }
            else {
                result.append("null");
            }
        }
        return result.toString();
    }
}
