package edu.stevens.cs522.chatapp.managers;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by 凡 on 2016/2/22.
 */
public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor>{


    private String tag;
    private Context context;
    private Uri uri;
    private int loaderID;
    private IEntityCreator<T> creator;
    private IQueryListener<T> listener;

    private String[] projection;
    private String selection;
    private String[] selectionArgs;


    private QueryBuilder(String tag, Context context, Uri uri, int loaderID, IEntityCreator<T> creator, IQueryListener<T> listener)
    {
        this.tag = tag;
        this.context= context;
        this.uri = uri;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;
    }

    private  QueryBuilder(String tag, Context context, Uri uri, int loaderID, String[] projection, String selection, String[] selectionArgs, IEntityCreator<T> creator, IQueryListener<T> listener){

        this(tag, context, uri, loaderID, creator, listener);
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;

    }

    public static <T> void executeQuery(String tag, Activity context, Uri uri, int loaderID, IEntityCreator<T> creator, IQueryListener<T> listener) {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);

        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);

    }

    public static <T> void executeQuery(String tag, Activity context, Uri uri, int loaderID, String[] projection, String selection, String[] selectionArgs, IEntityCreator<T> creator, IQueryListener<T> listener) {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, projection, selection, selectionArgs, creator, listener);

        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);

    }

    public static <T> void reexecuteQuery(String tag, Activity context, Uri uri, int loaderID, String[] projection, String selection, String[] selectionArgs, IEntityCreator<T> creator, IQueryListener<T> listener) {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, projection, selection, selectionArgs, creator, listener);

        LoaderManager lm = context.getLoaderManager();
        lm.restartLoader(loaderID, null, qb);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == loaderID) {

            return new CursorLoader(context, uri, projection, selection, selectionArgs, null);

        }

        else throw new IllegalStateException("Unexpected loader callback");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (loader.getId() == loaderID) {

            listener.handleResults(new TypedCursor<T>(cursor, creator));

        }

        else throw new IllegalStateException("Unexpected loader callback");


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (loader.getId() == loaderID) {

            listener.closeResults();

        }
        else {

            throw new IllegalStateException("Unexpected loader callback");

        }
    }



}
