/*********************************************************************
 * Chat server: accept chat messages from clients.
 * <p/>
 * Sender name and GPS coordinates are encoded
 * in the messages, and stripped off upon receipt.
 * <p/>
 * Copyright (c) 2012 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chatapp.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.chatapp.R;
import edu.stevens.cs522.chatapp.contracts.MessageContract;
import edu.stevens.cs522.chatapp.dialogs.ChatRoomDialog;
import edu.stevens.cs522.chatapp.dialogs.NotifyDialog;
import edu.stevens.cs522.chatapp.dialogs.PostMessageDialog;
import edu.stevens.cs522.chatapp.entities.ChatRoom;
import edu.stevens.cs522.chatapp.fragments.MessagePane;
import edu.stevens.cs522.chatapp.fragments.NavigationPane;
import edu.stevens.cs522.chatapp.managers.ChatRoomManager;
import edu.stevens.cs522.chatapp.managers.IContinue;
import edu.stevens.cs522.chatapp.managers.IEntityCreator;
import edu.stevens.cs522.chatapp.managers.PostMessageManager;
import edu.stevens.cs522.chatapp.rest.PostMessage;
import edu.stevens.cs522.chatapp.services.ServiceHelper;

public class ChatMain extends Activity implements ChatRoomDialog.ChatRoomDialogListener {

    final static public String TAG = ChatMain.class.getCanonicalName();

    public static final int MESSAGE_LOADER_ID = 1;

    /**
     * here for IDs simply use integer
     * generated UUID might be better
     */

    //Service ID
    public static final String SERVICE_IDENTIFIER = "service_id";
    public static final int WEB_SERVICE = 1;

    private PostMessageManager pmm;

    private BroadcastReceiver receiver;
    private ServiceHelper serviceHelper;

	/*
     * TODO: Declare UI.
	 */

    private ListView messageList;

    private EditText messageText;
    private Button sendButton;

	/*
     * End Todo
	 */

    private SimpleCursorAdapter sca;

    /*
     * AlarmManager
     *
     *
     */

    private AlarmManager am;
    private BroadcastReceiver alarmEventReceiver;

    /*
     * Called when the activity is first created.
     */

    //landscape
    private ChatRoomManager crm;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//       if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//           setContentView(R.layout.main);
//
//           pmm = new PostMessageManager(this, new IEntityCreator<PostMessage>() {
//               @Override
//               public PostMessage create(Cursor cursor) {
//                   return new PostMessage(cursor);
//               }
//           }, MESSAGE_LOADER_ID);
//
//           //for test use, init app identifier in preference and delete history messages
//           SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
//           SharedPreferences.Editor editor = spf.edit();
//           editor.putString("UUID", null);
//           editor.putString("seqNum", null);
//           editor.commit();
//           pmm.deleteAll();
//           //delete when coming into product
//
//           sca = new SimpleCursorAdapter(getApplicationContext(),
//                   R.layout.message_row, null,
//                   new String[]{MessageContract.SENDER, MessageContract.TIMESTAMP, MessageContract.MESSAGETEXT},
//                   new int[]{R.id.message_row_sender, R.id.message_row_timestamp, R.id.message_row_text}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//
//           messageList = (ListView) findViewById(R.id.msgList);
//           messageList.setAdapter(sca);
//
//           messageText = (EditText) findViewById(R.id.message_text);
//           sendButton = (Button) findViewById(R.id.send_button);
//
//           //init service helper
//           serviceHelper = ServiceHelper.getInstance(this);
//
//           sendButton.setOnClickListener(new View.OnClickListener() {
//               @Override
//               public void onClick(View v) {
//
//
//                   String messageText_value = messageText.getText().toString();
//
//
//                   if (serviceHelper != null) {
//
//                       Bundle data = new Bundle();
//                       data.putString("DATA_FROM_ACTIVITY", messageText_value);
//                       data.putInt(SERVICE_IDENTIFIER, WEB_SERVICE);
//                       serviceHelper.initiateService(data);
//
//                   }
//
//                   messageText.setText("");
//               }
//           });
//
//           //get all data asynchronously and swap with a cursor from cursor loader
//           //cursor will swap after content provide update
//           // UI listview will update automatically
//
//           pmm.getAllAsyn(sca);
//       }
//        else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
        setContentView(R.layout.main);

        crm = new ChatRoomManager(this, new IEntityCreator<ChatRoom>() {
            @Override
            public ChatRoom create(Cursor cursor) {
                return new ChatRoom(cursor);
            }
        }, NavigationPane.NAVI_LOADER);

        pmm = new PostMessageManager(this, new IEntityCreator<PostMessage>() {
            @Override
            public PostMessage create(Cursor cursor) {
                return new PostMessage(cursor);
            }
        }, MESSAGE_LOADER_ID);

//        pmm.deleteAllAsyn();//test
//        crm.deleteAllAsyn();//test

//       }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        //init broadcast receiver and register
//        IntentFilter filter = new IntentFilter(ServiceHelper.TAG);
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                int resultCode = intent.getIntExtra(ServiceHelper.RESULT_CODE, -1);
//
//                if (resultCode == RequestProcessor.REGISTER_RESULT) {
//                    //do nothing
//                    Log.v(ChatMain.class.getSimpleName(), "Register APP!");
//                } else if (resultCode == RequestProcessor.POSTMESSAGE_RESULT) {
//                    //do nothing
//                    Log.v(ChatMain.class.getSimpleName(), "Message sent!");
//                } else if (resultCode == RequestProcessor.SYNCHRONIZE_RESULT) {
//                    //do nothing
//                    Log.v(ChatMain.class.getSimpleName(), "Synchronized!");
//                }
//
//            }
//        };
//        registerReceiver(receiver, filter);
//
//        //set alarm for periodically synchronize
//
//        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, RequestService.class);
//        intent.putExtra("SERVICE_MODE", "sync");
//        PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5*1000, pi); // every 5 secs
//
//        IntentFilter filter2 = new IntentFilter("SYNC_ALARM");
//        alarmEventReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast toast = Toast.makeText(getApplicationContext(),
//                        "Synchronized with server!", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            }
//        };
//
//        registerReceiver(alarmEventReceiver, filter2);
//
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        unregisterReceiver(receiver);
//        unregisterReceiver(alarmEventReceiver);
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatmain_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.add_chatroom:

                showChatRoomDialog();

                return true;

//            case R.id.name_setting:
//                intent = new Intent(this, Setting.class);
//                startActivity(intent);
//                return true;

//            case R.id.show_client:
//                intent = new Intent(this, ClientList.class);
//
//                Cursor cursor = sca.getCursor();
//
//                if (cursor.getCount() == 0) {
//
//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            "Currently no clients!", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                    return true;
//                } else {
//
//                    Set<String> noDupSet = new HashSet();
//                    if (cursor.moveToFirst()) {
//
//                        do {
//                            String clientName = MessageContract.getSender(cursor);
//                            noDupSet.add(clientName);
//                        }
//                        while (cursor.moveToNext());
//
//                    }
//
//                    String[] currentClientList = new String[noDupSet.size()];
//                    int i = 0;
//                    for (String client : noDupSet) {
//                        currentClientList[i] = client;
//                        i++;
//                    }
//
//                    intent.putExtra("clientList", currentClientList);
//                    startActivity(intent);
//                    return true;
//                }
            case R.id.post_a_message:
                showPostMessageDialog();

        }

        return false;
    }

    private void showChatRoomDialog() {

        ChatRoomDialog dialog = new ChatRoomDialog();
        dialog.show(getFragmentManager(), "ChatRoomDialog");

    }

    private void showNotifyDialog(){
        NotifyDialog dialog = new NotifyDialog();
        dialog.show(getFragmentManager(), "NotifyDialog");
    }

    private void showPostMessageDialog(){
        PostMessageDialog dialog = new PostMessageDialog();
        dialog.show(getFragmentManager(), "PostMessageDialog");
    }


    @Override
    public void onCreateRoomClick(final String name) {

        IContinue checkListener = new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor cursor) {

                if (cursor.getCount() == 0) {
                    ChatRoom chatRoom = new ChatRoom(0, name);
                    crm.persistAsyn(chatRoom);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Create chat room: " + name, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {

//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            "Chat room: " + name + " already existed", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                    showNotifyDialog();
                    //pop up another dialog
                }
            }
        };

        if (crm != null) {
            crm.isRoomNameExistedAsyn(name, checkListener);
        }
    }

}