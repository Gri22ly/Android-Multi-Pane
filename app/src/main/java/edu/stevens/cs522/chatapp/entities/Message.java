package edu.stevens.cs522.chatapp.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chatapp.contracts.MessageContract;

/**
 * Created by 凡 on 2016/2/13.
 */
public class Message implements Parcelable{

    public long id;
    public String messageText;
    public String sender;
    public long peer;

    public Message() {
    }

    public Message(long id, String messageText, String sender, long peer) {
        this.id = id;
        this.messageText = messageText;
        this.sender = sender;
        this.peer = peer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeLong(id);
        out.writeString(messageText);
        out.writeString(sender);
        out.writeLong(peer);

    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public Message(Parcel in){

        id = in.readLong();
        messageText = in.readString();
        sender = in.readString();
        peer = in.readLong();

    }

    public Message(Cursor cursor){

        this.id = MessageContract.getId(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.sender = MessageContract.getSender(cursor);
        this.peer = MessageContract.getPeerFk(cursor);

    }

    public void writeToProvider(ContentValues values){

        MessageContract.putMessageText(values, this.messageText);
        MessageContract.putSender(values, this.sender);
        MessageContract.putPeerFk(values, this.peer);
        
    }
}
