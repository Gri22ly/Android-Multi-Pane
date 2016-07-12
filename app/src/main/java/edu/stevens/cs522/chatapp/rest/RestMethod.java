package edu.stevens.cs522.chatapp.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by FEIFAN on 2016/3/13.
 */
public class RestMethod {

    private static HttpURLConnection connection;
    Context context;
    String url_str;
    URL url;

    public static OutputStream uploadConnection;
    public static InputStream downloadConnection;


    public RestMethod(Context context, String url) {

        this.context = context;
        this.url_str = url;

    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void outputRequestEntity(Request request) throws IOException {
        String requestEntity = request.getRequestEntity();
        if (requestEntity != null) {
            Log.v("Json to be sent", requestEntity);

            connection.setDoOutput(true);//using POST
            connection.setRequestProperty("CONTENT-TYPE", "application/json");

            byte[] outputEntity = requestEntity.getBytes("UTF-8");

            connection.setFixedLengthStreamingMode(outputEntity.length);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());//implicitly connect

            out.write(outputEntity);

            out.flush();
            out.close();
        }
    }

    public void throwErrors(HttpURLConnection connection) throws IOException {
        final int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            String exceptionMessage = "Error response " + status + " " + connection.getResponseMessage() +
                    " for " + connection.getURL();
            throw new IOException(exceptionMessage);
        }
    }

    public Response perform(Register register) throws IOException {

        if (isOnline(context)) {

            try {
                String regUrl = url_str + "?username=" + register.clientName + "&regid=" + register.registrationID + "";
                url = new URL(regUrl);
            } catch (MalformedURLException e) {
                Log.e(getClass().getCanonicalName(), e.getMessage());
            }
            connection = (HttpURLConnection) url.openConnection();
        } else {
            return null;
        }

        connection.setRequestProperty("USER_AGENT", null);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("CONNECTION", "Keep-Alive");

        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);

        Map<String, String> headers = register.getRequestHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }

        connection.setDoInput(true);
        connection.connect();
        throwErrors(connection);

        JsonReader rd = new JsonReader(new BufferedReader(new InputStreamReader(connection.getInputStream())));

        Response response = register.getResponse(connection, rd);
        rd.close();

        if (response.isValid()) {
            return response;
        } else {
            return null;
        }

    }

    public StreamingResponse perform(Synchronize synchronize, IStreamingOutput out) throws IOException {

        // streaming process is left to processor
        // don't use outputRequestEntity
        // since json object might be very big


        if (isOnline(context)) {

            try {
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
                String clientID = spf.getString("clientID", "");
                String appIdentifier = spf.getString("UUID", null);
                String mdgUrl = url_str + "/" + clientID + "?regid=" + appIdentifier + "&seqnum=" +
                        synchronize.lastSeqNum;//may use preference value here
                url = new URL(mdgUrl);
            } catch (MalformedURLException e) {
                Log.e(getClass().getCanonicalName(), e.getMessage());
            }
            connection = (HttpURLConnection) url.openConnection();
        } else {
            Log.e("Network error","Device not online");
            return null;
        }

        connection.setRequestProperty("USER_AGENT", null);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("CONNECTION", "Keep-Alive");

        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);

        Map<String, String> headers = synchronize.getRequestHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }

        connection.setDoOutput(true);
        connection.setRequestProperty("CONTENT-TYPE", "application/json");
        connection.setChunkedStreamingMode(0);
        connection.setDoInput(true);

        uploadConnection = connection.getOutputStream();
        out.write(uploadConnection);//call back to processor

        throwErrors(connection);

        downloadConnection = connection.getInputStream();

        return new StreamingResponse(synchronize.getResponse(connection, null));

    }


    public Response perform(PostMessage postMessage) throws IOException {

        if (isOnline(context)) {

            try {
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
                String clientID = spf.getString("clientID", "");
                String appIdentifier = spf.getString("UUID", "");
                String mdgUrl = url_str + "/" + clientID + "?regid=" + appIdentifier;//may use preference value here
                url = new URL(mdgUrl);
            } catch (MalformedURLException e) {
                Log.e(getClass().getCanonicalName(), e.getMessage());
            }
            connection = (HttpURLConnection) url.openConnection();
        }

        connection.setRequestProperty("USER_AGENT", null);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("CONNECTION", "Keep-Alive");

        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);

        Map<String, String> headers = postMessage.getRequestHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }

        connection.setDoInput(true);
        outputRequestEntity(postMessage);
        throwErrors(connection);

        JsonReader rd = new JsonReader(new BufferedReader(new InputStreamReader(connection.getInputStream())));


        Response response = postMessage.getResponse(connection, rd);
        rd.close();

        if (response.isValid()) {
            return response;
        } else {
            return null;
        }

    }

    public static void closeConnection() throws IOException{

        uploadConnection.close();
        downloadConnection.close();
    }
}
