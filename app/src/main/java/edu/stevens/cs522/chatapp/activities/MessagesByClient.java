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

import edu.stevens.cs522.chatapp.R;
import edu.stevens.cs522.chatapp.contracts.MessageContract;
import edu.stevens.cs522.chatapp.managers.IEntityCreator;
import edu.stevens.cs522.chatapp.managers.PostMessageManager;
import edu.stevens.cs522.chatapp.rest.PostMessage;

/**
 * Created by 凡 on 2016/2/14.
 */
public class MessagesByClient extends Activity {

    private PostMessageManager pmm;
    private SimpleCursorAdapter sca;
    private ListView messageByClientList;

    private static final int MESSAGE_BY_CLIENT_LOADER_ID = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String clientName  = intent.getStringExtra(ClientList.CLIENT_RESULT_KEY);

        setContentView(R.layout.messages_by_client);

        getActionBar().setTitle(clientName + "'s Messages");

        pmm = new PostMessageManager(this, new IEntityCreator<PostMessage>() {
            @Override
            public PostMessage create(Cursor cursor) {
                return new PostMessage(cursor);
            }
        }, MESSAGE_BY_CLIENT_LOADER_ID);

        sca = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.message_row, null,
                new String[]{MessageContract.SENDER,MessageContract.TIMESTAMP, MessageContract.MESSAGETEXT},
                new int[]{R.id.message_row_sender,R.id.message_row_timestamp, R.id.message_row_text}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        messageByClientList = (ListView)findViewById(R.id.messageByClient);
        messageByClientList.setAdapter(sca);

        pmm.getMessagesByNameAsyn(sca, clientName);

    }

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
