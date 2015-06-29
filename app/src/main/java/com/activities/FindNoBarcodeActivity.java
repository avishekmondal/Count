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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.utility.Constant;
import com.utility.Pref;
import com.utility.ThemeSetter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FindNoBarcodeActivity extends ActionBarActivity {

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack;
    private TextView tvHeadingNoBarcode;
    private LinearLayout llBtnFindItem;
    private AutoCompleteTextView atvFindItem;
    private Pref _pref;
    private String selectedFindType;
    private String selectedFindValue;


    private static final String TAG_DATA = "data";
    private static final String TAG_CATEGORYDETAILS = "categoryDetails";
    private static final String TAG_CATID = "catId";
    private static final String TAG_PRODUCTNAME = "productName";
    private static final String TAG_ARTICLEDETAILS = "articleDetails";
    private static final String TAG_ARTICLENO = "articleNo";
    private static final String TAG_ERRNODE = "errNode";
    private static final String TAG_ERRCODE = "errCode";
    private static final String TAG_ERRMSG = "errMsg";

    private List<String> productCategoryItems;
    private List<String> productIdList;
    private List<String> articleNoList;
    private ArrayAdapter<String> strAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_no_barcode);
        selectedFindType = getIntent().getExtras().getString("find_type");
        initialize();

        _themeSetter.setButtonColor(llBtnFindItem);
        ivBack = _themeSetter.setHeaderTheme(actionBar, "FIND ITEMS", R.drawable.previous_icon);

        onClick();

        if(selectedFindType.equals("article")){
            tvHeadingNoBarcode.setText("BY ARTICLE");
            atvFindItem.setHint("Enter Article No.");
            loadArticleNo();
        } else if(selectedFindType.equals("category")) {
            tvHeadingNoBarcode.setText("BY CATEGORY");
            atvFindItem.setHint("Enter Product Category");
            loadCategory();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_no_barcode, menu);
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
        _themeSetter = new ThemeSetter(FindNoBarcodeActivity.this);
        tvHeadingNoBarcode = (TextView) findViewById(R.id.tvHeadingNoBarcode);
        llBtnFindItem = (LinearLayout) findViewById(R.id.llBtnFindItem);
        atvFindItem = (AutoCompleteTextView) findViewById(R.id.atvFindItem);
        productCategoryItems = new ArrayList<String>();
        productIdList = new ArrayList<String>();
        articleNoList = new ArrayList<String>();
        _pref = new Pref(FindNoBarcodeActivity.this);
    }

    private void onClick(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        atvFindItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selectedFindType.equals("article")){

                    int position = articleNoList.indexOf(atvFindItem.getText().toString());
                    selectedFindValue = articleNoList.get(position);

                } else if(selectedFindType.equals("category")) {

                    int position = productCategoryItems.indexOf(atvFindItem.getText().toString());
                    selectedFindValue = productIdList.get(position);
                }
            }
        });

        llBtnFindItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FindNoBarcodeActivity.this, FindItemActivity.class);
                intent.putExtra("finding_code", selectedFindValue);
                intent.putExtra("finding_type", selectedFindType);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Intent intent = new Intent(FindNoBarcodeActivity.this, HomeActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void loadCategory(){
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
                    Log.e("id", jObj.getString(TAG_CATID));
                }

                strAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                        productCategoryItems);
                atvFindItem.setThreshold(1);
                atvFindItem.setAdapter(strAdapter);

            } else {
                Toast.makeText(FindNoBarcodeActivity.this, jsonError.getString(TAG_ERRMSG),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadArticleNo(){
        try {
            String jsonArticleNoListStr = getStringFromFile(
                    _pref.getSession(Constant.JSON_ARTICLENO_FILE_NAME));
            JSONObject jsonArticleList = new JSONObject(jsonArticleNoListStr);
            JSONObject jsonError = jsonArticleList.getJSONObject(TAG_ERRNODE);

            if(jsonError.getString(TAG_ERRCODE).equals("0")) {

                JSONObject jsonData = jsonArticleList.getJSONObject(TAG_DATA);
                JSONArray articleDetails = jsonData.getJSONArray(TAG_ARTICLEDETAILS);

                for(int i=0; i< articleDetails.length(); i++){

                    JSONObject jObj = articleDetails.getJSONObject(i);
                    articleNoList.add(jObj.getString(TAG_ARTICLENO));
                }

                strAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                        articleNoList);
                atvFindItem.setThreshold(1);
                atvFindItem.setAdapter(strAdapter);

            } else {
                Toast.makeText(FindNoBarcodeActivity.this, jsonError.getString(TAG_ERRMSG),
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
