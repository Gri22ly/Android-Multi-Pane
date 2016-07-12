package edu.stevens.cs522.chatapp.managers;

/**
 * Created by 凡 on 2016/2/22.
 */
public interface IQueryListener<T> {

    void handleResults(TypedCursor<T> results);

    void closeResults();

}
