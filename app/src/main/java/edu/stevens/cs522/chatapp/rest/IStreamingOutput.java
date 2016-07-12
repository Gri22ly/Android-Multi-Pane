package edu.stevens.cs522.chatapp.rest;

import java.io.OutputStream;

/**
 * Created by FEIFAN on 2016/3/22.
 */
public interface IStreamingOutput {
    void write(OutputStream os);
}
