package com.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.PhysicalCountResultItem;
import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.utility.Constant;
import com.utility.DbAdapter;
import com.utility.NetworkConnectionCheck;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;
import com.utility.ThemeSetter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class PhysicalCountResultActivity extends ActionBarActivity implements BackgroundTaskInterface{

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack;

    private ListView lvCountResult;
    private LinearLayout llAddCount, llSubmitCount, llResultContainer, llSubmitContainer;
    private ProgressWheel pwResultSubmit;

    private ArrayList<PhysicalCountResultItem> resultItems;
    private PhysicalCountResultAdapter resultAdapter;

    private DbAdapter dbAdapter;

    private Pref _pref;
    private NetworkConnectionCheck connectionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_count_result);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "PHYSICAL COUNT", R.drawable.previous_icon);
        _themeSetter.setButtonColor(llSubmitCount);
        _themeSetter.setButtonColor(llAddCount);

        loadResultCount();
        onClick();
    }

    private void initialize(){
        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(PhysicalCountResultActivity.this);
        _pref = new Pref(PhysicalCountResultActivity.this);
        connectionCheck = new NetworkConnectionCheck(PhysicalCountResultActivity.this);

        lvCountResult = (ListView) findViewById(R.id.lvCountResult);
        llAddCount = (LinearLayout) findViewById(R.id.llAddCount);
        llSubmitCount = (LinearLayout) findViewById(R.id.llSubmitCount);
        llResultContainer = (LinearLayout) findViewById(R.id.llResultContainer);
        llSubmitContainer = (LinearLayout) findViewById(R.id.llSubmitContainer);
        pwResultSubmit = (ProgressWheel) findViewById(R.id.pwResultSubmit);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();

    }

    private void onClick(){

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llAddCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // onBackPressed();

                startActivity( new Intent(getBaseContext(),PhysicalCountAddActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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
    }

    private void loadResultCount(){

        dbAdapter = new DbAdapter(PhysicalCountResultActivity.this);
        dbAdapter.open();

        resultItems = dbAdapter.getPhysicalCountRecords();
        resultAdapter = new PhysicalCountResultAdapter(PhysicalCountResultActivity.this, resultItems);
        lvCountResult.setAdapter(resultAdapter);

        dbAdapter.close();

        if(resultItems.size() == 0){
            String current_surveyid = _pref.getSession("current_surveyid");
            _pref.setSession("complete_full_physical_" + current_surveyid, "");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_physical_count_details, menu);
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

    private void submitCount() {

        try{
            if(resultItems.size() > 0) {

                JSONArray jsonArray = new JSONArray();;

                String url = Constant.baseUrl + "setPhysicalCount";

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("accesstoken", _pref.getAccessToken());
                jsonObject.put("deviceId", _pref.getDeviceId());
                jsonObject.put("surveyId", _pref.getSession("current_surveyid"));

                for (int i = 0; i < resultItems.size(); i++) {

                    PhysicalCountResultItem _item = resultItems.get(i);

                    JSONObject jsonScan = new JSONObject();
                    jsonScan.put("catId", _item.getCatId());
                    jsonScan.put("rackNo", _item.getRackNo());
                    jsonScan.put("noOfItems", String.valueOf(_item.getNoOfItems()));

                    jsonArray.put(jsonScan);
                }

                jsonObject.put("scanedData", jsonArray);

                JSONObject data = new JSONObject();
                data.put("data", jsonObject);

                String jsonInput = data.toString();
                Log.v("submit", jsonInput);


                RunBackgroundAsnk submit_result = new RunBackgroundAsnk(
                        PhysicalCountResultActivity.this);
                submit_result.taskInterface = PhysicalCountResultActivity.this;
                submit_result.execute(url, jsonInput);

            } else {

                //pwResultSubmit.setVisibility(View.GONE);
                //llResultContainer.setVisibility(View.VISIBLE);
                //llSubmitContainer.setVisibility(View.VISIBLE);

                Toast.makeText(PhysicalCountResultActivity.this, "No item to submit. ",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted() {

        pwResultSubmit.setVisibility(View.VISIBLE);
        llResultContainer.setVisibility(View.GONE);
        llSubmitContainer.setVisibility(View.GONE);
    }

    @Override
    public void onCompleted(String jsonStr) {

        if (jsonStr != null) {

            Log.v("result", jsonStr);

            try {

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject errNodeObj = jsonObj.getJSONObject("errNode");
                String errCode = errNodeObj.getString("errCode");
                String errMsg = errNodeObj.getString("errMsg");

                if(errCode.equalsIgnoreCase("0")){

                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    String success = dataObj.getString("success");

                    if(success.equalsIgnoreCase("true")){

                        dbAdapter.open();
                        dbAdapter.deleteRecordPhysicalCount(_pref.getSession("current_surveyid"));
                        dbAdapter.close();

                        String current_surveyid = _pref.getSession("current_surveyid");
                        _pref.setSession("complete_full_physical_" + current_surveyid, "2");

                        Toast.makeText(PhysicalCountResultActivity.this, "Count result " +
                                "submitted successfully.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PhysicalCountResultActivity.this, ScheduleActivity.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();

                    }
                    else{

                        pwResultSubmit.setVisibility(View.GONE);
                        llResultContainer.setVisibility(View.VISIBLE);
                        llSubmitContainer.setVisibility(View.VISIBLE);

                        Toast.makeText(PhysicalCountResultActivity.this, "Error in submission. Try again.",
                                Toast.LENGTH_LONG).show();

                    }
                }

                else{

                    Toast.makeText(PhysicalCountResultActivity.this, errMsg, Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else{

            Toast.makeText(PhysicalCountResultActivity.this, "Something going wrong!! Please Try Again", Toast.LENGTH_LONG).show();

        }

    }

    public class PhysicalCountResultAdapter extends ArrayAdapter<PhysicalCountResultItem> {

        private LayoutInflater inflater;
        private Context context;

        public PhysicalCountResultAdapter(Context context,
                                          ArrayList<PhysicalCountResultItem> resultItems) {
            // TODO Auto-generated constructor stub
            super(context, R.layout.physical_count_result_row, R.id.tvType,
                    resultItems);
            this.context = context;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final PhysicalCountResultItem surveyList = (PhysicalCountResultItem) this
                    .getItem(position);

            ViewHolder holder;
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.physical_count_result_row, null);

            holder.tvType = (TextView) convertView
                    .findViewById(R.id.tvType);
            holder.tvRackNo = (TextView) convertView
                    .findViewById(R.id.tvRackNo);
            holder.tvNoOfItems = (TextView) convertView
                    .findViewById(R.id.tvNoOfItems);
            holder.ivDeleteRecord = (ImageView) convertView.findViewById(R.id.ivDeleteRecord);


            convertView.setTag(holder);
            holder = (ViewHolder) convertView.getTag();

            holder.tvType.setText(surveyList.getCatType());
            holder.tvNoOfItems.setText(String.valueOf(surveyList.getNoOfItems()));

            if(!surveyList.getRackNo().equalsIgnoreCase("")){
                holder.tvRackNo.setText(surveyList.getRackNo());
            }
            else{
                holder.tvRackNo.setText("-");
            }

            holder.ivDeleteRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog deleteDialog = getDeleteDialog(getItem(position).getId());
                    deleteDialog.show();
                }
            });


            return convertView;
        }

        public class ViewHolder {

            TextView tvType;
            TextView tvRackNo;
            TextView tvNoOfItems;
            ImageView ivDeleteRecord;

        }

        private AlertDialog getDeleteDialog(final String id){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Deletion Confirmation");
            builder.setMessage("Do you want to delete it?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dbAdapter.open();
                            dbAdapter.deleteRecordPhysicalCountById(id);
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
