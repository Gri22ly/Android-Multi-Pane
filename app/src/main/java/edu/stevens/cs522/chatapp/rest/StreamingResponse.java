package edu.stevens.cs522.chatapp.rest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by FEIFAN on 2016/3/22.
 */
public class StreamingResponse {

    public Response response;

    public StreamingResponse(Response response) {

        this.response = response;

    }


    public Response getResponse() {

        return response;

    }

    public InputStream getInputStream() {

        return RestMethod.downloadConnection;

    }

    public void disconnect() throws IOException {

        RestMethod.closeConnection();

    }
}
