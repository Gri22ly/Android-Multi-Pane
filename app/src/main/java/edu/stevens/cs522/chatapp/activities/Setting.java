package edu.stevens.cs522.chatapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.chatapp.R;

/**
 * Created by 凡 on 2016/2/14.
 */
public class Setting extends PreferenceActivity{


    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);



    }

    @Override
    protected void onResume(){

        super.onResume();

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);

        currentUserName = spf.getString("clientName", "Unknown Client");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){

            case R.id.setting_back:
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
                String name = spf.getString("clientName", "Unknown Client");
                if(!currentUserName.equals(name)) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Current Client's Name: " + name, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    //clear UUID
                    SharedPreferences.Editor editor = spf.edit();
                    editor.putString("UUID", null);
                    editor.commit();
                }
                finish();
                return true;

        }
        return false;
    }


}
