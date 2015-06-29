package com.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.count.R;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.utility.ThemeSetter;

/**
 * Created by Avishek on 6/1/2015.
 */
public class WebViewActivity extends ActionBarActivity {

    ActionBar actionBar;
    ImageView ivBack;
    ThemeSetter _themeSetter;

    private LinearLayout llScheduleMainLayout;
    private ProgressWheel pwSchedulerPage;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "ADMIN", R.drawable.previous_icon);
        _themeSetter.setBodyColor(llScheduleMainLayout);

        startWebView("http://count.bluehorse.in/client");

        onclick();

    }

    private void initialize(){

        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(WebViewActivity.this);

        llScheduleMainLayout = (LinearLayout) findViewById(R.id.llScheduleMainLayout);
        pwSchedulerPage = (ProgressWheel) findViewById(R.id.pwSchedulerPage);
        webView = (WebView) findViewById(R.id.webView);

    }

    private void onclick() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void startWebView(String url) {

        webView.setWebViewClient(new WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {

                //pwSchedulerPage.setVisibility(View.VISIBLE);
                //webView.setVisibility(View.GONE);

            }
            public void onPageFinished(WebView view, String url) {
                try{

                    pwSchedulerPage.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);

                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);

    }


    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);
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

}
