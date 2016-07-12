package edu.stevens.cs522.chatapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import edu.stevens.cs522.chatapp.rest.IRequestProcessorCallback;
import edu.stevens.cs522.chatapp.rest.PostMessage;
import edu.stevens.cs522.chatapp.rest.Register;
import edu.stevens.cs522.chatapp.rest.RequestProcessor;
import edu.stevens.cs522.chatapp.rest.Synchronize;

/**
 * Created by FEIFAN on 2016/3/13.
 */
public class RequestService extends IntentService {

    private ResultReceiver receiver;

    public RequestService() {
        super(RequestService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String mode = intent.getStringExtra("SERVICE_MODE");
        if(mode.equals("send")) {
            receiver = intent.getParcelableExtra(ServiceHelper.WEB_SERVICE_CALLBACK);

            String message = intent.getStringExtra("DATA_FROM_ACTIVITY");
            Register register = new Register();
            PostMessage postMessage = new PostMessage();
            Synchronize synchronize = new Synchronize();
            postMessage.messageText = message;

            RequestProcessor requestProcessor = new RequestProcessor(getApplicationContext());

            requestProcessor.perform(register, processorCallback);
            requestProcessor.perform(postMessage, processorCallback);
            requestProcessor.perform(synchronize, processorCallback);
        }else if(mode.equals("sync")){

            RequestProcessor requestProcessor = new RequestProcessor(getApplicationContext());
            Synchronize synchronize = new Synchronize();
            requestProcessor.perform(synchronize, null);//directly broadcast to main_land activity in processor


        }


    }

    //processor callback
    private IRequestProcessorCallback processorCallback= new IRequestProcessorCallback() {
        @Override
        public void send(int resultcode, Bundle data) {

            // send back to service helper
            if(receiver!= null){
                receiver.send(resultcode, data);
            }
        }
    };


}
