package com.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.interfaces.BackgroundTaskInterface;
import com.utility.Constant;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by Rahul on 5/14/2015.
 */
public class GetCategoryService extends Service implements BackgroundTaskInterface{

    private RunBackgroundAsnk backgroundAsnk;
    private JSONObject jsonObject1, jsonObject2;
    private static final String TAG_DATA = "data";
    private static final String TAG_ACCESSTOKEN = "accesstoken";
    private static final String TAG_DEVICEID = "deviceId";
    private Pref _pref;

    public GetCategoryService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        try {
            jsonObject2.put(TAG_ACCESSTOKEN, _pref.getAccessToken());
            jsonObject2.put(TAG_DEVICEID, _pref.getSession("deviceid"));
            jsonObject1.put(TAG_DATA, jsonObject2);

        } catch (Exception e){
            e.printStackTrace();
        }

        backgroundAsnk.taskInterface = GetCategoryService.this;
        backgroundAsnk.execute(Constant.baseUrl + "getProductCategory", jsonObject1.toString());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(String jsonStr) {
        try{
            String fileDIr = Environment.getExternalStorageDirectory()
                    + File.separator + getPackageName();
            File f = new File(fileDIr);
            if (!f.exists()) {
                f.mkdirs();
            }


            String filename = f.getAbsolutePath() + File.separator
                    + Constant.JSON_CATEGORY_FILE_NAME;
            File jsonF = new File(filename);

            _pref.setSession(Constant.JSON_CATEGORY_FILE_NAME, filename);

            if (jsonF.exists()) {
                jsonF.createNewFile();
            }

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(jsonF.getAbsolutePath());
                fos.write(jsonStr.getBytes());
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initialize(){
        backgroundAsnk = new RunBackgroundAsnk(getApplicationContext());
        _pref = new Pref(getApplicationContext());
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
    }
}
