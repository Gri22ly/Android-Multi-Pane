package edu.stevens.cs522.chatapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;

import edu.stevens.cs522.chatapp.R;
import edu.stevens.cs522.chatapp.contracts.ChatRoomContract;
import edu.stevens.cs522.chatapp.entities.ChatRoom;
import edu.stevens.cs522.chatapp.managers.ChatRoomManager;
import edu.stevens.cs522.chatapp.managers.IEntityCreator;
import edu.stevens.cs522.chatapp.managers.PostMessageManager;
import edu.stevens.cs522.chatapp.rest.PostMessage;

/**
 * Created by FEIFAN on 2016/4/8.
 */
public class PostMessageDialog extends DialogFragment {

    public static final int SPINNER_ROOM_LOADER = 3;
    public static final int SPINNER_MESSAGE_LOADER = 4;

    private ChatRoomManager crm;
    private SimpleCursorAdapter sca;

    private PostMessageManager pmm;

    private Spinner roomField;

    private EditText userField;

    private EditText messageField;

    private Button confirm;

    private Button cancel;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            mListener = (PostMessageDialogListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement NoticeDialogListener");
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crm = new ChatRoomManager(getActivity(), new IEntityCreator<ChatRoom>() {
            @Override
            public ChatRoom create(Cursor cursor) {
                return new ChatRoom(cursor);
            }
        }, SPINNER_ROOM_LOADER);

        pmm = new PostMessageManager(getActivity(), new IEntityCreator<PostMessage>() {
            @Override
            public PostMessage create(Cursor cursor) {
                return new PostMessage(cursor);
            }
        }, SPINNER_MESSAGE_LOADER);

        sca = new SimpleCursorAdapter(getActivity(), R.layout.spinner_chatroom_row, null, new String[]{ChatRoomContract.NAME},
                new int[]{R.id.room_name}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        crm.getAllAsyn(sca);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.dialog_postmessage, null);

        roomField = (Spinner) rootView.findViewById(R.id.roomname_choose);

        userField = (EditText) rootView.findViewById(R.id.username_input);

        messageField = (EditText) rootView.findViewById(R.id.postmessage_input);

        confirm = (Button) rootView.findViewById(R.id.postmessage_confirm);

        cancel = (Button) rootView.findViewById(R.id.postmessage_cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(roomField.getCount() == 0){
                      Toast toast = Toast.makeText(getActivity(),
                              "Currently no Chat Room, please create one!", Toast.LENGTH_LONG);
                      toast.setGravity(Gravity.CENTER, 0, 0);
                      toast.show();
                  }else if("".equals(userField.getText().toString())){
                      Toast toast = Toast.makeText(getActivity(),
                              "Please input a user name", Toast.LENGTH_LONG);
                      toast.setGravity(Gravity.CENTER, 0, 0);
                      toast.show();
                  }else{
                      PostMessage message = new PostMessage();

                      //not interact with web service this time
                      message.messageID = 0;
                      message.clientID = 0;
                      //consider this later

                      Cursor cursor = (Cursor)roomField.getSelectedItem();
                      message.chatroom = ChatRoomContract.getName(cursor);
                      message.clientName = userField.getText().toString().trim();

                      Date date = new Date();
                      message.timestamp = date;

                      message.messageText = messageField.getText().toString();

                      pmm.persistAsyn(message);


                      PostMessageDialog.this.getDialog().dismiss();
                  }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostMessageDialog.this.getDialog().cancel();
            }
        });

        roomField.setAdapter(sca);

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

}
