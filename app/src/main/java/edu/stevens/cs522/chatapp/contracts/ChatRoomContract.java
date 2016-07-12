package edu.stevens.cs522.chatapp.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by FEIFAN on 2016/4/5.
 */
public class ChatRoomContract {

    public static final String AUTHORITY = "edu.stevens.cs522.chatapp";
    public static final String CONTENT = "chatRooms";

    public static final Uri CONTENT_URI(String authority, String path) {
        return new Uri.Builder().scheme("content").authority(authority).path(path).build();
    }

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, CONTENT);

    public static Uri withExtendedPath(Uri uri, String[] path) {
        Uri.Builder builder = uri.buildUpon();
        for (String p : path) {
            builder.appendPath(p);
        }
        return
                builder.build();
    }

    public static Uri CONTENT_URI(String id) {

        String[] idPath = {id};

        return withExtendedPath(CONTENT_URI, idPath);
    }

    public static long getId(Uri uri) {

        return Long.parseLong(uri.getLastPathSegment());

    }

    public static String CONTENT_PATH(Uri uri) {

        return uri.getPath().substring(1);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));

    public static final String CONTENT_TYPE = "vnd.android.cursor/vnd." + AUTHORITY + "." + CONTENT + "s";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + CONTENT;

    public static final String ID = "_id";

    public static long getId(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(ID));
    }

    public static void putId(ContentValues values, long id) {
        values.put(ID, id);
    }

    public static final String NAME = "name";

    public static String getName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(NAME));
    }

    public static void putName(ContentValues values, String name) {
        values.put(NAME, name);
    }


}
