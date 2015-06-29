package com.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.count.R;
import com.interfaces.BackgroundTaskInterface;
import com.utility.Constant;
import com.utility.NetworkConnectionCheck;
import com.utility.Pref;
import com.utility.RunBackgroundAsnk;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity implements BackgroundTaskInterface {

    LinearLayout llWelcome, llView1, llView2, llView3, llForm;
    TextView tvAuthenticate, tvAuthenticateOTP;
    EditText etUserId, etMobile, etPassword, etOTP;
    ProgressBar progressbar;
    Button btnSubmit, btnSubmitOTP, btnResendOTP, btnStart;

    String userId = "", mobile = "", password = "";

    Animation fade_in, fade_out, slide_in, slide_out, slide_in_up, slide_in_down;
    ProgressDialog pDialog;

    Pref _pref;
    private NetworkConnectionCheck connectionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initialize();
        onclick();
    }

    public void initialize(){

        _pref = new Pref(LoginActivity.this);
        connectionCheck = new NetworkConnectionCheck(LoginActivity.this);

        llWelcome = (LinearLayout) findViewById(R.id.llWelcome);
        llView1 = (LinearLayout) findViewById(R.id.llView1);
        llView2 = (LinearLayout) findViewById(R.id.llView2);
        llView3 = (LinearLayout) findViewById(R.id.llView3);
        llForm = (LinearLayout) findViewById(R.id.llForm);

        tvAuthenticate = (TextView) findViewById(R.id.tvAuthenticate);
        tvAuthenticateOTP = (TextView) findViewById(R.id.tvAuthenticateOTP);
        etUserId = (EditText) findViewById(R.id.etUserId);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etOTP = (EditText) findViewById(R.id.etOTP);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmitOTP = (Button) findViewById(R.id.btnSubmitOTP);
        btnResendOTP = (Button) findViewById(R.id.btnResendOTP);
        btnStart = (Button) findViewById(R.id.btnStart);

        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        slide_in = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        slide_out = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        slide_out.setFillAfter(true);
        slide_in_up = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        slide_in_down = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);

        llWelcome.startAnimation(slide_in_down);
        tvAuthenticate.startAnimation(fade_in);
        llForm.startAnimation(slide_in_up);

    }

    public void onclick(){

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId = etUserId.getText().toString();
                mobile = etMobile.getText().toString();
                password = etPassword.getText().toString();

                if(!userId.equalsIgnoreCase("") && !mobile.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {

                    if (connectionCheck.isNetworkAvailable()) {

                        String url = Constant.baseUrl + "login";

                        try {

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("userId", userId);
                            jsonObject.put("mobile_no", mobile);
                            jsonObject.put("password", password);

                            JSONObject data = new JSONObject();
                            data.put("data", jsonObject);

                            String jsonInput = data.toString();

                            RunBackgroundAsnk login = new RunBackgroundAsnk(
                                    LoginActivity.this);
                            login.taskInterface = LoginActivity.this;
                            login.execute(url, jsonInput);
                        }

                        catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    else{

                        connectionCheck.getNetworkActiveAlert().show();

                    }

                }

                else{

                    Toast.makeText(LoginActivity.this, "Required fields should not be left blank!!", Toast.LENGTH_SHORT).show();
                }



            }
        });

        btnSubmitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etOTP.getText().toString().equalsIgnoreCase("123")){

                    etOTP.startAnimation(fade_out);
                    etOTP.setVisibility(View.GONE);

                    progressbar.setVisibility(View.VISIBLE);
                    progressbar.startAnimation(fade_in);

                    tvAuthenticateOTP.setVisibility(View.VISIBLE);
                    tvAuthenticateOTP.startAnimation(fade_in);

                    btnSubmitOTP.setVisibility(View.GONE);

                    btnResendOTP.setVisibility(View.VISIBLE);
                    btnResendOTP.startAnimation(fade_in);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            llView2.clearAnimation();
                            llView2.startAnimation(fade_out);
                            llView2.setVisibility(View.GONE);

                            llView3.setVisibility(View.VISIBLE);
                            llView3.startAnimation(fade_in);

                        }
                    }, 5000);

                }
                else{

                    etOTP.setText("");
                    Toast.makeText(LoginActivity.this, "OTP disabled now. Type 123 as OTP", Toast.LENGTH_SHORT).show();

                }

            }
        });

        /*btnResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressbar.startAnimation(fade_out);
                progressbar.setVisibility(View.GONE);

                tvAuthenticateOTP.startAnimation(fade_out);
                tvAuthenticateOTP.setVisibility(View.GONE);

                btnResendOTP.startAnimation(fade_out);
                btnResendOTP.setVisibility(View.GONE);

                etOTP.setVisibility(View.VISIBLE);
                etOTP.startAnimation(fade_in);

                btnSubmitOTP.setVisibility(View.VISIBLE);
                btnResendOTP.startAnimation(fade_in);

            }
        });*/

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @Override
    public void onStarted() {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Authenticating...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

    }

    @Override
    public void onCompleted(String jsonStr) {

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

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

                        JSONObject userDetailsObj = dataObj.getJSONObject("userDetails");

                       _pref.saveAccessToken(dataObj.getString("accesstoken"));
                       _pref.saveName(userDetailsObj.getString("name"));
                       _pref.saveMobileNo(userDetailsObj.getString("mobileNo"));

                        llView1.clearAnimation();
                        llView1.startAnimation(slide_out);
                        llView1.setVisibility(View.GONE);

                        llView2.setVisibility(View.VISIBLE);
                        llView2.startAnimation(slide_in);

                    }
                    else{

                        Toast.makeText(LoginActivity.this, "Authentication Problem!! Please Try Again", Toast.LENGTH_LONG).show();

                        etUserId.setText("");
                        etMobile.setText("");
                        etPassword.setText("");

                    }
                }

                else{

                    Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();

                    etUserId.setText("");
                    etMobile.setText("");
                    etPassword.setText("");

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else{

            Toast.makeText(LoginActivity.this, "Something going wrong!! Please Try Again", Toast.LENGTH_LONG).show();

        }

    }
}
