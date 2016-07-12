package edu.stevens.cs522.chatapp.managers;

import java.util.List;

/**
 * Created by 凡 on 2016/2/22.
 */
public interface ISimpleQueryListener<T> {

    void handleResults(List<T> results);

}
