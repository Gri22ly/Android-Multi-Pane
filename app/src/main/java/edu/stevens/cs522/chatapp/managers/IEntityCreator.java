package edu.stevens.cs522.chatapp.managers;

import android.database.Cursor;

/**
 * Created by 凡 on 2016/2/22.
 */
public interface IEntityCreator<T> {

    T create(Cursor cursor);

}
