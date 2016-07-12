package edu.stevens.cs522.chatapp.rest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chatapp.managers.IEntityCreator;
import edu.stevens.cs522.chatapp.managers.PostMessageManager;

/**
 * Created by FEIFAN on 2016/3/13.
 */
public class RequestProcessor {

    private static final int LOADER_ID = 2;

    public static final int REGISTER_RESULT = 1;
    public static final int POSTMESSAGE_RESULT = 2;
    public static final int SYNCHRONIZE_RESULT = 3;


    public Context context;
    public RestMethod restMethod;

    PostMessageManager pmm;

    public RequestProcessor(Context context) {

        this.context = context;

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        String pref_url = spf.getString("URL", "");

        this.restMethod = new RestMethod(context, pref_url);

        pmm = new PostMessageManager(context, new IEntityCreator<PostMessage>() {
            @Override
            public PostMessage create(Cursor cursor) {
                return null;
            }
        }, LOADER_ID);

    }

    public void perform(Register register, IRequestProcessorCallback callback) {

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        String appIdentifier = spf.getString("UUID", null);

        if (appIdentifier == null) { // if app not registered yet
            String pref_name = spf.getString("clientName", null);
            UUID uuid = UUID.randomUUID(); //generate a new UUID
            SharedPreferences.Editor editor = spf.edit();
            editor.putString("UUID", uuid.toString());
            editor.commit();

            //set uuid into request for registration
            register.registrationID = uuid;
            register.clientName = pref_name;

            Map<String, String> headers = new HashMap<>();
            headers.put("X-latitude", "40.7439905");
            headers.put("X-longitude", "-74.0323626");

            register.requestHeaders = headers;

            try {
                Response rr = restMethod.perform(register);
                if(rr == null){
                    return;
                }
                Log.v("Response code", rr.responseCode + "");
                Log.v("Response message", rr.responseMessage);
                Log.v("Response object", rr.responseEntity);

                editor.putString("clientID", rr.responseEntity);//save register client ID
                editor.commit();
            } catch (IOException e) {
                Log.e(this.getClass().getCanonicalName(), e.getMessage());
            }
            //callback to service
            callback.send(REGISTER_RESULT, null);
        }

    }

    public void perform(final Synchronize synchronize, IRequestProcessorCallback callback) {

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        String appIdentifier = spf.getString("UUID", null);


        if (appIdentifier == null) {
            Log.e("Registration error", "Current user not registered yet");
        } else {
            String clientID = spf.getString("clientID", "0");

            //query non-synchronized items
            final List<PostMessage> nonSyncList = pmm.fetchUnupdatedMsgs(clientID);

            if (nonSyncList.size() == 0) { //no non-sync messages
                return;
            }

            //get last sequence number first
            String lastSeqNum = spf.getString("seqNum", "0");
            synchronize.lastSeqNum = lastSeqNum;

            //get current user name
            String pref_name = spf.getString("clientName", "Unknown Client");
            synchronize.clientName = pref_name;

            //get current user id
            synchronize.clientID = Long.parseLong(clientID);

            //set headers
            Map<String, String> headers = new HashMap<>();
            headers.put("X-latitude", "40.7439905");
            headers.put("X-longitude", "-74.0323626");

            synchronize.requestHeaders = headers;

            //set request entity using the non-sync list
            try {
                synchronize.requestEntity = writeJsonStream(nonSyncList);
            } catch (IOException e) {
                Log.e(this.getClass().getCanonicalName(), e.getMessage());
            }

            /**
             * Streaming processed by processor here
             */

            //callback for streaming to server
            IStreamingOutput out = new IStreamingOutput() {
                public void write(OutputStream os) {

                    try {

                        //Write streaming upload data
                        byte[] outputEntity = synchronize.requestEntity.getBytes("UTF-8");
                        os.write(outputEntity);
                        os.flush();
                        //Don’t close! Don’t disconnect!

                    } catch (IOException e) {
                        Log.e(this.getClass().getCanonicalName(), e.getMessage());
                    }
                }
            };


            JsonReader jr;
            List<PostMessage> messageList = new ArrayList<>();
            try {

                StreamingResponse sr = restMethod.perform(synchronize, out);

                if(sr == null){//network problems
                    return;
                }

                InputStream is = sr.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                /**
                 * server return test
                 */
//                String temp;
//                String s = "";
//
//                while((temp = br.readLine()) != null)
//                {
//                    s = s + temp;
//                }
//
//                Log.v("test",s);

                jr = new JsonReader(br);

                messageList = parseJson(jr);

                jr.close();

                //disconnect here
                sr.disconnect();

            } catch (IOException e) {
                Log.e(this.getClass().getCanonicalName(), e.getMessage());
            }

            /**
             * Business logic for sync in database
             */

            //persist all messages sync from server
            //obtain the seqnum
            long seqNum = 0;
            for (PostMessage item : messageList) {
                if (item.messageID > seqNum) {
                    seqNum = item.messageID;
                    Log.v("sequence number", item.messageID + "");
                }
                pmm.persist(item);
            }
            Log.v("current sequence number", seqNum + "");
            //persist the last seqnum
            SharedPreferences.Editor editor = spf.edit();
            editor.putString("seqNum", seqNum + "");
            editor.commit();

            //delete all non-sync messages
            pmm.deleteUnupdatedMsgs();
            //callback to service
            if(callback != null) {
                callback.send(SYNCHRONIZE_RESULT, null);
            } else{
                Intent autoSync = new Intent("SYNC_ALARM");
                context.sendBroadcast(autoSync);
            }
        }

    }

    public void perform(PostMessage postMessage, IRequestProcessorCallback callback) {

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        String appIdentifier = spf.getString("UUID", null);


        if (appIdentifier == null) {
            Log.e("Registration error", "Not registration yet");
        } else {

            String pref_name = spf.getString("clientName", "Unknown Client");
            String clientID = spf.getString("clientID", "0");

            postMessage.registrationID = UUID.fromString(appIdentifier); //use UUID in preference
            postMessage.clientName = pref_name;
            postMessage.clientID = Integer.parseInt(clientID);
            postMessage.timestamp = new Date();

            /**
             * Don't send messages to server here, use sync for this assignment
             */
//            Map<String, String> headers = new HashMap<>();
//            headers.put("X-latitude", "40.7439905");
//            headers.put("X-longitude", "-74.0323626");
//
//            postMessage.requestHeaders = headers;


//            try {// network works fine
//                postMessage.requestEntity = writeJsonStream(postMessage);
//                Response rr = restMethod.perform(postMessage);
//                Log.v("Response code", rr.responseCode + "");
//                Log.v("Response message", rr.responseMessage);
//                Log.v("Response object", rr.responseEntity);
//                postMessage.messageID = Long.parseLong(rr.responseEntity);
//
//            } catch (IOException e) { //network has problem
//                Log.e(this.getClass().getCanonicalName(), e.getMessage());
//            }

            //set messageID with 0 indicating the message is not synchronized
            postMessage.messageID = 0;

            //persist message using synchronous method in back ground thread
            pmm.persist(postMessage);

            //callback to service
            callback.send(POSTMESSAGE_RESULT, null);
        }

    }


    private String writeJsonStream(PostMessage message) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonWriter jw = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writeMessage(jw, message);
        jw.close();
        byte[] bytes = out.toByteArray();
        return new String(bytes);
    }

    private void writeMessage(JsonWriter jw, PostMessage message) throws IOException {
        jw.beginObject();
        jw.name("chatroom").value("_default");
        jw.name("timestamp").value(message.timestamp.getTime());
        jw.name("text").value(message.messageText);
        jw.endObject();
    }

    //write json objects for synchronizing data items

    private String writeJsonStream(List<PostMessage> messageList) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonWriter jw = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writeMessage(jw, messageList);
        jw.close();
        byte[] bytes = out.toByteArray();
        return new String(bytes);
    }

    private void writeMessage(JsonWriter jw, List<PostMessage> messageList) throws IOException {
        jw.beginArray();
        for (PostMessage item : messageList) {
            jw.beginObject();
            jw.name("chatroom").value("_default");
            jw.name("timestamp").value(item.timestamp.getTime());
            jw.name("text").value(item.messageText);
            jw.endObject();
        }
        jw.endArray();
    }

    private List<PostMessage> parseJson(JsonReader jr) throws IOException {

        List<PostMessage> messageList = new ArrayList<>();

        jr.beginObject();
        if ("clients".equals(jr.nextName())) {//skip clients information
            jr.beginArray();
            while (jr.peek() != JsonToken.END_ARRAY) {
                jr.skipValue();
            }
            jr.endArray();
        }

        //parse messages
        if ("messages".equals(jr.nextName())) {
            jr.beginArray();
            while (jr.peek() != JsonToken.END_ARRAY) {

                jr.beginObject();
                PostMessage msgItem = new PostMessage();
                while (jr.peek() != JsonToken.END_OBJECT) {
                    String label = jr.nextName();

                    if ("timestamp".equals(label)) {
                        msgItem.timestamp = new Date(jr.nextLong());
                    } else if ("seqnum".equals(label)) {
                        msgItem.messageID = Long.parseLong(jr.nextString());
                    } else if ("sender".equals(label)) {
                        msgItem.clientName = jr.nextString();
                    } else if ("text".equals(label)) {
                        msgItem.messageText = jr.nextString();
                    } else {
                        jr.skipValue();
                    }
                }
                jr.endObject();
                messageList.add(msgItem);
            }
            jr.endArray();
        }
        jr.endObject();

        return messageList;
    }
}