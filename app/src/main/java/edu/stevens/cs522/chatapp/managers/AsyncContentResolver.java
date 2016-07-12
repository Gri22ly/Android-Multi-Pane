package edu.stevens.cs522.chatapp.managers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by 凡 on 2016/2/22.
 */
public class AsyncContentResolver extends AsyncQueryHandler {


    public AsyncContentResolver(ContentResolver cr) {
        super(cr);
    }

    public void queryAsync(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, IContinue<Cursor> callback) {

        this.startQuery(0, callback, uri, projection, selection, selectionArgs, sortOrder);

    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor c) {

        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
            callback.kontinue(c);
        }

    }


    public void insertAsync(Uri uri, ContentValues values, IContinue<Uri> callback) {

        this.startInsert(0, callback, uri, values);

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Uri> callback = (IContinue<Uri>) cookie;
            callback.kontinue(uri);
        }

    }



    public void deleteAsync(Uri uri, String selection, String[] selectionArgs) {

        this.startDelete(0, null, uri, selection, selectionArgs);

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result)
    {

//        if (cookie != null) {
//            @SuppressWarnings("unchecked")
//            IContinue<Uri> callback = (IContinue<Uri>) cookie;
//            Uri uri = BookContract.CONTENT_URI(result + "");
//            callback.kontinue(uri);
//        }

    }


    public void updateAsync(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        this.startUpdate(0, null, uri, values, selection, selectionArgs);

    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {

//        if (cookie != null) {
//            @SuppressWarnings("unchecked")
//            IContinue<Uri> callback = (IContinue<Uri>) cookie;
//            Uri uri = BookContract.CONTENT_URI(result + "");
//            callback.kontinue(uri);
//        }

    }




}
