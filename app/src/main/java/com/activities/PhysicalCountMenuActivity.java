package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.PhysicalCountMenuAdapter;
import com.bean.PhysicalCountMenuItem;
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

import java.util.ArrayList;


public class PhysicalCountMenuActivity extends ActionBarActivity implements BackgroundTaskInterface {

    private ActionBar actionBar;
    private ImageView ivBack;
    private ThemeSetter _themeSetter;

    private LinearLayout llScheduleMainLayout;
    private TextView tvNoScheduleScan;
    private ProgressWheel pwSchedulerPage;
    ListView listCategory;
    ArrayList<PhysicalCountMenuItem> catItems;
    PhysicalCountMenuAdapter adapter;

    private NetworkConnectionCheck connectionCheck;
    Pref _pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_count_menu);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "PHYSICAL COUNT", R.drawable.previous_icon);
        _themeSetter.setBodyColor(llScheduleMainLayout);

        onclick();

    }

    protected void onResume() {
        super.onResume();
        catItems = new ArrayList<PhysicalCountMenuItem>();
        listCategory.setVisibility(View.GONE);
        if (connectionCheck.isNetworkAvailable()) {
            loadPhysicalCountMenu();
        } else {
            connectionCheck.getNetworkActiveAlert().show();
        }


    }

    private void initialize(){

        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(PhysicalCountMenuActivity.this);
        _pref = new Pref(PhysicalCountMenuActivity.this);
        connectionCheck = new NetworkConnectionCheck(PhysicalCountMenuActivity.this);

        llScheduleMainLayout = (LinearLayout) findViewById(R.id.llScheduleMainLayout);
        pwSchedulerPage = (ProgressWheel) findViewById(R.id.pwSchedulerPage);
        tvNoScheduleScan = (TextView) findViewById(R.id.tvNoScheduleScan);
        listCategory = (ListView) findViewById(R.id.listCategory);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void onclick(){

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent gotoAddCount = new Intent(PhysicalCountMenuActivity.this, PhysicalCountAddActivity.class);
                gotoAddCount.putExtra("catId", catItems.get(position).getcatId());
                gotoAddCount.putExtra("catType", catItems.get(position).getcatType());
                startActivity(gotoAddCount);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


    }

    private void loadPhysicalCountMenu(){

        String url = Constant.baseUrl + "getCountCategory";

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accesstoken", _pref.getAccessToken());
            jsonObject.put("deviceId", _pref.getDeviceId());

            JSONObject data = new JSONObject();
            data.put("data", jsonObject);

            String jsonInput = data.toString();

            RunBackgroundAsnk menu = new RunBackgroundAsnk(
                    PhysicalCountMenuActivity.this);
            menu.taskInterface = PhysicalCountMenuActivity.this;
            menu.execute(url, jsonInput);
        }

        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_physical_count_menu, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStarted() {

        pwSchedulerPage.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCompleted(String jsonStr) {

        pwSchedulerPage.setVisibility(View.GONE);

        if (jsonStr != null) {

            try {

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject errNodeObj = jsonObj.getJSONObject("errNode");
                String errCode = errNodeObj.getString("errCode");
                String errMsg = errNodeObj.getString("errMsg");

                if(errCode.equalsIgnoreCase("0")){

                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    String success = dataObj.getString("success");

                    if(success.equalsIgnoreCase("true")){

                        JSONArray categoryDetailsArray = dataObj.getJSONArray("categoryDetails");

                        for(int i = 0; i < categoryDetailsArray.length(); i++){

                            PhysicalCountMenuItem item = new PhysicalCountMenuItem();
                            JSONObject c = categoryDetailsArray.getJSONObject(i);

                            item.setcatId(c.getString("catId"));
                            item.setcatType(c.getString("catType"));
                            item.setcatImg(c.getString("catImg"));

                            catItems.add(item);

                        }

                        adapter = new PhysicalCountMenuAdapter(PhysicalCountMenuActivity.this, catItems);
                        listCategory.setAdapter(adapter);

                        listCategory.setVisibility(View.VISIBLE);


                    }
                    else{

                        tvNoScheduleScan.setVisibility(View.VISIBLE);

                    }
                }

                else{

                    Toast.makeText(PhysicalCountMenuActivity.this, errMsg, Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else{

            Toast.makeText(PhysicalCountMenuActivity.this, "Something going wrong!! Please Try Again", Toast.LENGTH_LONG).show();

        }

    }
}
