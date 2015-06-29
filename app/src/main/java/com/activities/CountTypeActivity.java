package com.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bean.CountResultItem;
import com.bean.PhysicalCountResultItem;
import com.count.R;
import com.utility.DbAdapter;
import com.utility.Pref;
import com.utility.ThemeSetter;

import java.util.ArrayList;

public class CountTypeActivity extends ActionBarActivity {

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;
    private ImageView ivBack, ivScanCountStatus, ivPhysicalCountStatus;
    private RelativeLayout rlScanCount, rlPhysicalCount, rlGuideLines;
    private LinearLayout llCountTypeMainLayout, llInstruction;
    private Pref _pref;
    private TextView tvScanInstruction;

    String current_surveyid = "";

    private Animation moveFromUp, moveFromDown;

    private DbAdapter dbAdapter;
    private ArrayList<CountResultItem> scanItems;
    private ArrayList<PhysicalCountResultItem> physicalItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_type);

        initialize();

        rlPhysicalCount.setVisibility(View.INVISIBLE);
        rlPhysicalCount.setAnimation(moveFromUp);

        rlScanCount.setAnimation(moveFromUp);
        llInstruction.setAnimation(moveFromDown);

        tvScanInstruction.setText(getIntent().getExtras().getString("instruction"));
        ivBack = _themeSetter.setHeaderTheme(actionBar, "FULL SURVEY", R.drawable.previous_icon);
        _themeSetter.setBodyColor(llCountTypeMainLayout);
        _themeSetter.setBodyColor(rlGuideLines);

        onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();

        current_surveyid = _pref.getSession("current_surveyid");

        /*if(_pref.getSession("complete_full_scan_" + current_surveyid).equals("1")){
            ivScanCountStatus.setImageResource(R.drawable.workonprocess_icon);
        } else if(_pref.getSession("complete_full_scan_" + current_surveyid).equals("2")){
            rlScanCount.setEnabled(false);
            rlScanCount.setBackground(getResources()
                    .getDrawable(R.drawable.shadow_background_complete));
            ivScanCountStatus.setImageResource(R.drawable.tick_icon);
        } else if(_pref.getSession("complete_full_scan_" + current_surveyid).equals("")) {
            rlScanCount.setEnabled(true);
            rlScanCount.setBackground(getResources()
                    .getDrawable(R.drawable.shadow_background_remain));
            ivScanCountStatus.setImageResource(R.drawable.cross_icon);
        }

        if(_pref.getSession("complete_full_physical_" + current_surveyid).equals("1")){
            ivPhysicalCountStatus.setImageResource(R.drawable.workonprocess_icon);
        } else if(_pref.getSession("complete_full_physical_" + current_surveyid).equals("2")){
            rlPhysicalCount.setEnabled(false);
            rlPhysicalCount.setBackground(getResources()
                    .getDrawable(R.drawable.shadow_background_complete));
            ivPhysicalCountStatus.setImageResource(R.drawable.tick_icon);
        } else if(_pref.getSession("complete_full_physical_" + current_surveyid).equals("")) {
            rlPhysicalCount.setEnabled(true);
            rlPhysicalCount.setBackground(getResources()
                    .getDrawable(R.drawable.shadow_background_remain));
            ivPhysicalCountStatus.setImageResource(R.drawable.cross_icon);
        }*/

        rlScanCount.setEnabled(true);
        rlScanCount.setBackground(getResources()
                .getDrawable(R.drawable.shadow_background_remain));
        ivScanCountStatus.setImageResource(R.drawable.cross_icon);

        rlPhysicalCount.setEnabled(true);
        rlPhysicalCount.setBackground(getResources()
                .getDrawable(R.drawable.shadow_background_remain));
        ivPhysicalCountStatus.setImageResource(R.drawable.cross_icon);
    }

    private void initialize(){

        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(CountTypeActivity.this);
        _pref = new Pref(CountTypeActivity.this);

        rlScanCount = (RelativeLayout) findViewById(R.id.rlScanCount);
        rlPhysicalCount = (RelativeLayout) findViewById(R.id.rlPhysicalCount);
        llCountTypeMainLayout = (LinearLayout) findViewById(R.id.llCountTypeMainLayout);
        rlGuideLines = (RelativeLayout) findViewById(R.id.rlGuideLines);
        ivScanCountStatus = (ImageView) findViewById(R.id.ivScanCountStatus);
        ivPhysicalCountStatus = (ImageView) findViewById(R.id.ivPhysicalCountStatus);
        llInstruction = (LinearLayout) findViewById(R.id.llInstruction);
        tvScanInstruction= (TextView) findViewById(R.id.tvScanInstruction);

        moveFromUp = AnimationUtils.loadAnimation(CountTypeActivity.this, R.anim.move_down);
        moveFromDown = AnimationUtils.loadAnimation(CountTypeActivity.this, R.anim.move_up);

    }

    private void onClick(){

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rlScanCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbAdapter = new DbAdapter(CountTypeActivity.this);
                scanItems = new ArrayList<CountResultItem>();
                dbAdapter.open();
                scanItems = dbAdapter.getRecords();
                dbAdapter.close();

                if(scanItems.size()> 0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(CountTypeActivity.this);
                    builder.setTitle("Scan Status");
                    builder.setMessage("Scanned Records available!!")
                            .setCancelable(false)
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    gotoScanCount();

                                }
                            })
                            .setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(CountTypeActivity.this);
                                    builder.setTitle("Delete Confirmation");
                                    builder.setMessage("Deleting Records. Are you sure??")
                                            .setCancelable(false)
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    dbAdapter.open();
                                                    dbAdapter.deleteRecord(current_surveyid);
                                                    dbAdapter.close();

                                                    _pref.setSession("complete_full_scan_" + current_surveyid, "");
                                                    gotoScanCount();

                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();

                                                }
                                            });
                                    builder.create();
                                    builder.show();

                                }
                            });
                    builder.create();
                    builder.show();

                }

                else{

                    gotoScanCount();

                }


                }

        });

        rlPhysicalCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbAdapter = new DbAdapter(CountTypeActivity.this);
                physicalItems = new ArrayList<PhysicalCountResultItem>();
                dbAdapter.open();
                physicalItems = dbAdapter.getPhysicalCountRecords();
                dbAdapter.close();

                if(physicalItems.size()> 0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(CountTypeActivity.this);
                    builder.setTitle("Scan Status");
                    builder.setMessage("Scanned Records available!!")
                            .setCancelable(false)
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    gotoPhysicalCount();

                                }
                            })
                            .setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(CountTypeActivity.this);
                                    builder.setTitle("Delete Confirmation");
                                    builder.setMessage("Deleting Records. Are you sure??")
                                            .setCancelable(false)
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    dbAdapter.open();
                                                    dbAdapter.deleteRecordPhysicalCount(current_surveyid);
                                                    dbAdapter.close();

                                                    _pref.setSession("complete_full_physical_" + current_surveyid, "");
                                                    gotoPhysicalCount();

                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();

                                                }
                                            });
                                    builder.create();
                                    builder.show();
                                }
                            });
                    builder.create();
                    builder.show();

                }

                else{

                    gotoPhysicalCount();

                }

            }
        });
    }

    public void gotoScanCount(){

        _pref.setSession("selected_count_type", "scan");
        Intent intent = new Intent(CountTypeActivity.this, ScanScreenActivity.class);
        intent.putExtra("scan_type", "count");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void gotoPhysicalCount(){

        _pref.setSession("selected_count_type", "physical");
        startActivity(new Intent(CountTypeActivity.this, PhysicalCountAddActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count_type, menu);
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
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(CountTypeActivity.this, ScheduleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();

    }

}
