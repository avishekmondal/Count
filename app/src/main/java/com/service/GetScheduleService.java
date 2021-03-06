package com.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.interfaces.BackgroundTaskInterface;
import com.utility.Constant;
import com.utility.NetworkConnectionCheck;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by BLUEHORSE DEVLOPER on 6/20/2015.
 */
public class GetScheduleService extends Service implements BackgroundTaskInterface {
    private RunBackgroundAsnk backgroundAsnk;
    private JSONObject jsonObject1, jsonObject2;
    private static final String TAG_DATA = "data";
    private static final String TAG_ACCESSTOKEN = "accesstoken";
    private static final String TAG_DEVICEID = "deviceId";
    private NetworkConnectionCheck connectionCheck;
    private Pref _pref;

    public GetScheduleService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        try {
            jsonObject2.put(TAG_ACCESSTOKEN, _pref.getAccessToken());
            jsonObject2.put(TAG_DEVICEID, _pref.getDeviceId());
            jsonObject1.put(TAG_DATA, jsonObject2);

        } catch (Exception e){
            e.printStackTrace();
        }

        backgroundAsnk.taskInterface = GetScheduleService.this;
        backgroundAsnk.execute(Constant.baseUrl + "getCountSchedule", jsonObject1.toString());
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

         if (connectionCheck.isNetworkAvailable()) {
        // getScheduledCount();
//------------------------------------------------------------------------------------------------
             try{
                 String fileDIr = Environment.getExternalStorageDirectory()
                         + File.separator + getPackageName();
                 File f = new File(fileDIr);
                 if (!f.exists()) {
                     f.mkdirs();
                 }


                 String filename = f.getAbsolutePath() + File.separator
                         + Constant.JSON_SCH_CATEGORY_FILE_NAME;
                 File jsonF = new File(filename);

                 _pref.setSession(Constant.JSON_SCH_CATEGORY_FILE_NAME, filename);

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
             //-----------------------------------------------------------------------------------

           } else {
             //connectionCheck.getNetworkActiveAlert().show();
             Toast.makeText(getBaseContext(),"Network not Available! Working on offline",Toast.LENGTH_LONG).show();
          }

        Toast.makeText(getBaseContext(),"Please Continue..",Toast.LENGTH_LONG).show();   //This for Check... Have to fix.
stopSelf();


    }



    private void initialize(){
        backgroundAsnk = new RunBackgroundAsnk(getApplicationContext());
        _pref = new Pref(getApplicationContext());
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        connectionCheck = new NetworkConnectionCheck(GetScheduleService.this);
    }
}
