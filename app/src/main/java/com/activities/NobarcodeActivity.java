package com.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.count.R;
import com.utility.Constant;
import com.utility.Pref;
import com.utility.ThemeSetter;
import com.utility.DbAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NobarcodeActivity extends ActionBarActivity {

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack;

    private LinearLayout llAddCount;
    private EditText etArticleCount, etArticleNo, etCategoryCount;
    private AutoCompleteTextView avtCategory;

    private static final String TAG_DATA = "data";
    private static final String TAG_CATEGORYDETAILS = "categoryDetails";
    private static final String TAG_CATID = "catId";
    private static final String TAG_PRODUCTNAME = "productName";
    private static final String TAG_ERRNODE = "errNode";
    private static final String TAG_ERRCODE = "errCode";
    private static final String TAG_ERRMSG = "errMsg";

    private List<String> productCategoryItems;
    private List<String> productIdList;
    private ArrayAdapter<String> strProductAdapter;
    private String productId;

    private Pref _pref;
    private DbAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nobarcode);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "SCAN COUNT", R.drawable.previous_icon);
        _themeSetter.setButtonColor(llAddCount);

        onClick();
        loadProductCategory();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nobarcode, menu);
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
        _themeSetter = new ThemeSetter(NobarcodeActivity.this);
        _pref = new Pref(NobarcodeActivity.this);
        dbAdapter = new DbAdapter(NobarcodeActivity.this);

        llAddCount = (LinearLayout) findViewById(R.id.llAddCount);
        etArticleCount = (EditText) findViewById(R.id.etArticleCount);
        etArticleNo = (EditText) findViewById(R.id.etArticleNo);
        etCategoryCount = (EditText) findViewById(R.id.etCategoryCount);
        avtCategory = (AutoCompleteTextView) findViewById(R.id.avtCategory);

        productCategoryItems = new ArrayList<String>();
        productIdList = new ArrayList<String>();

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
            public void onClick(View view) {

                dbAdapter.open();
                if((!etArticleCount.getText().toString().equals("") ||
                        !etArticleNo.getText().toString().equals("")) &&
                        (!avtCategory.getText().toString().equals("") ||
                        !etCategoryCount.getText().toString().equals(""))) {
                    Toast.makeText(NobarcodeActivity.this,
                            "You can't select artical no. and product category at same time", Toast.LENGTH_LONG).show();

                } else if(!etArticleCount.getText().toString().equals("") ||
                        !etArticleNo.getText().toString().equals("")) {
                    if(etArticleCount.getText().toString().equals("") ||
                            etArticleNo.getText().toString().equals("")) {
                        Toast.makeText(NobarcodeActivity.this,
                                "Please give the both article details.", Toast.LENGTH_LONG).show();
                    } else {
                        dbAdapter.insertValue("NA", Integer.parseInt(etArticleCount.getText().toString())
                                , etArticleNo.getText().toString(), "NA", "NA", "1", "NA");
                        String current_surveyid = _pref.getSession("current_surveyid");
                        _pref.setSession("complete_full_scan_" + current_surveyid, "1");

                        dbAdapter.close();

                        Intent intent = new Intent(NobarcodeActivity.this, ScanScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("scan_type", "count");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    }
                } else if(!avtCategory.getText().toString().equals("") ||
                        !etCategoryCount.getText().toString().equals("")) {
                    if(avtCategory.getText().toString().equals("") ||
                            etCategoryCount.getText().toString().equals("")) {
                        Toast.makeText(NobarcodeActivity.this,
                                "Please give the both product category details.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        dbAdapter.insertValue("NA", Integer.parseInt(etCategoryCount.getText().toString())
                                , "NA", avtCategory.getText().toString(), productId, "NA", "1");
                        String current_surveyid = _pref.getSession("current_surveyid");
                        _pref.setSession("complete_full_scan_" + current_surveyid, "1");

                        dbAdapter.close();

                        Intent intent = new Intent(NobarcodeActivity.this, ScanScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("scan_type", "count");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    }
                } else{
                    Toast.makeText(NobarcodeActivity.this, "Please provide the No. of item",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        avtCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = productCategoryItems.indexOf(avtCategory.getText().toString());
                productId = productIdList.get(position);
            }
        });
    }

    private void loadProductCategory(){
        try {
            String jsonProductListStr = getStringFromFile(
                    _pref.getSession(Constant.JSON_CATEGORY_FILE_NAME));
            JSONObject jsonProductList = new JSONObject(jsonProductListStr);
            JSONObject jsonError = jsonProductList.getJSONObject(TAG_ERRNODE);

            if(jsonError.getString(TAG_ERRCODE).equals("0")) {

                JSONObject jsonData = jsonProductList.getJSONObject(TAG_DATA);
                JSONArray productDetails = jsonData.getJSONArray(TAG_CATEGORYDETAILS);

                for(int i=0; i< productDetails.length(); i++){

                    JSONObject jObj = productDetails.getJSONObject(i);
                    productCategoryItems.add(jObj.getString(TAG_PRODUCTNAME));
                    productIdList.add(jObj.getString(TAG_CATID));
                    Log.e("product", jObj.getString(TAG_PRODUCTNAME));
                }

                strProductAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                        productCategoryItems);
                avtCategory.setThreshold(1);
                avtCategory.setAdapter(strProductAdapter);

            } else {
                Toast.makeText(NobarcodeActivity.this, jsonError.getString(TAG_ERRMSG),
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
}
