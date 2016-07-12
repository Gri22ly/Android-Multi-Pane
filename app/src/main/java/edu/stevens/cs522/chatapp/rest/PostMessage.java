package edu.stevens.cs522.chatapp.rest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import edu.stevens.cs522.chatapp.contracts.MessageContract;

/**
 * Created by FEIFAN on 2016/3/13.
 */
public class PostMessage extends Request {

    public long id;
    public String messageText;
    public Date timestamp;
    public long messageID;
    public String chatroom;

    public PostMessage(Map<String, String> requestHeaders, Uri requestUri, String requestEntity) {

        super(requestHeaders, requestUri, requestEntity);

    }


    public PostMessage() {

        super();

    }

    @Override
    public Map<String, String> getRequestHeaders() {

        if (requestHeaders != null) {
            return this.requestHeaders;
        } else {
            return null;
        }

    }

    @Override
    public Uri getRequestUri() {

        if (requestUri != null) {
            return this.requestUri;
        } else {
            return null;
        }

    }

    @Override
    public String getRequestEntity() {

        if (requestEntity != null) {
            return this.requestEntity;
        } else {
            return null;
        }

    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException {

        int code = connection.getResponseCode();
        String message = connection.getResponseMessage();

        Response response = new Response(code, message, parseJson(rd));//rd simply parsed here

        return response;
    }


    public static final Parcelable.Creator<PostMessage> CREATOR = new Parcelable.Creator<PostMessage>() {
        @Override
        public PostMessage createFromParcel(Parcel in) {
            return new PostMessage(in);
        }

        @Override
        public PostMessage[] newArray(int size) {
            return new PostMessage[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(messageText);

    }

    public PostMessage(Parcel in) {

        messageText = in.readString();

    }

    public PostMessage(Cursor cursor) {

        this.id = MessageContract.getId(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.messageID = MessageContract.getMessageIdentifier(cursor);
        this.clientName = MessageContract.getSender(cursor);
        this.clientID = MessageContract.getClientIdentifier(cursor);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            this.timestamp = sdf.parse(MessageContract.getTimeStamp(cursor));
        } catch (ParseException e) {
            Log.e("Time parse exception", e.getMessage());
        }

        this.chatroom = MessageContract.getChatroom(cursor);


    }

    public void writeToProvider(ContentValues values) {

        MessageContract.putMessageText(values, this.messageText);
        MessageContract.putMessageIdentifier(values, this.messageID);
        MessageContract.putSender(values, this.clientName);
        MessageContract.putClientIdentifier(values, this.clientID);


        String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this.timestamp);
        Log.v("time",dateStr);
        MessageContract.putTimeStamp(values, dateStr);

        MessageContract.putChatroom(values, this.chatroom);
    }

    String parseJson(JsonReader jr) throws IOException{

        String entityValue = "";
        jr.beginObject();
        while (jr.peek() != JsonToken.END_OBJECT) {
            String label = jr.nextName();
            if ("id".equals(label)) {
                entityValue = jr.nextLong() + "";
            }

        }
        jr.endObject();
        return entityValue;
    }


}
