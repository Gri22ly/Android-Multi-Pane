package edu.stevens.cs522.chatapp.managers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatapp.contracts.PeerContract;
import edu.stevens.cs522.chatapp.entities.Peer;

/**
 * Created by FEIFAN on 2016/4/6.
 */
public class PeerManager extends Manager {

    public PeerManager(Context context, IEntityCreator<Peer> creator, int loaderID) {
        super(context, creator, loaderID);
    }

    /**
     * Persist
     */

    //synchronous
    public Uri persist(Peer chatRoom) {

        ContentResolver cr = this.getSyncResolver();

        ContentValues values = new ContentValues();
        chatRoom.writeToProvider(values);

        return cr.insert(PeerContract.CONTENT_URI, values);// id in uri is left to the database
    }

    //asynchronous
    public void persistAsyn(final Peer chatRoom) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        ContentValues values = new ContentValues();
        chatRoom.writeToProvider(values);

        asyncCr.insertAsync(PeerContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri uri) {
                chatRoom.id = PeerContract.getId(uri);
            }
        });
    }

    /**
     * Query a Peer by id
     */

    //synchronous
    public Peer search(long id) {

        ContentResolver cr = this.getSyncResolver();

        return new Peer(cr.query(PeerContract.CONTENT_URI(id + ""), null, null, null, null));
    }

    //asynchronous
    public void searchAsyn(long id) {

        AsyncContentResolver asyncCr = this.getAsyncResolver();

        asyncCr.queryAsync(PeerContract.CONTENT_URI(id + ""), null, null, null, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor cursor) {

                // singleMessage = new Peer(cursor);

            }
        });
    }

    /**
     * Query a list of Peer by name
     */

    //synchronous
    public Cursor search(String name) {

        ContentResolver cr = this.getSyncResolver();

        String selection = PeerContract.NAME + " = '" + name + "'";

        Cursor cursor = cr.query(PeerContract.CONTENT_URI, null, selection, null, null);

        return cursor;
    }


    /**
     * Get all
     */

    public Cursor getAll() {

        ContentResolver cr = this.getSyncResolver();

        return cr.query(PeerContract.CONTENT_URI, null, null, null, null);

    }

    public void getAllAsyn(final SimpleCursorAdapter sca) {


        this.executeQuery(PeerContract.CONTENT_URI, new IQueryListener<Peer>() {

            @Override
            public void handleResults(TypedCursor<Peer> results) {

                sca.swapCursor(results.getCursor());

            }

            @Override
            public void closeResults() {


            }
        });
    }


    public void deleteAll() {

        ContentResolver cr = this.getSyncResolver();

        cr.delete(PeerContract.CONTENT_URI, null, null);

    }
}