package edu.stevens.cs522.chatapp.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by 凡 on 2016/2/13.
 */
public class MessageContract {

    public static final String AUTHORITY = "edu.stevens.cs522.chatapp";
    public static final String CONTENT = "messages";

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

    public static final String MESSAGETEXT = "messageText";

    public static String getMessageText(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGETEXT));
    }

    public static void putMessageText(ContentValues values, String messageText) {
        values.put(MESSAGETEXT, messageText);
    }

    public static final String SENDER = "sender";

    public static String getSender(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
    }

    public static void putSender(ContentValues values, String sender) {
        values.put(SENDER, sender);
    }

    public static final String PEER_FK = "peer_fk";

    public static long getPeerFk(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(PEER_FK));
    }

    public static void putPeerFk(ContentValues values, long peer_fk) {
        values.put(PEER_FK, peer_fk);
    }

    public static final String CHATROOM = "chatroom";

    public static String getChatroom(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(CHATROOM));
    }

    public static void putChatroom(ContentValues values, String chatroom) {
        values.put(CHATROOM, chatroom);
    }


    //for messages with web service

    public static final String MESSAGE_IDENTIFIER = "msg_id"; //message identifier

    public static long getMessageIdentifier(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(MESSAGE_IDENTIFIER));
    }

    public static void putMessageIdentifier(ContentValues values, long identifier) {
        values.put(MESSAGE_IDENTIFIER, identifier);
    }

    public static final String CLIENT_IDENTIFIER = "client_id";

    public static long getClientIdentifier(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(CLIENT_IDENTIFIER));
    }

    public static void putClientIdentifier(ContentValues values, long identifier) {
        values.put(CLIENT_IDENTIFIER, identifier);
    }

    public static final String TIMESTAMP = "timeStamp";

    public static String getTimeStamp(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP));
    }

    public static void putTimeStamp(ContentValues values, String timeStamp) {
        values.put(TIMESTAMP, timeStamp);
    }


}
