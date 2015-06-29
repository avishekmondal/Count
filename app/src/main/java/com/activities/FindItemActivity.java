package com.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.FindItemAdapter;
import com.bean.FindItem;
import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.utility.Constant;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;
import com.utility.ThemeSetter;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindItemActivity extends ActionBarActivity implements BackgroundTaskInterface {

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack;
    private ProgressWheel progress_wheel;
    private RunBackgroundAsnk backgroundAsnk;
    private LinearLayout llFindAllLayout;
    private ListView lvFindItem;
    private ArrayList<FindItem> findItems;
    private TextView tvFindNoItem;
    private FindItemAdapter itemAdapter;
    private JSONObject jsonObj1, jsonObj2;
    private static final String TAG_DATA = "data";
    private static final String TAG_ACCESSTOKEN = "accesstoken";
    private static final String TAG_DEVICEID = "deviceId";
    private static final String TAG_BARCODE = "barcode";
    private static final String TAG_ARTICLECODE = "articleCode";
    private static final String TAG_CATEGORYCODE = "categoryCode";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_ITEMDETAILS = "itemDetails";
    private static final String TAG_TOTALCOUNT = "totalCount";
    private static final String TAG_ERRNODE = "errNode";
    private static final String TAG_ERRCODE = "errCode";
    private static final String TAG_ERRMSG = "errMsg";
    private Pref _pref;

    private String jsonStrDemo = "{\"data\":{\"success\" : \"true\", \"details\":[{\"articleCode\":\"12345\", \"category\":\"t-shirt\", \"barcode\":\"09876533\", \"itemDetails\":\"XL, black\", \"totalCount\":\"50\"}, {\"articleCode\":\"122222\",\"category\":\"t-shirt\", \"barcode\":\"09877733\", \"itemDetails\":\"XXL, blue\", \"totalCount\":\"10\"}, {\"articleCode\":\"123445\",\"category\":\"shirt\", \"barcode\":\"09888533\", \"itemDetails\":\"L, white\", \"totalCount\":\"20\"}, {\"articleCode\":\"12345\", \"category\":\"t-shirt\", \"barcode\":\"09876533\", \"itemDetails\":\"L, green, t-shirt\", \"totalCount\":\"50\"}, {\"articleCode\":\"12345\", \"category\":\"shirt\", \"barcode\":\"09876533\", \"itemDetails\":\"XL, black, shirt, xxxx, yyyyy, gggg\", \"totalCount\":\"50\"}]}, \"errNode\":{\"errCode\":\"0\", \"errMsg\":\"\"}}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);
        initialize();
        ivBack = _themeSetter.setHeaderTheme(actionBar, "FIND ITEMS", R.drawable.previous_icon);
        lvFindItem.setAdapter(itemAdapter);
        onClick();
        loadFilterList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_item, menu);
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
    protected void onResume() {
        super.onResume();
        //loadFilterList();
        //loadTempFilterList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*findItems.clear();
        itemAdapter.notifyDataSetChanged();*/

    }

    private void initialize(){
        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(FindItemActivity.this);
        progress_wheel = (ProgressWheel) findViewById(R.id.pwFindItem);
        backgroundAsnk = new RunBackgroundAsnk(FindItemActivity.this);
        lvFindItem = (ListView) findViewById(R.id.lvFindItem);
        findItems = new ArrayList<FindItem>();
        itemAdapter = new FindItemAdapter(FindItemActivity.this, findItems);
        llFindAllLayout = (LinearLayout) findViewById(R.id.llFindAllLayout);
        tvFindNoItem = (TextView) findViewById(R.id.tvFindNoItem);
        _pref = new Pref(FindItemActivity.this);
    }

    private void onClick(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadFilterList(){
        jsonObj1 = new JSONObject();
        jsonObj2 = new JSONObject();
        try {

            jsonObj2.put(TAG_ACCESSTOKEN, _pref.getAccessToken());
            jsonObj2.put(TAG_DEVICEID, _pref.getDeviceId());
            if(getIntent().getExtras().getString("finding_type").equals("barcode")){
                jsonObj2.put(TAG_BARCODE, getIntent().getExtras().getString("finding_code"));
            } else if(getIntent().getExtras().getString("finding_type").equals("article")){
                jsonObj2.put(TAG_ARTICLECODE, getIntent().getExtras().getString("finding_code"));
            } else {
                jsonObj2.put(TAG_CATEGORYCODE, getIntent().getExtras().getString("finding_code"));
            }
            jsonObj1.put(TAG_DATA, jsonObj2);

            Log.e("input data", jsonObj1.toString());
            backgroundAsnk = new RunBackgroundAsnk(FindItemActivity.this);
            backgroundAsnk.taskInterface = FindItemActivity.this;
            if(getIntent().getExtras().getString("finding_type").equals("barcode")){
                backgroundAsnk.execute(Constant.baseUrl + "findItemScan", jsonObj1.toString());
            } else if(getIntent().getExtras().getString("finding_type").equals("article")){
                backgroundAsnk.execute(Constant.baseUrl + "findItemArticle", jsonObj1.toString());
            } else {
                backgroundAsnk.execute(Constant.baseUrl + "findItemCategory", jsonObj1.toString());
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    private void loadTempFilterList(){
        progress_wheel.setVisibility(View.GONE);
        try {
            //JSONObject jsonAll = new JSONObject(jsonStr);
            JSONObject jsonAll = new JSONObject(jsonStrDemo);
            JSONObject jsonError = jsonAll.getJSONObject(TAG_ERRNODE);

            if(jsonError.getString(TAG_ERRCODE).equals("0")){

                JSONObject jsonData = jsonAll.getJSONObject(TAG_DATA);

                if(jsonData.getBoolean(TAG_SUCCESS)){

                    JSONArray jsonFindItemDetails = jsonData.getJSONArray(TAG_DETAILS);
                    llFindAllLayout.setVisibility(View.VISIBLE);

                    for(int i=0; i<jsonFindItemDetails.length(); i++){

                        final FindItem _item = new FindItem();
                        JSONObject jsonObject = jsonFindItemDetails.getJSONObject(i);
                        _item.setArticleCode(jsonObject.getString(TAG_ARTICLECODE));
                        _item.setBarcode(jsonObject.getString(TAG_BARCODE));
                        _item.setCategoryName(jsonObject.getString(TAG_CATEGORY));
                        _item.setDetails(jsonObject.getString(TAG_ITEMDETAILS));
                        _item.setCount(jsonObject.getString(TAG_TOTALCOUNT));

                        findItems.add(_item);
                    }

                    itemAdapter.notifyDataSetChanged();

                } else {

                    tvFindNoItem.setVisibility(View.VISIBLE);

                }
            } else {

                tvFindNoItem.setVisibility(View.VISIBLE);
                tvFindNoItem.setText(jsonError.getString(TAG_ERRMSG));

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStarted() {
        progress_wheel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompleted(String jsonStr) {
        progress_wheel.setVisibility(View.GONE);
        try {
            JSONObject jsonAll = new JSONObject(jsonStr);
            //JSONObject jsonAll = new JSONObject(jsonStrDemo);
            JSONObject jsonError = jsonAll.getJSONObject(TAG_ERRNODE);

            if(jsonError.getString(TAG_ERRCODE).equals("0")){

                JSONObject jsonData = jsonAll.getJSONObject(TAG_DATA);

                if(jsonData.getBoolean(TAG_SUCCESS)){

                    JSONArray jsonFindItemDetails = jsonData.getJSONArray(TAG_DETAILS);
                    llFindAllLayout.setVisibility(View.VISIBLE);

                    for(int i=0; i<jsonFindItemDetails.length(); i++){

                        final FindItem _item = new FindItem();
                        JSONObject jsonObject = jsonFindItemDetails.getJSONObject(i);
                        _item.setArticleCode(jsonObject.getString(TAG_ARTICLECODE));
                        _item.setBarcode(jsonObject.getString(TAG_BARCODE));
                        _item.setCategoryName(jsonObject.getString(TAG_CATEGORY));
                        _item.setDetails(jsonObject.getString(TAG_ITEMDETAILS));
                        _item.setCount(jsonObject.getString(TAG_TOTALCOUNT));

                        findItems.add(_item);
                    }

                    itemAdapter.notifyDataSetChanged();

                } else {

                    tvFindNoItem.setVisibility(View.VISIBLE);

                }
            } else {

                tvFindNoItem.setVisibility(View.VISIBLE);
                tvFindNoItem.setText(jsonError.getString(TAG_ERRMSG));

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
