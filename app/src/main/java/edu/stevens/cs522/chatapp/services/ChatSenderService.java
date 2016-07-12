package edu.stevens.cs522.chatapp.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by FEIFAN on 2016/3/6.
 */
public class ChatSenderService extends Service {

    public static final String TAG = "ChatSenderService";

    public static final int SEND_ACTION = 1;

    public static final String DESTINATION_HOST = "destination_host";
    public static final String DESTINATION_PORT = "destination_port";
    public static final String MESSAGE = "message";

    private ResultReceiver resultReceiver;

    private Messenger messenger;

    private HandlerThread messengerThread;

    private Looper messengerLooper;

    private class MessageHandler extends Handler{


        public MessageHandler(Looper looper){//handler constructor for work thread by passing looper
            super(looper);
        }

        public void handleMessage(Message message){
            Bundle data = message.getData();
           // ResultReceiver resultReceiver = data.getParcelable("");

            switch (message.what){

                case SEND_ACTION:
                    String destinationHost = (String)data.get(DESTINATION_HOST);
                    String destinationPort = (String)data.get(DESTINATION_PORT);
                    String messageSend = (String)data.get(MESSAGE);
                    sendMessageToClient(destinationHost, destinationPort, messageSend);

                    //result receiver
                    //resultReceiver.send(ChatMain.SEND_RESULT_CODE, null);//simply display toast without passing data
            }
        }
    }

    private MessageHandler handler;

    public MessageHandler getHandler(){
        return this.handler;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        messengerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        messengerThread.start();
        messengerLooper = messengerThread.getLooper();
        handler = new MessageHandler(messengerLooper);
        messenger = new Messenger(handler);
    }

    private final IChatSenderService binder = new IChatSenderService();

    @Override
    public IChatSenderService onBind(Intent intent) {

        //resultReceiver = intent.getParcelableExtra(ChatMain.TAG);

        return binder;
    }

    public class IChatSenderService extends Binder {

        public ChatSenderService getService() {
            return ChatSenderService.this;
        }

    }


    public void sendMessageToClient(String destinationHost, String destinationPort, String message){

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = spf.getString("clientName", "Unknown Client");

        try {
            /*
             * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

            DatagramSocket socket = new DatagramSocket();

            InetAddress destAddr = null;

            int destPort = 0;

            byte[] sendData = null;  // Combine sender and message text; default encoding is UTF-8

            // TODO get data from UI

            sendData = (name + "@" + message).getBytes();//add client name at the front
            destAddr = InetAddress.getByName(destinationHost);
            destPort = Integer.parseInt(destinationPort);
            // End todo

            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, destAddr, destPort);

            socket.send(sendPacket);

            Log.i(TAG, "Client: " + name + " Sent packet: " + message);

        } catch (UnknownHostException e) {
            Log.e(TAG, "Unknown host exception: ", e);
        } catch (IOException e) {
            Log.e(TAG, "IO exception: ", e);
        }
    }



}
