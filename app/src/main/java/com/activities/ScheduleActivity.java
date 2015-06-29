package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.SurveyListAdapter;
import com.bean.SurveyItem;
import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.utility.Constant;
import com.utility.NetworkConnectionCheck;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;
import com.utility.ThemeSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends ActionBarActivity implements BackgroundTaskInterface {

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack;

    private LinearLayout llScheduleMainLayout;
    private ProgressWheel pwSchedulerPage;
    private TextView tvNoScheduleScan, tvScheduleDate;
    private RunBackgroundAsnk backgroundAsnk;
    private JSONObject jsonObj1, jsonObj2;
    private ListView lvSurveyList;
    String jsonProductListStr;
    private static final String TAG_DATA = "data";
    private static final String TAG_ACCESSTOKEN = "accesstoken";
    private static final String TAG_DEVICEID = "deviceId";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_SCHEDULEDETAILS = "scheduleDetails";
    private static final String TAG_SURVEYNAME = "surveyName";
    private static final String TAG_SURVEYID = "surveyId";
    private static final String TAG_SURVEYTYPE = "surveyType";
    private static final String TAG_SURVEYDATE = "date";
    private static final String TAG_SURVEYTIME = "time";
    private static final String TAG_SURVEYSTATUS = "status";
    private static final String TAG_SURVEYINS = "instruction";
    private static final String TAG_ERRNODE = "errNode";
    private static final String TAG_ERRCODE = "errCode";
    private static final String TAG_ERRMSG = "errMsg";

    private ArrayList<SurveyItem> surveyItems;
    private SurveyListAdapter listAdapter;

    private NetworkConnectionCheck connectionCheck;
    private Pref _pref;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private Date todaysDate;

    private Animation moveFromUp;
    private Object scheduleC;

    //private String jsonStrDemo = "{\"data\":{\"scheduleDetails\":[{\"surveyName\":\"XYZ Servey\", \"surveyId\":\"1\", \"surveyType\": \"full\", \"status\": \"notdone\", \"date\": \"12-05-2015\", \"time\": \"14HRS\", \"instruction\": \"xxccvvbdnmg\"},{\"surveyName\":\"ABC Servey\", \"surveyId\":\"2\", \"surveyType\": \"daily\", \"status\": \"notdone\", \"date\": \"12-05-2015\", \"time\": \"14HRS\", \"instruction\": \"xxccvvbdnmg\"},{\"surveyName\":\"MNO Servey\", \"surveyId\":\"3\", \"surveyType\": \"full\", \"status\": \"notdone\", \"date\": \"12-05-2015\", \"time\": \"14HRS\", \"instruction\": \"xxccvvbdnmg\"},{\"surveyName\":\"XYZ Servey\", \"surveyId\":\"4\", \"surveyType\": \"full\", \"status\": \"done\", \"date\": \"12-05-2015\", \"time\": \"14HRS\", \"instruction\": \"xxccvvbdnmg\"},{\"surveyName\":\"SSS Servey\", \"surveyId\":\"5\", \"surveyType\": \"daily\", \"status\": \"notdone\", \"date\": \"12-05-2015\", \"time\": \"14HRS\", \"instruction\": \"xxccvvbdnmg\"},,{\"surveyName\":\"SSS Servey\", \"surveyId\":\"5\", \"surveyType\": \"daily\", \"status\": \"notdone\", \"date\": \"12-05-2015\", \"time\": \"14HRS\", \"instruction\": \"xxccvvbdnmg\"}],\"schedule\":true},\"errNode\":{\"errCode\":\"0\",\"errMsg\":\"\"}}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "COUNT", R.drawable.previous_icon);
        _themeSetter.setBodyColor(llScheduleMainLayout);

        todaysDate = calendar.getTime();
        tvScheduleDate.setText(dateFormat.format(todaysDate));
        lvSurveyList.setDivider(null);
        lvSurveyList.setAdapter(listAdapter);

        onClick();

    }

    protected void onResume() {
        super.onResume();
       // if (connectionCheck.isNetworkAvailable()) {
        //    getScheduledCount();



      //  } else {
       //     connectionCheck.getNetworkActiveAlert().show();
        getScheduleC();
      //  }
    }

    @Override
    protected void onPause() {
        super.onPause();
        surveyItems.clear();
     //   tvNoScheduleScan.setVisibility(View.GONE);
        listAdapter.notifyDataSetChanged();
    }

    private void initialize(){

        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(ScheduleActivity.this);
        _pref = new Pref(ScheduleActivity.this);
        connectionCheck = new NetworkConnectionCheck(ScheduleActivity.this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        llScheduleMainLayout = (LinearLayout) findViewById(R.id.llScheduleMainLayout);
        pwSchedulerPage = (ProgressWheel) findViewById(R.id.pwSchedulerPage);
        tvNoScheduleScan = (TextView) findViewById(R.id.tvNoScheduleScan);
        tvScheduleDate = (TextView) findViewById(R.id.tvScheduleDate);
        lvSurveyList = (ListView) findViewById(R.id.lvSurveyList);

        surveyItems = new ArrayList<SurveyItem>();
        listAdapter = new SurveyListAdapter(ScheduleActivity.this, surveyItems);

        jsonObj1 = new JSONObject();
        jsonObj2 = new JSONObject();

        moveFromUp = AnimationUtils.loadAnimation(ScheduleActivity.this, R.anim.move_down);
    }

    private void onClick(){

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        Intent intent = new Intent(ScheduleActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getScheduledCount(){

        try {

            jsonObj2.put(TAG_ACCESSTOKEN, _pref.getAccessToken());
            jsonObj2.put(TAG_DEVICEID, _pref.getDeviceId());
            jsonObj1.put(TAG_DATA, jsonObj2);

            backgroundAsnk = new RunBackgroundAsnk(ScheduleActivity.this);
            backgroundAsnk.taskInterface = ScheduleActivity.this;
            backgroundAsnk.execute(Constant.baseUrl + "getCountSchedule", jsonObj1.toString());

        } catch (JSONException e){
            e.printStackTrace();
        }

    }



    @Override
    public void onStarted() {

        pwSchedulerPage.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCompleted(String jsonStr) {

      //  pwSchedulerPage.setVisibility(View.GONE);

     //   try {
     //       JSONObject jsonAll = new JSONObject(jsonStr);
            //JSONObject jsonAll = new JSONObject(jsonStrDemo);
       //     JSONObject jsonError = jsonAll.getJSONObject(TAG_ERRNODE);

    //        if(jsonError.getString(TAG_ERRCODE).equals("0")){

      //          JSONObject jsonData = jsonAll.getJSONObject(TAG_DATA);

     //           if(jsonData.getBoolean(TAG_SCHEDULE)){

        //            JSONArray jsonScheduleDetails = jsonData.getJSONArray(TAG_SCHEDULEDETAILS);
       //             lvSurveyList.setVisibility(View.VISIBLE);

       //             for(int i=0; i<jsonScheduleDetails.length(); i++){

          //              final SurveyItem _item = new SurveyItem();
          //              JSONObject jsonObject = jsonScheduleDetails.getJSONObject(i);
         //               _item.setSurveyName(jsonObject.getString(TAG_SURVEYNAME));
          //              _item.setSurveyId(jsonObject.getString(TAG_SURVEYID));
         //               _item.setSurveyType(jsonObject.getString(TAG_SURVEYTYPE));
         //               _item.setSurveyDate(jsonObject.getString(TAG_SURVEYDATE));
          //              _item.setSurveyTime(jsonObject.getString(TAG_SURVEYTIME));
         //               _item.setSurveyStatus(jsonObject.getString(TAG_SURVEYSTATUS));
         //               _item.setSurveyInstruction(jsonObject.getString(TAG_SURVEYINS));

          //              surveyItems.add(_item);

              //      }

              //      listAdapter.notifyDataSetChanged();

         //       } else {

           //         tvNoScheduleScan.setVisibility(View.VISIBLE);

          //      }
          //  } else {

         //       tvNoScheduleScan.setVisibility(View.VISIBLE);
        //        tvNoScheduleScan.setText(jsonError.getString(TAG_ERRMSG));

       //     }

    //    }catch (Exception e){
    //        e.printStackTrace();
     //   }

        //-----------------------------------------------------------------------------------------
        //-----------------------------------------------------------------------------------------

    }


    public void getScheduleC() {

        try {
            jsonProductListStr = getStringFromFile(
                    _pref.getSession(Constant.JSON_SCH_CATEGORY_FILE_NAME));
            JSONObject jsonAll = new JSONObject(jsonProductListStr);
            Log.d("Tag --> ",jsonAll.toString());
            JSONObject jsonError = jsonAll.getJSONObject(TAG_ERRNODE);

            if(jsonError.getString(TAG_ERRCODE).equals("0")){

                JSONObject jsonData = jsonAll.getJSONObject(TAG_DATA);

                if(jsonData.getBoolean(TAG_SCHEDULE)){

                    JSONArray jsonScheduleDetails = jsonData.getJSONArray(TAG_SCHEDULEDETAILS);
                    lvSurveyList.setVisibility(View.VISIBLE);

                    for(int i=0; i<jsonScheduleDetails.length(); i++){

                        final SurveyItem _item = new SurveyItem();
                        JSONObject jsonObject = jsonScheduleDetails.getJSONObject(i);
                        _item.setSurveyName(jsonObject.getString(TAG_SURVEYNAME));
                        _item.setSurveyId(jsonObject.getString(TAG_SURVEYID));
                        _item.setSurveyType(jsonObject.getString(TAG_SURVEYTYPE));
                        _item.setSurveyDate(jsonObject.getString(TAG_SURVEYDATE));
                        _item.setSurveyTime(jsonObject.getString(TAG_SURVEYTIME));
                        _item.setSurveyStatus(jsonObject.getString(TAG_SURVEYSTATUS));
                        _item.setSurveyInstruction(jsonObject.getString(TAG_SURVEYINS));

                        surveyItems.add(_item);

                    }

                    listAdapter.notifyDataSetChanged();

                    pwSchedulerPage.setVisibility(View.GONE);
                } else {

                    tvNoScheduleScan.setVisibility(View.VISIBLE);

                }
            } else {

                tvNoScheduleScan.setVisibility(View.VISIBLE);
                tvNoScheduleScan.setText(jsonError.getString(TAG_ERRMSG));

            }

        }catch (Exception e){
            e.printStackTrace();
        }

      //  return scheduleC;
    }

    public String convertStreamToString(InputStream is) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();

        return sb.toString();

    }

    public String getStringFromFile(String filePath) throws Exception {

        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        // Make sure you close all streams.
        fin.close();

        return ret;

    }
}
