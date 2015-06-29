package com.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.CountResultItem;
import com.bean.CountResultWithArticleItem;
import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.utility.Constant;
import com.utility.NetworkConnectionCheck;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;
import com.utility.ThemeSetter;
import com.utility.DbAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CountResultActivity extends ActionBarActivity implements BackgroundTaskInterface{

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack;

    private ListView lvCountResult;
    private ArrayList<CountResultItem> resultItems;
    private ArrayList<CountResultWithArticleItem> resultItemsWithArticle;
    private CountResultAdapter resultAdapter;
    private DbAdapter dbAdapter;
    private LinearLayout llSubmitCount, llResultContainer, llSubmitContainer, llBackToScan;
    private RunBackgroundAsnk backgroundAsnk;
    private ProgressWheel pwResultSubmit;
    private JSONObject jsonObj1, jsonObj2, jsonObj3;
    private JSONArray jsonArr;

    private static final String TAG_DATA = "data";
    private static final String TAG_ACCESSTOKEN = "accesstoken";
    private static final String TAG_DEVICEID = "deviceId";
    private static final String TAG_SURVEYID = "surveyId";
    private static final String TAG_SCANEDDATA = "scanedData";
    private static final String TAG_BARCODE = "barcode";
    private static final String TAG_COUNTNO = "countNo";
    private static final String TAG_ARTICLENO = "articleNo";
    private static final String TAG_CATEGORYID = "categoryId";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERRNODE = "errNode";
    private static final String TAG_ERRCODE = "errCode";
    private static final String TAG_ERRMSG = "errMsg";
    private Pref _pref;
    private NetworkConnectionCheck connectionCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_result);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "SCAN COUNT", R.drawable.previous_icon);
        _themeSetter.setButtonColor(llSubmitCount);
        _themeSetter.setButtonColor(llBackToScan);

        loadResultCount();
        onClick();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count_resunt, menu);
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

    private void initialize(){

        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(CountResultActivity.this);
        _pref = new Pref(CountResultActivity.this);
        connectionCheck = new NetworkConnectionCheck(CountResultActivity.this);

        lvCountResult = (ListView) findViewById(R.id.lvCountResult);
        dbAdapter = new DbAdapter(CountResultActivity.this);

        llSubmitCount = (LinearLayout) findViewById(R.id.llSubmitCount);
        llResultContainer = (LinearLayout) findViewById(R.id.llResultContainer);
        llSubmitContainer = (LinearLayout) findViewById(R.id.llSubmitContainer);
        pwResultSubmit = (ProgressWheel) findViewById(R.id.pwResultSubmit);
        llBackToScan = (LinearLayout) findViewById(R.id.llBackToScan);

        jsonObj1 = new JSONObject();
        jsonObj2 = new JSONObject();
        jsonArr = new JSONArray();

    }

    private void onClick(){
        lvCountResult.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        llSubmitCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionCheck.isNetworkAvailable()) {
                    submitCount();
                } else {
                    connectionCheck.getNetworkActiveAlert().show();
                }
            }
        });

        llBackToScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void loadResultCount(){
        dbAdapter.open();
        resultItems = dbAdapter.getRecords();
        dbAdapter.close();
        resultAdapter = new CountResultAdapter(CountResultActivity.this, resultItems);
        lvCountResult.setAdapter(resultAdapter);

        if(resultItems.size() == 0){
            String current_surveyid = _pref.getSession("current_surveyid");
            _pref.setSession("complete_full_scan_" + current_surveyid, "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();

    }

    @Override
    public void onStarted() {

        pwResultSubmit.setVisibility(View.VISIBLE);
        llResultContainer.setVisibility(View.GONE);
        llSubmitContainer.setVisibility(View.GONE);
    }

    @Override
    public void onCompleted(String jsonStr) {
        try {
            Log.e("result", jsonStr);
            JSONObject jsonAll = new JSONObject(jsonStr);
            JSONObject jsonError = jsonAll.getJSONObject(TAG_ERRNODE);
            if(jsonError.getString(TAG_ERRCODE).equals("0")){
                JSONObject jsonData = jsonAll.getJSONObject(TAG_DATA);
                if(jsonData.getBoolean(TAG_SUCCESS)){
                    dbAdapter.open();
                    dbAdapter.deleteRecord(_pref.getSession("current_surveyid"));
                    dbAdapter.close();
                    /*if(_pref.getSession("selected_schedule").equals("daily")
                            && _pref.getSession("selected_count_type").equals("scan")){
                        _pref.setSession("complete_daily_scan", "2");
                    } if(_pref.getSession("selected_schedule").equals("full")
                            && _pref.getSession("selected_count_type").equals("scan")){
                        _pref.setSession("complete_full_scan", "2");
                    }*/

                    String current_surveyid = _pref.getSession("current_surveyid");
                    _pref.setSession("complete_full_scan_" + current_surveyid, "2");

                    Toast.makeText(CountResultActivity.this, "Count result " +
                            "submitted successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CountResultActivity.this, ScheduleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                } else {

                    pwResultSubmit.setVisibility(View.GONE);
                    llResultContainer.setVisibility(View.VISIBLE);
                    llSubmitContainer.setVisibility(View.VISIBLE);

                    Toast.makeText(CountResultActivity.this, "Error in submission. Try again.",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(CountResultActivity.this, jsonError.getString(TAG_ERRMSG),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void submitCount() {
        dbAdapter.open();
        resultItemsWithArticle = dbAdapter.getRecordsForSubmit();
        dbAdapter.close();
        try{
            if(resultItemsWithArticle.size() > 0) {
                jsonObj2.put(TAG_ACCESSTOKEN, _pref.getAccessToken());
                jsonObj2.put(TAG_DEVICEID, _pref.getDeviceId());
                jsonObj2.put(TAG_SURVEYID, _pref.getSession("current_surveyid"));
                for (int i = 0; i < resultItemsWithArticle.size(); i++) {
                    CountResultWithArticleItem _item = resultItemsWithArticle.get(i);
                    jsonObj3 = new JSONObject();
                    jsonObj3.put(TAG_BARCODE, _item.getBarcode());
                    jsonObj3.put(TAG_COUNTNO, _item.getCountNo());
                    jsonObj3.put(TAG_ARTICLENO, _item.getArticleNo());
                    jsonObj3.put(TAG_CATEGORYID, _item.getCategoryId());
                    jsonArr.put(jsonObj3);
                }

                jsonObj2.put(TAG_SCANEDDATA, jsonArr);
                jsonObj1.put(TAG_DATA, jsonObj2);

                Log.e("input", jsonObj1.toString());

                backgroundAsnk = new RunBackgroundAsnk(CountResultActivity.this);
                backgroundAsnk.taskInterface = CountResultActivity.this;
                backgroundAsnk.execute(Constant.baseUrl + "setScanCount", jsonObj1.toString());
            } else {
                pwResultSubmit.setVisibility(View.GONE);
                llResultContainer.setVisibility(View.VISIBLE);
                llSubmitContainer.setVisibility(View.VISIBLE);
                Toast.makeText(CountResultActivity.this, "No item to submit. ",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public class CountResultAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<CountResultItem> countItem;
        private LayoutInflater inflater;
        private ThemeSetter themeSetter;
        private DbAdapter dbAdapter;

        public CountResultAdapter(Context context, ArrayList<CountResultItem> countItem) {
            this.context = context;
            this.countItem = countItem;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            themeSetter = new ThemeSetter(context);
            dbAdapter = new DbAdapter(context);
        }

        @Override
        public int getCount() {
            return countItem.size();
        }

        @Override
        public CountResultItem getItem(int i) {
            return countItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            View rowView = view;
            if(rowView == null){
                rowView = inflater.inflate(R.layout.scan_count_result_item_row, null);
                ItemHolder holder = new ItemHolder();
                holder.tvBarcodeNo = (TextView) rowView.findViewById(R.id.tvBarcodeNo);
                holder.tvCountNo = (TextView) rowView.findViewById(R.id.tvCountNo);
                holder.ivDeleteRecord = (ImageView) rowView.findViewById(R.id.ivDeleteRecord);

                rowView.setTag(holder);
            }

            ItemHolder newHolder = (ItemHolder)rowView.getTag();
            if(getItem(i).getItemType().equals("article")) {
                newHolder.tvBarcodeNo.setText("Article Id: " + getItem(i).getBarcode());
                newHolder.tvCountNo.setText(getItem(i).getCountNo());
                newHolder.tvBarcodeNo.setTextColor(Color.parseColor("#0000FF"));
                newHolder.tvCountNo.setTextColor(Color.parseColor("#0000FF"));
            } else if(getItem(i).getItemType().equals("product")){
                newHolder.tvBarcodeNo.setText("Category: " + getItem(i).getBarcode());
                newHolder.tvCountNo.setText(getItem(i).getCountNo());
                newHolder.tvBarcodeNo.setTextColor(Color.parseColor("#00AEFF"));
                newHolder.tvCountNo.setTextColor(Color.parseColor("#00AEFF"));
            } else{
                newHolder.tvBarcodeNo.setText("#" + getItem(i).getBarcode());
                newHolder.tvCountNo.setText(getItem(i).getCountNo());
            }

            newHolder.ivDeleteRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog deleteDialog = getDeleteDialog(getItem(i).getBarcode(),
                            getItem(i).getItemType());
                    deleteDialog.show();
                }
            });

            return rowView;
        }

        private class ItemHolder {
            TextView tvBarcodeNo, tvCountNo;
            ImageView ivDeleteRecord;
        }

        private AlertDialog getDeleteDialog(final String id, final String itemType){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Deletion Confirmation");
            builder.setMessage("Do you want to delete it?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dbAdapter.open();
                            dbAdapter.deleteRecordScanCount(id, itemType);
                            dbAdapter.close();

                            loadResultCount();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            return alertDialog;
        }
    }
}
