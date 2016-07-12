package edu.stevens.cs522.chatapp.rest;

import android.os.Bundle;

/**
 * Created by FEIFAN on 2016/3/14.
 */
public interface IRequestProcessorCallback {

    void send(int resultCode, Bundle data);
}
