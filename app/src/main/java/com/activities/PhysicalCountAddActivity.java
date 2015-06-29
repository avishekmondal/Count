package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adapter.PhysicalCountMenuAdapter;
import com.bean.PhysicalCountMenuItem;
import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.utility.Constant;
import com.utility.DbAdapter;
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
import java.util.ArrayList;


public class PhysicalCountAddActivity extends ActionBarActivity implements BackgroundTaskInterface {

    private NetworkConnectionCheck connectionCheck;
    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack;
    private AutoCompleteTextView autoComplete;
    private PhysicalCountMenuAdapter adapter;
    private static final String TAG_ERRNODE = "errNode";
    private static final String TAG_ERRCODE = "errCode";
    private static final String TAG_ERRMSG = "errMsg";
    private static final String TAG_DATA = "data";
    private static final String TAG_CATEGORYDETAILS = "categoryDetails";
    private static final String TAG_CATID = "catId";
   // private static final String TAG_PRODUCTNAME = "productName";
    ArrayList<PhysicalCountMenuItem> catItems;
    ArrayList<String> Names;
    ArrayAdapter<String> arrayAdapterNames;

    EditText etRackNo, etNoOfItems;
    LinearLayout llAddCount;


    String catId="", catType = "", rackNo = "", noOfItems = "";

    DbAdapter db;
    long id;

    private Pref _pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_count_add);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "PHYSICAL COUNT", R.drawable.previous_icon);
        _themeSetter.setButtonColor(llAddCount);

        onclick();

      //  catId = getIntent().getStringExtra("catId");
      //  catType = getIntent().getStringExtra("catType");

        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int x = Names.indexOf(autoComplete.getText().toString());

                catId = catItems.get(x).getcatId();
                catType = catItems.get(x).getcatType();

                Log.d("ID:--> ", catId);
                Log.d("ITEMS:--> ", catType);
            }
        });

        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

              catType = autoComplete.getText().toString();

            }
        });



    }

    private void loadProductCategory() {

        try {
            String jsonProductListStr = getStringFromFile(
                    _pref.getSession(Constant.JSON_PHY_CATEGORY_FILE_NAME));
            JSONObject jsonProductList = new JSONObject(jsonProductListStr);
            JSONObject jsonError = jsonProductList.getJSONObject(TAG_ERRNODE);

            if(jsonError.getString(TAG_ERRCODE).equals("0")) {

                JSONObject jsonData = jsonProductList.getJSONObject(TAG_DATA);
                JSONArray categoryDetailsArray = jsonData.getJSONArray(TAG_CATEGORYDETAILS);

                for(int i=0; i< categoryDetailsArray.length(); i++){

                    PhysicalCountMenuItem item = new PhysicalCountMenuItem();
                    JSONObject c = categoryDetailsArray.getJSONObject(i);

                    item.setcatId(c.getString("catId"));
                    item.setcatType(c.getString("catType"));
                    item.setcatImg(c.getString("catImg"));

                    catItems.add(item);
                    Names.add(catItems.get(i).getcatType().toString());
                }

                arrayAdapterNames = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                        Names);
              //  avtCategory.setThreshold(1);
              //  avtCategory.setAdapter(strProductAdapter);
                autoComplete.setAdapter(arrayAdapterNames);
                Log.d("tag --->",Names.toString());

            } else {
                Toast.makeText(PhysicalCountAddActivity.this, jsonError.getString(TAG_ERRMSG),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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


    protected void onResume() {
        super.onResume();
       // catItems = new ArrayList<PhysicalCountMenuItem>();
       // if (connectionCheck.isNetworkAvailable()) {
         //   loadPhysicalCountMenu();
        loadProductCategory();


      //  } else {
        //    connectionCheck.getNetworkActiveAlert().show();
      //  }


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
                    PhysicalCountAddActivity.this);
            menu.taskInterface = PhysicalCountAddActivity.this;
            menu.execute(url, jsonInput);
        }

        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void initialize(){

        actionBar = getSupportActionBar();
        _pref = new Pref(PhysicalCountAddActivity.this);
        _themeSetter = new ThemeSetter(PhysicalCountAddActivity.this);

        etRackNo = (EditText) findViewById(R.id.etRackNo);
        etNoOfItems = (EditText) findViewById(R.id.etNoOfItems);
        llAddCount = (LinearLayout) findViewById(R.id.llAddCount);
        autoComplete = (AutoCompleteTextView)findViewById(R.id.autoComplete);
        catItems = new ArrayList<PhysicalCountMenuItem>();
        Names = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getBaseContext(),ScheduleActivity.class));
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


        llAddCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rackNo = etRackNo.getText().toString();
                String noOfItems = etNoOfItems.getText().toString();

                if (noOfItems.equalsIgnoreCase("")) {

                    Toast.makeText(PhysicalCountAddActivity.this, "Please input number of items", Toast.LENGTH_LONG).show();
                } else {

                    db = new DbAdapter(PhysicalCountAddActivity.this);
                    db.open();

                    db.insertValuePhysicalCount(catId, catType, rackNo, Integer.parseInt(noOfItems));
                    _pref.setSession("complete_full_physical_" + _pref.getSession("current_surveyid"), "1");

                    db.close();

                    startActivity(new Intent(PhysicalCountAddActivity.this, PhysicalCountResultActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                }

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_physical_stock_add, menu);
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

        autoComplete.setEnabled(false);
    }

    @Override
    public void onCompleted(String jsonStr) {
        autoComplete.setEnabled(true);
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
                          Names.add(catItems.get(i).getcatType().toString());

                        }

                        arrayAdapterNames = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Names);
                        adapter = new PhysicalCountMenuAdapter(PhysicalCountAddActivity.this, catItems);
                        autoComplete.setAdapter(arrayAdapterNames);
                        Log.d("tag --->",Names.toString());   //For Debug Purpose..

                    }
                    else{

                       //No Data Available...

                    }
                }

                else{

                    Toast.makeText(PhysicalCountAddActivity.this, errMsg, Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else{

            Toast.makeText(PhysicalCountAddActivity.this, "Something going wrong!! Please Try Again", Toast.LENGTH_LONG).show();

        }

    }


}
