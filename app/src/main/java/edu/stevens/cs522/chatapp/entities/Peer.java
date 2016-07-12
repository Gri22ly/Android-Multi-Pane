package edu.stevens.cs522.chatapp.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chatapp.contracts.PeerContract;

/**
 * Created by 凡 on 2016/2/13.
 */

public class Peer implements Parcelable {

    public long id;
    public String name;


    public Peer() {

    }

    public Peer(long id, String name) {
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

    public static final Creator<Peer> CREATOR = new Creator<Peer>(){

        @Override
        public Peer[] newArray(int size)
        {
            return new Peer[size];
        }

        @Override
        public Peer createFromParcel(Parcel in)
        {
            return new Peer(in);
        }

    };

    public Peer(Parcel in){

        id = in.readLong();
        name = in.readString();

    }


    public Peer(Cursor cursor){
        this.id = PeerContract.getId(cursor);
        this.name = PeerContract.getName(cursor);

    }

    public void writeToProvider(ContentValues values){

        PeerContract.putName(values, this.name);

    }

}
