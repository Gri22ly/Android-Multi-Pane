package edu.stevens.cs522.chatapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.stevens.cs522.chatapp.entities.Message;

/**
 * Created by FEIFAN on 2016/3/6.
 */
public class ChatReceiverService extends Service {

    private ResultReceiver resultReceiver;

    @Override
    public IBinder onBind(Intent intent) {

       // resultReceiver = intent.getParcelableExtra(ChatMain.TAG);

        return null;
    }

    public static final String TAG = "ChatReceiverService";

    public static final String NEW_MESSAGE_BROADCAST = "edu.stevens.cs522.chat.NewMessageBroadcast";

    private DatagramSocket serverSocket;
    private boolean socketOK;
    private MyAsyncTask task;

    private Intent msgUpdateBroadcast;

    @Override
    public void onCreate() {

        try {
            int port = Integer.parseInt("6666");
            serverSocket = new DatagramSocket(port);
        } catch (Exception e) {
            Log.e(TAG, "Cannot open socket" + e.getMessage());
        }

        socketOK = true;

        task = new MyAsyncTask();

        msgUpdateBroadcast = new Intent(NEW_MESSAGE_BROADCAST);

    }

    @Override
    public int onStartCommand(Intent request, int flags, int startId) {

        task.execute();

        return START_STICKY;
    }

    //define an asyncTask
    private class MyAsyncTask extends AsyncTask<Void, Message, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            Log.v("Start", "task!");
            while (socketOK) {
                Message message = null;
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);// blocking operation
                    Log.i(TAG, "Received a packet");

                    InetAddress sourceIPAddress = receivePacket.getAddress();
                    Log.i(TAG, "Source IP Address: " + sourceIPAddress);

			/*
             * TODO: Extract sender and receiver from message and display.
			 */
                    String receiveMessage = new String(receivePacket.getData());

                    String peerName = receiveMessage.substring(0, receiveMessage.indexOf("@"));
                    String messageContent = receiveMessage.substring(receiveMessage.indexOf("@") + 1, receiveMessage.length());

                    message = new Message(0, messageContent, peerName, 0);

                    //result receiver
                    //resultReceiver.send(ChatMain.RECEIVE_RESULT_CODE,null);//simply display toast without passing data

                    //broadcast
                    msgUpdateBroadcast.putExtra(TAG,message);
                    sendBroadcast(msgUpdateBroadcast);
			/*
             * End Todo
			 */
                    Log.v("test", message.messageText);
                } catch (Exception e) {

                    Log.e(TAG, "Problems receiving packet: " + e.getMessage());
                    socketOK = false;
                    serverSocket.close();
                }
            }

            return null;
        }

    }


}
