package edu.stevens.cs522.chatapp.rest;

import android.net.Uri;
import android.os.Parcelable;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

/**
 * Created by FEIFAN on 2016/3/13.
*/
public abstract class Request implements Parcelable {

    public long clientID;

    //obtain from preference
    public String clientName;
    public UUID registrationID; // sanity check

    public Map<String, String> requestHeaders;

    public Uri requestUri;

    public String requestEntity;

    public Request(Map<String, String> requestHeaders, Uri requestUri, String requestEntity ){

        this.requestHeaders = requestHeaders;
        this.requestUri = requestUri;
        this.requestEntity = requestEntity;

    }

    public Request(){

    }

    // App-specific HTTP request headers.
    public Map<String, String> getRequestHeaders() {

        if (requestHeaders != null) {
            return this.requestHeaders;
        }else {
            return null;
        }

    }

    // Chat service URI with parameters e.g. query string parameters.
    public Uri getRequestUri() {

        if (requestUri != null) {
            return this.requestUri;
        }else {
            return null;
        }

    }

    // JSON body (if not null) for request data not passed in headers.
    public String getRequestEntity(){

        if (requestEntity != null) {
            return this.requestEntity;
        }else {
            return null;
        }

    }

    // Define your own Response class, including HTTP response code.
    public abstract Response getResponse(HttpURLConnection connection, JsonReader rd /* Null for streaming */) throws IOException;

    @Override
    public int describeContents() {
        return 0;
    }

}
