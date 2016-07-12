package edu.stevens.cs522.chatapp.managers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;

import java.util.List;

import edu.stevens.cs522.chatapp.contracts.MessageContract;
import edu.stevens.cs522.chatapp.entities.Message;

/**
 * Created by FEIFAN on 2016/2/28.
 */
public class MessageManager extends Manager {

    public MessageManager(Context context, IEntityCreator<Message> creator, int loaderID) {
        super(context, creator, loaderID);
    }

    //cache asynchronous search results

    private Message singleMessage;

    public Message getSingleMessageCache() {

        return singleMessage;

    }

    private List<Message> MessageList;

    private List<Message> MessageList() {

        return MessageList;

    }


    private TypedCursor<Message> typedCursor;


    public TypedCursor<Message> getMessageCursor() {
        return typedCursor;
    }

    /**
     * Persist
     */

    //synchronous
    public Uri persist(Message Message) {

        ContentResolver cr = this.getSyncResolver();

        ContentValues values = new ContentValues();
        Message.writeToProvider(values);

        return cr.insert(MessageContract.CONTENT_URI, values);// id in uri is left to the database
    }

    //asynchronous
    public void persistAsyn(final Message Message) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        ContentValues values = new ContentValues();
        Message.writeToProvider(values);

        asyncCr.insertAsync(MessageContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri uri) {
                Message.id = MessageContract.getId(uri);
            }
        });
    }

    /**
     * Query a Message by id
     */

    //synchronous
    public Message search(long id) {

        ContentResolver cr = this.getSyncResolver();

        return new Message(cr.query(MessageContract.CONTENT_URI(id + ""), null, null, null, null));
    }

    //asynchronous
    public void searchAsyn(long id) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        asyncCr.queryAsync(MessageContract.CONTENT_URI(id + ""), null, null, null, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor cursor) {

                singleMessage = new Message(cursor);

            }
        });
    }

    /**
     * Query a list of Message by name
     */

    //synchronous
    public Cursor search(String sender) {

        ContentResolver cr = this.getSyncResolver();

        String selection = MessageContract.SENDER + " = '" + sender + "'";

        Cursor cursor = cr.query(MessageContract.CONTENT_URI, null, selection, null, null);

        return cursor;
    }

    //asynchronous
    public void searchAsyn(String title) {

        String selection = MessageContract.SENDER + " = '" + title + "'";

        this.executeSimpleQuery(MessageContract.CONTENT_URI, null, selection, null, new ISimpleQueryListener<Message>() {
            @Override
            public void handleResults(List<Message> results) {

                MessageList = results;

            }
        });
    }

    /**
     * Get all
     */

    public Cursor getAll() {

        ContentResolver cr = this.getSyncResolver();

        return cr.query(MessageContract.CONTENT_URI, null, null, null, null);

    }

    public void getAllAsyn(final SimpleCursorAdapter sca) {


        this.executeQuery(MessageContract.CONTENT_URI, new IQueryListener<Message>() {

            @Override
            public void handleResults(TypedCursor<Message> results) {

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
}
