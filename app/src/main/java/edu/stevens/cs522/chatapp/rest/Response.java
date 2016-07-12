package edu.stevens.cs522.chatapp.rest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FEIFAN on 2016/3/13.
 */
public class Response implements Parcelable {

    public int responseCode;

    public String responseMessage;

    public String responseEntity;

    public Response(int responseCode, String responseMessage, String responseEntity){

        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseEntity = responseEntity;

    }

    public Response(){

    }

    public boolean isValid() {

        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeInt(responseCode);
        out.writeString(responseMessage);
        out.writeString(responseEntity);

    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    public Response(Parcel in) {

        this.responseCode = in.readInt();
        this.responseMessage = in.readString();
        this.responseEntity = in.readString();

    }


}
