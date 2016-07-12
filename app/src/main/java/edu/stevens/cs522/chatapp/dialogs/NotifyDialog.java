package edu.stevens.cs522.chatapp.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import edu.stevens.cs522.chatapp.R;

/**
 * Created by FEIFAN on 2016/4/8.
 */
public class NotifyDialog extends DialogFragment {

    Button confirm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_notify, null);

        confirm = (Button) rootView.findViewById(R.id.nameDup_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotifyDialog.this.getDialog().dismiss();
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
