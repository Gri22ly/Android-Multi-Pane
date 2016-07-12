package edu.stevens.cs522.chatapp.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chatapp.contracts.ChatRoomContract;
import edu.stevens.cs522.chatapp.contracts.MessageContract;
import edu.stevens.cs522.chatapp.contracts.PeerContract;

/**
 * Created by FEIFAN on 2016/3/13.
 */
public class WebMessageProvider extends ContentProvider {

    /**
     * A new database for web service messages
     */
    public static final String DATABASE_NAME = "chat.db";

    public static final String MESSAGE_TABLE = "messages";

    public static final String PEER_TABLE = "peers";

    public static final String CHATROOM_TABLE = "chatrooms";

    public static final int DATABASE_VERSION = 1;

    public static final String MESSAGE_CREATE = "create table if not exists " +
            MESSAGE_TABLE + " (" +
            MessageContract.ID + " integer primary key autoincrement, " +
            MessageContract.MESSAGETEXT + " text, " +
            MessageContract.MESSAGE_IDENTIFIER + " integer not null, " +
            MessageContract.SENDER + " text not null, " +
            MessageContract.CLIENT_IDENTIFIER + " integer not null, " +
            MessageContract.TIMESTAMP + " text not null, " +
            MessageContract.CHATROOM + " text not null" +
            ")";

    public static final String PEER_CREATE = "create table if not exists " +
            PEER_TABLE + " (" +
            PeerContract.ID + " integer primary key autoincrement, " +
            PeerContract.NAME + " text not null" +
            ")";

    public static final String CHATROOM_CREATE = "create table if not exists " +
            CHATROOM_TABLE + " (" +
            ChatRoomContract.ID + " integer primary key autoincrement, " +
            ChatRoomContract.NAME + " text not null" +
            ")";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

            super(context, name, factory, version);

        }

        public void onCreate(SQLiteDatabase _db) {

            _db.execSQL(CHATROOM_CREATE);
            _db.execSQL(PEER_CREATE);
            _db.execSQL(MESSAGE_CREATE);

        }

        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            Log.w("TaskDBAdapter", "Upgrading from version" + _oldVersion + " to " + _newVersion);

            _db.execSQL("drop table if exists " + CHATROOM_TABLE);
            _db.execSQL("drop table if exists " + PEER_TABLE);
            _db.execSQL("drop table if exists " + MESSAGE_TABLE);


            onCreate(_db);
        }
    }

    /**
     * End
     */

    private static final int MESSAGES_ALL_ROWS = 11;
    private static final int MESSAGE_SINGLE_ROW = 12;

    private static final int CHATROOMS_ALL_ROWS = 21;
    private static final int CHATROOMS_SINGLE_ROW = 22;

    private static final int PEERS_ALL_ROWS = 31;
    private static final int PEERS_SINGLE_ROW = 32;


    public static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_PATH_ITEM, MESSAGE_SINGLE_ROW);

        uriMatcher.addURI(ChatRoomContract.AUTHORITY, ChatRoomContract.CONTENT_PATH, CHATROOMS_ALL_ROWS);
        uriMatcher.addURI(ChatRoomContract.AUTHORITY, ChatRoomContract.CONTENT_PATH_ITEM, CHATROOMS_SINGLE_ROW);

        uriMatcher.addURI(PeerContract.AUTHORITY, PeerContract.CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(PeerContract.AUTHORITY, PeerContract.CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);

    }

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {

        dbHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);//expose db helper to provider

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)) {

            case MESSAGES_ALL_ROWS:
                projection = new String[]{MessageContract.ID, MessageContract.MESSAGETEXT, MessageContract.MESSAGE_IDENTIFIER, MessageContract.SENDER, MessageContract.CLIENT_IDENTIFIER, MessageContract.TIMESTAMP, MessageContract.CHATROOM};
                db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(WebMessageProvider.MESSAGE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), MessageContract.CONTENT_URI);
                return cursor;

            case MESSAGE_SINGLE_ROW:
                projection = new String[]{MessageContract.ID, MessageContract.MESSAGETEXT, MessageContract.MESSAGE_IDENTIFIER, MessageContract.SENDER, MessageContract.CLIENT_IDENTIFIER, MessageContract.TIMESTAMP, MessageContract.CHATROOM};
                selection = MessageContract.ID + " = " + MessageContract.getId(uri);
                db = dbHelper.getReadableDatabase();
                return db.query(WebMessageProvider.MESSAGE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

            case CHATROOMS_ALL_ROWS:
                projection = new String[]{ChatRoomContract.ID, ChatRoomContract.NAME};
                db = dbHelper.getReadableDatabase();
                Cursor cursor1 = db.query(WebMessageProvider.CHATROOM_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor1.setNotificationUri(getContext().getContentResolver(), ChatRoomContract.CONTENT_URI);
                return cursor1;

            case CHATROOMS_SINGLE_ROW:
                projection = new String[]{ChatRoomContract.ID, ChatRoomContract.NAME};
                selection = ChatRoomContract.ID + " = " + ChatRoomContract.getId(uri);
                db = dbHelper.getReadableDatabase();
                return db.query(WebMessageProvider.CHATROOM_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

            case PEERS_ALL_ROWS:
                projection = new String[]{PeerContract.ID, PeerContract.NAME};
                db = dbHelper.getReadableDatabase();
                Cursor cursor2 = db.query(WebMessageProvider.PEER_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor2.setNotificationUri(getContext().getContentResolver(), PeerContract.CONTENT_URI);
                return cursor2;

            case PEERS_SINGLE_ROW:
                projection = new String[]{PeerContract.ID, PeerContract.NAME};
                selection = PeerContract.ID + " = " + PeerContract.getId(uri);
                db = dbHelper.getReadableDatabase();
                return db.query(WebMessageProvider.PEER_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }

    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {

            case MESSAGES_ALL_ROWS:

                return MessageContract.CONTENT_TYPE;

            case MESSAGE_SINGLE_ROW:

                return MessageContract.CONTENT_ITEM_TYPE;

            case PEERS_ALL_ROWS:

                return PeerContract.CONTENT_TYPE;

            case PEERS_SINGLE_ROW:

                return PeerContract.CONTENT_ITEM_TYPE;

            case CHATROOMS_ALL_ROWS:

            return ChatRoomContract.CONTENT_TYPE;

            case CHATROOMS_SINGLE_ROW:

            return ChatRoomContract.CONTENT_ITEM_TYPE;


            default:

                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        db = dbHelper.getWritableDatabase();
        long row;

        switch (uriMatcher.match(uri)) {

            case MESSAGES_ALL_ROWS:

                row = db.insert(WebMessageProvider.MESSAGE_TABLE, null, values);

                if (row > 0) {

                    Uri instanceUri = MessageContract.CONTENT_URI(row + "");
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(MessageContract.CONTENT_URI, null);
                    return instanceUri;
                }

            case PEERS_ALL_ROWS:

                row = db.insert(WebMessageProvider.PEER_TABLE, null, values);

                if (row > 0) {

                    Uri instanceUri = PeerContract.CONTENT_URI(row + "");
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(PeerContract.CONTENT_URI, null);
                    return instanceUri;
                }

            case CHATROOMS_ALL_ROWS:

                row = db.insert(WebMessageProvider.CHATROOM_TABLE, null, values);

                if (row > 0) {

                    Uri instanceUri = ChatRoomContract.CONTENT_URI(row + "");
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(ChatRoomContract.CONTENT_URI, null);
                    return instanceUri;
                }

            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int result;
        ContentResolver cr;

        switch (uriMatcher.match(uri)) {

            case MESSAGES_ALL_ROWS:

                db = dbHelper.getWritableDatabase();

                result = db.delete(WebMessageProvider.MESSAGE_TABLE, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(MessageContract.CONTENT_URI, null);

                return result;

            case MESSAGE_SINGLE_ROW:

                selection = MessageContract.ID + " = " + MessageContract.getId(uri);

                db = dbHelper.getWritableDatabase();

                result = db.delete(WebMessageProvider.MESSAGE_TABLE, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(MessageContract.CONTENT_URI, null);

                return result;

            case PEERS_ALL_ROWS:

                db = dbHelper.getWritableDatabase();

                result = db.delete(WebMessageProvider.PEER_TABLE, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(PeerContract.CONTENT_URI, null);

                return result;

            case PEERS_SINGLE_ROW:

                selection = PeerContract.ID + " = " + PeerContract.getId(uri);

                db = dbHelper.getWritableDatabase();

                result = db.delete(WebMessageProvider.PEER_TABLE, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(MessageContract.CONTENT_URI, null);

                return result;

            case CHATROOMS_ALL_ROWS:

                db = dbHelper.getWritableDatabase();

                result = db.delete(WebMessageProvider.CHATROOM_TABLE, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(ChatRoomContract.CONTENT_URI, null);

                return result;

            case CHATROOMS_SINGLE_ROW:

                selection = ChatRoomContract.ID + " = " + ChatRoomContract.getId(uri);

                db = dbHelper.getWritableDatabase();

                result = db.delete(WebMessageProvider.CHATROOM_TABLE, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(ChatRoomContract.CONTENT_URI, null);

                return result;

            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int result;
        ContentResolver cr;

        switch (uriMatcher.match(uri)) {

            case MESSAGES_ALL_ROWS:

                db = dbHelper.getWritableDatabase();

                result = db.update(WebMessageProvider.MESSAGE_TABLE, values, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(MessageContract.CONTENT_URI, null);

                return result;

            case MESSAGE_SINGLE_ROW:

                selection = MessageContract.ID + " = " + MessageContract.getId(uri);

                db = dbHelper.getWritableDatabase();

                result = db.update(WebMessageProvider.MESSAGE_TABLE, values, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(MessageContract.CONTENT_URI, null);

                return result;

            case PEERS_ALL_ROWS:

                db = dbHelper.getWritableDatabase();

                result = db.update(WebMessageProvider.PEER_TABLE, values, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(PeerContract.CONTENT_URI, null);

                return result;

            case PEERS_SINGLE_ROW:

                selection = PeerContract.ID + " = " + PeerContract.getId(uri);

                db = dbHelper.getWritableDatabase();

                result = db.update(WebMessageProvider.PEER_TABLE, values, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(PeerContract.CONTENT_URI, null);

                return result;

            case CHATROOMS_ALL_ROWS:

                db = dbHelper.getWritableDatabase();

                result = db.update(WebMessageProvider.CHATROOM_TABLE, values, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(ChatRoomContract.CONTENT_URI, null);

                return result;

            case CHATROOMS_SINGLE_ROW:

                selection = ChatRoomContract.ID + " = " + ChatRoomContract.getId(uri);

                db = dbHelper.getWritableDatabase();

                result = db.update(WebMessageProvider.CHATROOM_TABLE, values, selection, null);

                cr = getContext().getContentResolver();
                cr.notifyChange(ChatRoomContract.CONTENT_URI, null);

                return result;

            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }


}
