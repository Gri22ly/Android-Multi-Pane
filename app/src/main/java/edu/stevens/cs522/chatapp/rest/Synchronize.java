package edu.stevens.cs522.chatapp.rest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
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
public class Synchronize extends Request {

    public long id;
    public String messageText;
    public Date timestamp;
    public long messageID;

    public String lastSeqNum;

    public Synchronize(Map<String, String> requestHeaders, Uri requestUri, String requestEntity) {

        super(requestHeaders, requestUri, requestEntity);

    }


    public Synchronize() {

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


        Response response = new Response();

        response.responseCode = connection.getResponseCode();
        response.responseMessage = connection.getResponseMessage();

        return response;
    }


    public static final Creator<Synchronize> CREATOR = new Creator<Synchronize>() {
        @Override
        public Synchronize createFromParcel(Parcel in) {
            return new Synchronize(in);
        }

        @Override
        public Synchronize[] newArray(int size) {
            return new Synchronize[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(messageText);

    }

    public Synchronize(Parcel in) {

        messageText = in.readString();

    }

    public Synchronize(Cursor cursor) {

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


    }

    public void writeToProvider(ContentValues values) {

        MessageContract.putMessageText(values, this.messageText);
        MessageContract.putMessageIdentifier(values, this.messageID);
        MessageContract.putSender(values, this.clientName);
        MessageContract.putClientIdentifier(values, this.clientID);


        String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this.timestamp);
        MessageContract.putTimeStamp(values, dateStr);

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
