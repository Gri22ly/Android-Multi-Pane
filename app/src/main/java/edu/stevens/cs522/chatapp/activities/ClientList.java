package edu.stevens.cs522.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.stevens.cs522.chatapp.R;

/**
 * Created by 凡 on 2016/2/14.
 */
public class ClientList extends Activity {

    //
//    private ChatDbAdapter dbAdapter;
//    private Cursor cursor;
//    private SimpleCursorAdapter sca;
    private ListView clientList;
    private ArrayAdapter aa;

    private static final int ITEM1 = Menu.FIRST;

    public static final String CLIENT_RESULT_KEY = "client_selected";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientlist);

        Intent intent = getIntent();


        String[] clients = intent.getStringArrayExtra("clientList");

        Log.v("!!!!",clients[0]);

        aa = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, clients);
        clientList = (ListView) findViewById(R.id.clientList);
        clientList.setAdapter(aa);

        registerForContextMenu(clientList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clientlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.clientList_back:
                finish();
                return true;

        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("What's Next?");
        menu.add(0, ITEM1, 0, "Show client's message list.");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String peerName = (String) clientList.getItemAtPosition(menuInfo.position);
        switch (item.getItemId()) {
            case ITEM1:
                Intent intent = new Intent(this, MessagesByClient.class);
                intent.putExtra(CLIENT_RESULT_KEY, peerName);
                startActivity(intent);
                break;

        }
        return true;
    }
}
