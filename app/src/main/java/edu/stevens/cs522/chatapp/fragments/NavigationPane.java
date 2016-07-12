package edu.stevens.cs522.chatapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatapp.R;
import edu.stevens.cs522.chatapp.activities.MessagesByRoom;
import edu.stevens.cs522.chatapp.contracts.ChatRoomContract;
import edu.stevens.cs522.chatapp.entities.ChatRoom;
import edu.stevens.cs522.chatapp.managers.ChatRoomManager;
import edu.stevens.cs522.chatapp.managers.IEntityCreator;

public class NavigationPane extends Fragment {

    public static final int NAVI_LOADER = 1;
    private ChatRoomManager crm;
    private ListView crList;
    private SimpleCursorAdapter sca;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crm = new ChatRoomManager(getActivity(), new IEntityCreator<ChatRoom>() {
            @Override
            public ChatRoom create(Cursor cursor) {
                return new ChatRoom(cursor);
            }
        }, NAVI_LOADER);

        sca = new SimpleCursorAdapter(getActivity(), R.layout.chatroom_row, null, new String[]{ChatRoomContract.NAME},
                new int[]{R.id.room_name}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        crm.getAllAsyn(sca);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.navigation, container, false);

        crList = (ListView) rootView.findViewById(R.id.roomList);

        crList.setEmptyView(rootView.findViewById(R.id.room_empty_view));

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        crList.setAdapter(sca);

        crList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) sca.getItem(position);
                String roomName = ChatRoomContract.getName(cursor);

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    Log.v("call","call");
                    showChatRoomDetails(roomName);

                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

                    Intent intent = new Intent(getActivity(), MessagesByRoom.class);

                    intent.putExtra("ROOM_NAME", roomName);

                    startActivity(intent);
                }

            }
        });

    }

    private void showChatRoomDetails(String roomName) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        MessagePane messagePane = new MessagePane();
        Bundle data = new Bundle();
        data.putString("ROOM_NAME", roomName);
        messagePane.setArguments(data);
        fragmentTransaction.replace(R.id.fragment_container, messagePane);
        if (fragmentManager.getBackStackEntryCount() != 0) {
        fragmentManager.popBackStack();
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
