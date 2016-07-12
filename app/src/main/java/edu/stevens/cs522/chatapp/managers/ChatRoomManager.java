package edu.stevens.cs522.chatapp.managers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatapp.contracts.ChatRoomContract;
import edu.stevens.cs522.chatapp.entities.ChatRoom;

/**
 * Created by FEIFAN on 2016/4/6.
 */
public class ChatRoomManager extends Manager {

    public ChatRoomManager(Context context, IEntityCreator<ChatRoom> creator, int loaderID) {
        super(context, creator, loaderID);
    }

    /**
     * Persist
     */

    //synchronous
    public Uri persist(ChatRoom chatRoom) {

        ContentResolver cr = this.getSyncResolver();

        ContentValues values = new ContentValues();
        chatRoom.writeToProvider(values);

        return cr.insert(ChatRoomContract.CONTENT_URI, values);// id in uri is left to the database
    }

    //asynchronous
    public void persistAsyn(final ChatRoom chatRoom) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        ContentValues values = new ContentValues();
        chatRoom.writeToProvider(values);

        asyncCr.insertAsync(ChatRoomContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri uri) {
                chatRoom.id = ChatRoomContract.getId(uri);
            }
        });
    }

    public void isRoomNameExistedAsyn(String roomName, IContinue listener) {

        final AsyncContentResolver asyncCr = this.getAsyncResolver();

        String selection = ChatRoomContract.NAME + " = '" + roomName + "'";
        String[] projection = new String[]{ChatRoomContract.ID, ChatRoomContract.NAME};

        asyncCr.queryAsync(ChatRoomContract.CONTENT_URI, projection, selection, null, null, listener);

    }

    /**
     * Query a ChatRoom by id
     */

    //synchronous
    public ChatRoom search(long id) {

        ContentResolver cr = this.getSyncResolver();

        return new ChatRoom(cr.query(ChatRoomContract.CONTENT_URI(id + ""), null, null, null, null));
    }

    //asynchronous
    public void searchAsyn(long id) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        asyncCr.queryAsync(ChatRoomContract.CONTENT_URI(id + ""), null, null, null, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor cursor) {

                // singleMessage = new ChatRoom(cursor);

            }
        });
    }

    /**
     * Query a list of ChatRoom by name
     */

    //synchronous
    public Cursor search(String name) {

        ContentResolver cr = this.getSyncResolver();

        String selection = ChatRoomContract.NAME + " = '" + name + "'";

        Cursor cursor = cr.query(ChatRoomContract.CONTENT_URI, null, selection, null, null);

        return cursor;
    }


    /**
     * Get all
     */

    public Cursor getAll() {

        ContentResolver cr = this.getSyncResolver();

        return cr.query(ChatRoomContract.CONTENT_URI, null, null, null, null);

    }

    public void getAllAsyn(final SimpleCursorAdapter sca) {


        this.executeQuery(ChatRoomContract.CONTENT_URI, new IQueryListener<ChatRoom>() {

            @Override
            public void handleResults(TypedCursor<ChatRoom> results) {

                sca.swapCursor(results.getCursor());

            }

            @Override
            public void closeResults() {


            }
        });
    }


    public void deleteAll() {

        ContentResolver cr = this.getSyncResolver();

        cr.delete(ChatRoomContract.CONTENT_URI, null, null);

    }

    public void deleteAllAsyn() {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        asyncCr.deleteAsync(ChatRoomContract.CONTENT_URI, null, null);
    }
}