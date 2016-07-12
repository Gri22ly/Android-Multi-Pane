package edu.stevens.cs522.chatapp.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


import edu.stevens.cs522.chatapp.contracts.ChatRoomContract;

/**
 * Created by 凡 on 2016/2/13.
 */

public class ChatRoom implements Parcelable {

    public long id;
    public String name;


    public ChatRoom() {

    }

    public ChatRoom(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeLong(id);
        out.writeString(name);


    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>(){

        @Override
        public ChatRoom[] newArray(int size)
        {
            return new ChatRoom[size];
        }

        @Override
        public ChatRoom createFromParcel(Parcel in)
        {
            return new ChatRoom(in);
        }

    };

    public ChatRoom(Parcel in){

        id = in.readLong();
        name = in.readString();

    }


    public ChatRoom(Cursor cursor){
        this.id = ChatRoomContract.getId(cursor);
        this.name = ChatRoomContract.getName(cursor);

    }

    public void writeToProvider(ContentValues values){

        ChatRoomContract.putName(values, this.name);

    }

}
