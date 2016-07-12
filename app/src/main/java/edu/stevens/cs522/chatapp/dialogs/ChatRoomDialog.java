package edu.stevens.cs522.chatapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import edu.stevens.cs522.chatapp.R;

/**
 * Created by FEIFAN on 2016/4/6.
 */
public class ChatRoomDialog extends DialogFragment {

    public interface ChatRoomDialogListener {
         void onCreateRoomClick(String roomName);
    }

    ChatRoomDialogListener mListener;

    private EditText nameField;

    private Button confirm;

    private Button cancel;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ChatRoomDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_chatroom, null);

        nameField = (EditText)rootView.findViewById(R.id.roomname_input);

        confirm = (Button)rootView.findViewById(R.id.roomname_input_confirm);

        cancel = (Button)rootView.findViewById(R.id.roomname_input_cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = nameField.getText().toString().trim();
                mListener.onCreateRoomClick(roomName);
                ChatRoomDialog.this.getDialog().dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoomDialog.this.getDialog().cancel();
            }
        });
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
}
