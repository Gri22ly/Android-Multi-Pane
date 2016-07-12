package edu.stevens.cs522.chatapp.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatapp.R;
import edu.stevens.cs522.chatapp.contracts.MessageContract;
import edu.stevens.cs522.chatapp.managers.ChatRoomManager;
import edu.stevens.cs522.chatapp.managers.IEntityCreator;
import edu.stevens.cs522.chatapp.managers.MessageManager;
import edu.stevens.cs522.chatapp.managers.PostMessageManager;
import edu.stevens.cs522.chatapp.rest.PostMessage;

/**
 * Created by FEIFAN on 2016/4/3.
 */
public class MessagePane extends Fragment {


    public static final int MESSAGE_LOADER = 2;
    private PostMessageManager pmm;
    private ListView msgList;
    private SimpleCursorAdapter sca;
    private String roomName;
    private TextView titleBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pmm = new PostMessageManager(getActivity(), new IEntityCreator<PostMessage>() {
            @Override
            public PostMessage create(Cursor cursor) {
                return new PostMessage(cursor);
            }
        }, MESSAGE_LOADER);

        sca = new SimpleCursorAdapter(getActivity(),
                   R.layout.message_row, null,
                   new String[]{MessageContract.SENDER, MessageContract.TIMESTAMP, MessageContract.MESSAGETEXT},
                   new int[]{R.id.message_row_sender, R.id.message_row_timestamp, R.id.message_row_text}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        Bundle data = getArguments();
        roomName = data.getString("ROOM_NAME");

        pmm.getMessagesByRoomAsyn(sca, roomName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.message_pane, container, false);

        msgList = (ListView) rootView.findViewById(R.id.msgList_per_room);
        msgList.setEmptyView(rootView.findViewById(R.id.message_empty_view));

        titleBar = (TextView) rootView.findViewById(R.id.roomTitle);
        titleBar.setText("Chat Room: " + roomName);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        msgList.setAdapter(sca);

    }

}
