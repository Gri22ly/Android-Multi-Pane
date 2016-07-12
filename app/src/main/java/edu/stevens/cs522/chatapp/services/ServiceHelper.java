package edu.stevens.cs522.chatapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import edu.stevens.cs522.chatapp.activities.ChatMain;

/**
 * Created by FEIFAN on 2016/3/14.
 */
public class ServiceHelper {

    public static final String RESULT_DATA = "result_data";

    public static final String WEB_SERVICE_CALLBACK = "web_service_callback";

    public static final String TAG = ServiceHelper.class.getCanonicalName();

    private Intent resultBroadcast;

    private Context context;

    public static final String RESULT_CODE = "result_code";

    //singleton
    private static ServiceHelper instance;

    private ServiceHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ServiceHelper getInstance(Context context) {

        if (instance == null) {

            instance = new ServiceHelper(context);

        }
        return instance;
    }

    public void initiateService(Bundle data) {

        int serviceID = data.getInt(ChatMain.SERVICE_IDENTIFIER, -1);

        switch (serviceID) {

            case ChatMain.WEB_SERVICE:

                String msg = data.getString("DATA_FROM_ACTIVITY");
                Intent intent = new Intent(this.context, RequestService.class);

                //call back for service
                ResultReceiver serviceCallback = new ResultReceiver(null) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        handleServiceResponse(resultCode, resultData);
                    }
                };
                intent.putExtra(WEB_SERVICE_CALLBACK, serviceCallback);
                intent.putExtra("DATA_FROM_ACTIVITY", msg);
                intent.putExtra("SERVICE_MODE","send");
                this.context.startService(intent);

            default: //handle exception here, not implemented yet
                break;
        }

    }


    private void handleServiceResponse(int resultCode, Bundle resultData) {

        //send broadcast back to activity
        resultBroadcast = new Intent(TAG);
        resultBroadcast.putExtra(ChatMain.SERVICE_IDENTIFIER, ChatMain.WEB_SERVICE);
        resultBroadcast.putExtra(RESULT_CODE, resultCode);
        resultBroadcast.putExtra(RESULT_DATA, resultData);

        context.sendBroadcast(resultBroadcast);
    }

}
