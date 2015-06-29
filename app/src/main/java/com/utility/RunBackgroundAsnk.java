package com.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.interfaces.BackgroundTaskInterface;

import org.json.JSONObject;

/**
 * Created by Rahul on 5/6/2015.
 */
public class RunBackgroundAsnk extends AsyncTask<String, String, String> {

    private String serviceUrl, jsonInput;
    private JsonWebServicePost servicePost;
    private JSONObject jsonObject;
    private Context context;
    public BackgroundTaskInterface taskInterface;

    public RunBackgroundAsnk(Context context){
        servicePost = new JsonWebServicePost();
        this.context = context;
    }

    @Override
    protected String doInBackground(String... args) {

        serviceUrl = args[0];
        jsonInput = args[1];
        jsonObject = servicePost.getJsonObj(serviceUrl, jsonInput);
        return jsonObject.toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        taskInterface.onStarted();
    }

    @Override
    protected void onPostExecute(String jsonStr) {

        super.onPostExecute(jsonStr);
        taskInterface.onCompleted(jsonStr);
    }
}
