package com.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.utility.Constant;
import com.utility.NetworkConnectionCheck;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SplashActivity extends Activity implements BackgroundTaskInterface{

    private Pref _pref;
    private NetworkConnectionCheck connectionCheck;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        initialize();

        Date currentDate = calendar.getTime();
        Constant.CURRENT_DATE = dateFormat.format(currentDate);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        _pref.saveDeviecId(deviceId);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
    }

    private void initialize(){

        _pref = new Pref(SplashActivity.this);
        connectionCheck = new NetworkConnectionCheck(SplashActivity.this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    }

    private void checkVersion(){

        if (connectionCheck.isNetworkAvailable()) {

            PackageManager manager = this.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
                String packageName = info.packageName;
                int versionCode = info.versionCode;
                String versionName = info.versionName;

                String url = Constant.baseUrl + "versionCheck";

                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("version", versionName);

                    JSONObject data = new JSONObject();
                    data.put("data", jsonObject);

                    String jsonInput = data.toString();

                    RunBackgroundAsnk splash = new RunBackgroundAsnk(
                            SplashActivity.this);
                    splash.taskInterface = SplashActivity.this;
                    splash.execute(url, jsonInput);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
            }
        } else {

           // connectionCheck.getNetworkActiveAlert().show();
            Toast.makeText(getBaseContext(),"Working on Offline",Toast.LENGTH_LONG).show();



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this,
                            HomeActivity.class));

                }
            }, 5000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(String jsonStr) {


        if (jsonStr != null) {

            try {

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject errNodeObj = jsonObj.getJSONObject("errNode");
                String errCode = errNodeObj.getString("errCode");
                String errMsg = errNodeObj.getString("errMsg");

                if (errCode.equalsIgnoreCase("0")) {

                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    String verified = dataObj.getString("verified");
                    String active = dataObj.getString("active");

                    if (verified.equalsIgnoreCase("true") && active.equalsIgnoreCase("true")) {

                        if (_pref.getAccessToken().equals("")) {

                            startActivity(new Intent(SplashActivity.this,
                                    LoginActivity.class));
                            finish();
                        } else {

                            startActivity(new Intent(SplashActivity.this,
                                    HomeActivity.class));
                            finish();
                        }

                    } else {

                        Toast.makeText(SplashActivity.this, "Something going wrong!!" +
                                " Please Try Again", Toast.LENGTH_LONG).show();

                    }
                } else {

                    Toast.makeText(SplashActivity.this, errMsg, Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        else{

            Toast.makeText(SplashActivity.this, "Something going wrong!!" +
                    " Please Try Again", Toast.LENGTH_LONG).show();

        }

    }

}
