package edu.stevens.cs522.chatapp.managers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chatapp.contracts.MessageContract;
import edu.stevens.cs522.chatapp.rest.PostMessage;

/**
 * Created by FEIFAN on 2016/2/28.
 */
public class PostMessageManager extends Manager {

    public PostMessageManager(Context context, IEntityCreator<PostMessage> creator, int loaderID) {
        super(context, creator, loaderID);
    }

    /**
     * Persist
     */

    //synchronous
    public Uri persist(PostMessage PostMessage) {

        ContentResolver cr = this.getSyncResolver();

        ContentValues values = new ContentValues();
        PostMessage.writeToProvider(values);

        return cr.insert(MessageContract.CONTENT_URI, values);// id in uri is left to the database
    }

    //asynchronous
    public void persistAsyn(final PostMessage PostMessage) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        ContentValues values = new ContentValues();
        PostMessage.writeToProvider(values);

        asyncCr.insertAsync(MessageContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri uri) {
                PostMessage.id = MessageContract.getId(uri);
            }
        });
    }

    /**
     * Query a PostMessage by id
     */

    //synchronous
    public PostMessage search(long id) {

        ContentResolver cr = this.getSyncResolver();

        return new PostMessage(cr.query(MessageContract.CONTENT_URI(id + ""), null, null, null, null));
    }

    //asynchronous
    public void searchAsyn(long id) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        asyncCr.queryAsync(MessageContract.CONTENT_URI(id + ""), null, null, null, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor cursor) {

               // singleMessage = new PostMessage(cursor);

            }
        });
    }

    /**
     * Query a list of PostMessage by name
     */

    //synchronous
    public Cursor search(String sender) {

        ContentResolver cr = this.getSyncResolver();

        String selection = MessageContract.SENDER + " = '" + sender + "'";

        Cursor cursor = cr.query(MessageContract.CONTENT_URI, null, selection, null, null);

        return cursor;
    }

    public List<PostMessage> fetchUnupdatedMsgs(String clientID) {

        ContentResolver cr = this.getSyncResolver();

        String selection = MessageContract.CLIENT_IDENTIFIER + " = " + clientID +
                " and " + MessageContract.MESSAGE_IDENTIFIER + " = 0" ;

        Cursor cursor = cr.query(MessageContract.CONTENT_URI, null, selection, null, null);

        List<PostMessage> messageList = new ArrayList<>();

        if (cursor.moveToFirst()) {

            do {
                PostMessage msg = new PostMessage(cursor);
                messageList.add(msg);
            }
            while (cursor.moveToNext());

        }

        cursor.close();

        return messageList;
    }

    public void deleteUnupdatedMsgs() {

        ContentResolver cr = this.getSyncResolver();

        String selection = MessageContract.MESSAGE_IDENTIFIER + " = 0";

        cr.delete(MessageContract.CONTENT_URI, selection, null);

    }

    /**
     * Get all
     */

    public Cursor getAll() {

        ContentResolver cr = this.getSyncResolver();

        return cr.query(MessageContract.CONTENT_URI, null, null, null, null);

    }

    public void getAllAsyn(final SimpleCursorAdapter sca) {


        this.executeQuery(MessageContract.CONTENT_URI, new IQueryListener<PostMessage>() {

            @Override
            public void handleResults(TypedCursor<PostMessage> results) {

                sca.swapCursor(results.getCursor());

            }

            @Override
            public void closeResults() {


            }
        });
    }

    public void getMessagesByNameAsyn(final SimpleCursorAdapter sca, String clientName) {

        String[] projection = new String[]{MessageContract.ID, MessageContract.MESSAGETEXT, MessageContract.MESSAGE_IDENTIFIER, MessageContract.SENDER, MessageContract.CLIENT_IDENTIFIER, MessageContract.TIMESTAMP, MessageContract.CHATROOM};

        String selection = MessageContract.SENDER + " = '" + clientName + "'";

        this.executeQuery(MessageContract.CONTENT_URI, projection, selection, null, new IQueryListener<PostMessage>() {

            @Override
            public void handleResults(TypedCursor<PostMessage> results) {

                sca.swapCursor(results.getCursor());

            }

            @Override
            public void closeResults() {


            }
        });
    }

    public void getMessagesByRoomAsyn(final SimpleCursorAdapter sca, String roomName) {

        String[] projection = new String[]{MessageContract.ID, MessageContract.MESSAGETEXT, MessageContract.MESSAGE_IDENTIFIER, MessageContract.SENDER, MessageContract.CLIENT_IDENTIFIER, MessageContract.TIMESTAMP, MessageContract.CHATROOM};

        String selection = MessageContract.CHATROOM + " = '" + roomName + "'";

        //use reexecute since each time different query
        this.reexecuteQuery(MessageContract.CONTENT_URI, projection, selection, null, new IQueryListener<PostMessage>() {

            @Override
            public void handleResults(TypedCursor<PostMessage> results) {

                sca.swapCursor(results.getCursor());

            }

            @Override
            public void closeResults() {


            }
        });
    }

    public void deleteAll() {

        ContentResolver cr = this.getSyncResolver();

        cr.delete(MessageContract.CONTENT_URI, null, null);

    }

    public void deleteAllAsyn() {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        asyncCr.deleteAsync(MessageContract.CONTENT_URI, null, null);
    }
}
