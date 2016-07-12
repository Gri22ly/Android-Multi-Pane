package edu.stevens.cs522.chatapp.rest;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by FEIFAN on 2016/3/13.
 */
public class Register extends Request {

    public Register(Map<String, String> requestHeaders, Uri requestUri, String requestEntity) {

        super(requestHeaders, requestUri, requestEntity);

    }

    public Register() {
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

        Response response = new Response(code, message, parseJson(rd));

        return response;
    }

    public static final Creator<Register> CREATOR = new Creator<Register>() {
        @Override
        public Register createFromParcel(Parcel in) {
            return new Register(in);
        }

        @Override
        public Register[] newArray(int size) {
            return new Register[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {

    }

    public Register(Parcel in) {

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
