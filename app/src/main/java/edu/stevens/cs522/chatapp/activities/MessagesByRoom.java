package edu.stevens.cs522.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatapp.R;
import edu.stevens.cs522.chatapp.contracts.MessageContract;
import edu.stevens.cs522.chatapp.managers.IEntityCreator;
import edu.stevens.cs522.chatapp.managers.PostMessageManager;
import edu.stevens.cs522.chatapp.rest.PostMessage;

/**
 * Created by FEIFAN on 2016/4/9.
 */
public class MessagesByRoom extends Activity {


    public static final int MESSAGE_LOADER = 2;
    private PostMessageManager pmm;
    private ListView msgList;
    private SimpleCursorAdapter sca;
    private String roomName;
    private TextView titleBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("ROOM_NAME");

        setContentView(R.layout.message_pane);

        msgList = (ListView)findViewById(R.id.msgList_per_room);
        msgList.setEmptyView(findViewById(R.id.message_empty_view));

        titleBar = (TextView)findViewById(R.id.roomTitle);
        titleBar.setText("Chat Room: " + roomName);

        pmm = new PostMessageManager(this, new IEntityCreator<PostMessage>() {
            @Override
            public PostMessage create(Cursor cursor) {
                return new PostMessage(cursor);
            }
        }, MESSAGE_LOADER);

        sca = new SimpleCursorAdapter(this,
                R.layout.message_row, null,
                new String[]{MessageContract.SENDER, MessageContract.TIMESTAMP, MessageContract.MESSAGETEXT},
                new int[]{R.id.message_row_sender, R.id.message_row_timestamp, R.id.message_row_text}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        msgList.setAdapter(sca);
        pmm.getMessagesByRoomAsyn(sca, roomName);

    }


    //use old layout for back in menu, too lazy to write a new one
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.message_by_peer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){

            case R.id.messageByPeer_back:
                finish();
                return true;

        }
        return false;
    }

}
