package com.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.count.R;
import com.utility.Constant;
import com.utility.Pref;
import com.utility.ThemeSetter;
import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;
import com.utility.DbAdapter;

public class ScanScreenActivity extends ActionBarActivity implements ScanditSDKListener {

    private ScanditSDK mBarcodePicker;
    public static final String sScanditSdkAppKey = "VnpXA5CD8+MHJvZJA8fQ1NhLz9JvTGlOAIOtL1JHlf0";

    private RelativeLayout overLay;
    private TextView tvScanResult, tvScanStatus, tvTopGuideLines;
    private ImageView ivScanResult;
    private TextView tvNoBarcode, tvFinish;
    private LinearLayout leftLayout, rightLayout, llBtnFindArticleLayout, llBtnFindCategoryLayout;
    private DbAdapter dbAdapter;
    private ThemeSetter _themeSetter;
    private Pref _pref;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.wakeLock.acquire();

        _themeSetter = new ThemeSetter(ScanScreenActivity.this);
        _pref = new Pref(ScanScreenActivity.this);
        dbAdapter = new DbAdapter(ScanScreenActivity.this);

        initializeScanScreen();
        onClick();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_screen, menu);
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

    @Override
    public void didCancel() {

        mBarcodePicker.stopScanning();
        finish();
    }

    @Override
    public void didScanBarcode(String barcode, String symbology) {
        String cleanedBarcode = "";
        for (int i = 0 ; i < barcode.length(); i++) {
            if (barcode.charAt(i) > 30) {
                cleanedBarcode += barcode.charAt(i);
            }
        }
        mBarcodePicker.stopScanning();
        if(getIntent().getExtras().getString("scan_type").equals("count")) {
            tvScanResult.setText("SUCCESS!!");
            tvScanStatus.setText("Scan Next");
            ivScanResult.setImageResource(R.drawable.thumbsup_icon);
            ivScanResult.setVisibility(View.VISIBLE);
            tvScanStatus.setVisibility(View.VISIBLE);
            tvScanResult.setVisibility(View.VISIBLE);
            //storeData(symbology + " : " + cleanedBarcode);
            storeData(cleanedBarcode);
        } else {
            Intent intent = new Intent(ScanScreenActivity.this, FindItemActivity.class);
            intent.putExtra("finding_code", cleanedBarcode);
            intent.putExtra("finding_type", "barcode");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void didManualSearch(String s) {

    }

    @Override
    protected void onResume() {
        mBarcodePicker.startScanning();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mBarcodePicker.stopScanning();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.wakeLock.release();
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initializeScanScreen(){

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ScanditSDKAutoAdjustingBarcodePicker picker = new ScanditSDKAutoAdjustingBarcodePicker(
                this, sScanditSdkAppKey, ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);

        setContentView(picker);
        mBarcodePicker = picker;
        mBarcodePicker.getOverlayView().addListener(this);

        overLay = (RelativeLayout)mBarcodePicker.getOverlayView();

        if(getIntent().getExtras().getString("scan_type").equals("count")){
            setScanView();
        } else {
            setFindView();
        }

    }

    private void storeData(String codeData){



        dbAdapter.open();
        dbAdapter.insertValue(codeData, 1, "NA", "NA", "NA", "NA", "NA");
        dbAdapter.close();
        /*if(_pref.getSession("selected_schedule").equals("daily")
                && _pref.getSession("selected_count_type").equals("scan")){
            _pref.setSession("complete_daily_scan", "1");
        } if(_pref.getSession("selected_schedule").equals("full")
                && _pref.getSession("selected_count_type").equals("scan")){
            _pref.setSession("complete_full_scan", "1");
        }*/
        String current_surveyid = _pref.getSession("current_surveyid");
        _pref.setSession("complete_full_scan_" + current_surveyid, "1");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                tvScanResult.setText("SCANNING..");
                tvScanStatus.setText("Please Wait");
                ivScanResult.setVisibility(View.INVISIBLE);
                tvScanStatus.setVisibility(View.INVISIBLE);
                tvScanResult.setVisibility(View.INVISIBLE);
                mBarcodePicker.startScanning();

            }
        }, 1000);
    }

    private void onClick(){
        if(getIntent().getExtras().getString("scan_type").equals("count")){

            leftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(ScanScreenActivity.this, NobarcodeActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            rightLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(ScanScreenActivity.this, CountResultActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

        } else{

            llBtnFindArticleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ScanScreenActivity.this, FindNoBarcodeActivity.class);
                    intent.putExtra("find_type", "article");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            llBtnFindCategoryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ScanScreenActivity.this, FindNoBarcodeActivity.class);
                    intent.putExtra("find_type", "category");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
    }

    private void setScanView(){
        RelativeLayout  upperLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams upperLayoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        upperLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);

        LinearLayout allItemLayout = new LinearLayout(this);
        allItemLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams allItemLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setPadding(0, 60, 0, 0);
        innerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams innerLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ivScanResult = new ImageView(this);
        ivScanResult.setImageResource(R.drawable.ic_launcher);
        ivScanResult.setVisibility(View.INVISIBLE);
        innerLayout.addView(ivScanResult);

        tvScanResult = new TextView(this);
        tvScanResult.setText("SCANNING..");
        tvScanResult.setTextColor(Color.parseColor("#FFFFFF"));
        tvScanResult.setPadding(10, 2, 0, 0);
        tvScanResult.setTextSize(25);
        tvScanResult.setGravity(Gravity.CENTER_HORIZONTAL);
        tvScanResult.setVisibility(View.INVISIBLE);
        innerLayout.addView(tvScanResult);
        allItemLayout.addView(innerLayout, innerLayoutParam);

        tvScanStatus = new TextView(this);
        tvScanStatus.setText("Please Wait");
        tvScanStatus.setTextColor(Color.parseColor("#FFFFFF"));
        tvScanStatus.setTextSize(18);
        tvScanStatus.setGravity(Gravity.CENTER_HORIZONTAL);
        tvScanStatus.setPadding(0, 8, 0, 0);
        tvScanStatus.setTextAppearance(ScanScreenActivity.this, R.style.italicText);
        tvScanStatus.setVisibility(View.INVISIBLE);
        allItemLayout.addView(tvScanStatus);

        tvTopGuideLines = new TextView(this);
        tvTopGuideLines.setText("Center barcode inside the square");
        tvTopGuideLines.setTextColor(Color.parseColor("#FFFFFF"));
        tvTopGuideLines.setTextSize(18);
        tvTopGuideLines.setTextAppearance(ScanScreenActivity.this, R.style.boldText);
        tvTopGuideLines.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTopGuideLines.setPadding(0, 35, 0, 0);
        allItemLayout.addView(tvTopGuideLines);

        upperLayout.addView(allItemLayout, allItemLayoutParam);
        overLay.addView(upperLayout, upperLayoutParam);

        RelativeLayout bottomLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams bottomLayoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        bottomLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        LinearLayout innerBottomLayout = new LinearLayout(this);
        LinearLayout.LayoutParams innerBottomLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                80);
        innerBottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerBottomLayout.setWeightSum(10);

        leftLayout = new LinearLayout(this);
        LinearLayout.LayoutParams leftLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        leftLayoutParams.weight = 5;
        leftLayout.setLayoutParams(leftLayoutParams);
        leftLayout.setBackground(new ColorDrawable(Color.parseColor("#00A3E8")));
        leftLayout.setClickable(true);
        leftLayout.setGravity(Gravity.CENTER);

        tvNoBarcode = new TextView(this);
        TableRow.LayoutParams tvNoBarcodeParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tvNoBarcode.setText("NO BARCODE");
        tvNoBarcode.setTextSize(20);
        tvNoBarcode.setGravity(Gravity.CENTER);
        tvNoBarcode.setTextAppearance(ScanScreenActivity.this, R.style.boldText);
        tvNoBarcode.setLayoutParams(tvNoBarcodeParams);

        leftLayout.addView(tvNoBarcode);

        rightLayout = new LinearLayout(this);
        LinearLayout.LayoutParams rightLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        rightLayoutParams.weight = 5;
        rightLayout.setLayoutParams(leftLayoutParams);
        rightLayout.setBackground(new ColorDrawable(Color.parseColor("#22B04C")));
        rightLayout.setClickable(true);
        rightLayout.setGravity(Gravity.CENTER);

        tvFinish = new TextView(this);
        TableRow.LayoutParams tvFinishParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tvFinish.setText("FINISH");
        tvFinish.setTextSize(20);
        tvFinish.setGravity(Gravity.CENTER);
        tvFinish.setTextAppearance(ScanScreenActivity.this, R.style.boldText);
        tvFinish.setLayoutParams(tvFinishParams);

        rightLayout.addView(tvFinish);

        innerBottomLayout.addView(leftLayout, leftLayoutParams);
        innerBottomLayout.addView(rightLayout, rightLayoutParams);

        bottomLayout.addView(innerBottomLayout, innerBottomLayoutParam);
        overLay.addView(bottomLayout, bottomLayoutParam);
    }

    private void setFindView(){
        RelativeLayout  upperLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams upperLayoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        upperLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);

        LinearLayout textLayout = new LinearLayout(this);
        LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                230);
        textLayout.setOrientation(LinearLayout.HORIZONTAL);
        textLayout.setGravity(Gravity.BOTTOM);

        TextView tvFindItem = new TextView(this);
        TableRow.LayoutParams tvFindItemParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tvFindItem.setText("FIND ITEM");
        tvFindItem.setTextColor(Color.parseColor("#FFFFFF"));
        tvFindItem.setTextSize(25);
        tvFindItem.setGravity(Gravity.CENTER);
        tvFindItem.setTextAppearance(ScanScreenActivity.this, R.style.boldText);
        tvFindItem.setLayoutParams(tvFindItemParams);

        textLayout.addView(tvFindItem);

        upperLayout.addView(textLayout, textLayoutParam);
        overLay.addView(upperLayout, upperLayoutParam);

        RelativeLayout  bottomLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams bottomLayoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                150);
        bottomLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomLayout.setBackground(new ColorDrawable(Color.parseColor("#DFDFDF")));

        LinearLayout llButtonContainer = new LinearLayout(this);
        LinearLayout.LayoutParams llButtonContainerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        llButtonContainer.setOrientation(LinearLayout.HORIZONTAL);
        llButtonContainer.setGravity(Gravity.CENTER);

        llBtnFindArticleLayout = new LinearLayout(this);
        LinearLayout.LayoutParams llBtnFindLayoutParam =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 65);
        llBtnFindLayoutParam.setMargins(0, 0, 3, 0);
        llBtnFindArticleLayout.setOrientation(LinearLayout.HORIZONTAL);
        llBtnFindArticleLayout.setGravity(Gravity.CENTER);
        llBtnFindArticleLayout.setClickable(true);
        _themeSetter.setButtonColor(llBtnFindArticleLayout);

        TextView tvFinfArtCode = new TextView(this);
        TableRow.LayoutParams tvFinfArtCodeParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tvFinfArtCode.setText("    FIND BY ARTICLE NO.    ");
        tvFinfArtCode.setTextColor(Color.parseColor("#FFFFFF"));
        tvFinfArtCode.setTextSize(12);
        tvFinfArtCode.setGravity(Gravity.CENTER);
        tvFinfArtCode.setLayoutParams(tvFinfArtCodeParams);

        llBtnFindArticleLayout.addView(tvFinfArtCode);

        llButtonContainer.addView(llBtnFindArticleLayout, llBtnFindLayoutParam);


        llBtnFindCategoryLayout = new LinearLayout(this);
        LinearLayout.LayoutParams llBtnFindCategoryLayoutParam =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 65);
        llBtnFindCategoryLayoutParam.setMargins(3, 0, 0, 0);
        llBtnFindCategoryLayout.setOrientation(LinearLayout.HORIZONTAL);
        llBtnFindCategoryLayout.setGravity(Gravity.CENTER);
        llBtnFindCategoryLayout.setClickable(true);
        _themeSetter.setButtonColor(llBtnFindCategoryLayout);

        TextView tvFindCatCode = new TextView(this);
        TableRow.LayoutParams tvFindCatCodeParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        tvFindCatCode.setText("    FIND BY CATEGORY    ");
        tvFindCatCode.setTextColor(Color.parseColor("#FFFFFF"));
        tvFindCatCode.setTextSize(12);
        tvFindCatCode.setGravity(Gravity.CENTER);
        tvFindCatCode.setLayoutParams(tvFindCatCodeParams);

        llBtnFindCategoryLayout.addView(tvFindCatCode);

        llButtonContainer.addView(llBtnFindCategoryLayout, llBtnFindCategoryLayoutParam);


        bottomLayout.addView(llButtonContainer, llButtonContainerParam);
        overLay.addView(bottomLayout, bottomLayoutParam);
    }
}
