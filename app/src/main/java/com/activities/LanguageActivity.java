package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.count.R;
import com.utility.ThemeSetter;

import java.util.Locale;

/**
 * Created by Avishek on 6/5/2015.
 */
public class LanguageActivity extends ActionBarActivity implements View.OnClickListener {

    ActionBar actionBar;
    ImageView ivBack;
    ThemeSetter _themeSetter;

    private LinearLayout llScheduleMainLayout;

    private TextView txt_hello;
    private Button btn_en, btn_ru, btn_fr, btn_de;
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        initialize();

        ivBack = _themeSetter.setHeaderTheme(actionBar, "CHANGE LANGUAGE", R.drawable.previous_icon);
        _themeSetter.setBodyColor(llScheduleMainLayout);

        this.btn_en.setOnClickListener(this);
        this.btn_ru.setOnClickListener(this);
        this.btn_fr.setOnClickListener(this);
        this.btn_de.setOnClickListener(this);

        loadLocale();
        onclick();
    }

    private void initialize(){

        actionBar = getSupportActionBar();
        _themeSetter = new ThemeSetter(LanguageActivity.this);
        llScheduleMainLayout = (LinearLayout) findViewById(R.id.llScheduleMainLayout);

        this.txt_hello = (TextView)findViewById(R.id.txt_hello);
        this.btn_en = (Button)findViewById(R.id.btn_en);
        this.btn_ru = (Button)findViewById(R.id.btn_ru);
        this.btn_fr = (Button)findViewById(R.id.btn_fr);
        this.btn_de = (Button)findViewById(R.id.btn_de);

    }

    private void onclick() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(LanguageActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();

    }

    public void loadLocale()
    {

        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString("Language", "");
        changeLang(language);
    }

    public void saveLocale(String lang)
    {

        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Language", lang);
        editor.commit();
    }



    private void updateTexts()
    {
        txt_hello.setText(R.string.str_count);
        btn_en.setText(R.string.btn_en);
        btn_ru.setText(R.string.btn_ru);
        btn_fr.setText(R.string.btn_fr);
        btn_de.setText(R.string.btn_de);
    }

    @Override
    public void onClick(View v) {
        String lang = "en";
        switch (v.getId()) {
            case R.id.btn_en:
                lang = "en";
                break;
            case R.id.btn_ru:
                lang = "ru";
                break;
            case R.id.btn_de:
                lang = "de";
                break;
            case R.id.btn_fr:
                lang = "fr";
                break;
            default:
                break;
        }
        changeLang(lang);
    }

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null){
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}

