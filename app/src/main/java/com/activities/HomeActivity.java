package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.count.R;
import com.fragment.HomeFragment;
import com.service.GetArticleNoService;
import com.service.GetCategoryService;
import com.service.GetPhysicleCountService;
import com.service.GetScheduleService;
import com.utility.Constant;
import com.utility.DbAdapter;
import com.utility.Pref;
import com.utility.ThemeSetter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends ActionBarActivity {

    private ActionBar actionBar;
    private ThemeSetter _themeSetter;

    private DrawerLayout navDrawer;
    private ListView lvMenuList;
    private String[] menuItem;
    private ActionBarDrawerToggle drawerToggle;
    private boolean mSlideState = false;
    private ImageView ivMenu;
    private String deviceId;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private Pref _pref;
    private DbAdapter dbAdapter;

    private Locale myLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();
        loadLocale();

        ivMenu = _themeSetter.setHeaderTheme(actionBar, getResources().getString(R.string.str_count) , R.drawable.menu_icon);

        lvMenuList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_item_layout,
                R.id.tvMenuIem, menuItem));
        navDrawer.setDrawerListener(drawerToggle);



        try {
            if (!_pref.getSession("current_date").equals("")) {

                Date dateToday = dateFormat.parse(Constant.CURRENT_DATE);
                Date dateStored = dateFormat.parse(_pref.getSession("current_date"));

                if(dateToday.compareTo(dateStored) > 0){

                    _pref.setSession("current_date", Constant.CURRENT_DATE);
                    dbAdapter.open();
                    dbAdapter.deleteAllRecord();
                    dbAdapter.close();
                }

            } else {

                _pref.setSession("current_date", Constant.CURRENT_DATE);

            }

        } catch (Exception e){
            e.printStackTrace();
        }

        onClick();
        loadHomeFragment();

        startService(new Intent(HomeActivity.this, GetCategoryService.class));
        startService(new Intent(HomeActivity.this, GetArticleNoService.class));
        startService(new Intent(HomeActivity.this, GetPhysicleCountService.class));
        //------ Checking Json Data in Memory----//
        String sdFile = _pref.getSession(Constant.JSON_SCH_CATEGORY_FILE_NAME);
        if(sdFile.isEmpty()) {
            startService(new Intent(HomeActivity.this, GetScheduleService.class));
        }


    }


    private void initialize(){

        _pref = new Pref(HomeActivity.this);
        _themeSetter = new ThemeSetter(HomeActivity.this);
        dbAdapter = new DbAdapter(HomeActivity.this);
        actionBar = getSupportActionBar();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        navDrawer = (DrawerLayout) findViewById(R.id.navDrawer);
        lvMenuList = (ListView) findViewById(R.id.lvMenuList);
        menuItem = getResources().getStringArray(R.array.menu_item);

        drawerToggle = new ActionBarDrawerToggle(this, navDrawer,
                R.drawable.menu_icon_white, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                mSlideState = true;
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mSlideState = false;
                super.onDrawerClosed(drawerView);
            }
        };
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void onClick(){

        lvMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setFragment(position);
            }
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSlideState){
                    navDrawer.closeDrawers();
                } else {
                    navDrawer.openDrawer(Gravity.LEFT);
                }
            }
        });

    }

    private void setFragment(int position){
        switch (position){
            case 0:
                break;
            case 1:
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case 2:
                startActivity(new Intent(HomeActivity.this, LanguageActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case 3:
                _pref.saveAccessToken("");
                _pref.saveName("");
                _pref.saveMobileNo("");

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

                break;
        }
       navDrawer.closeDrawers();
    }

    private void loadHomeFragment(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HomeFragment fragment = new HomeFragment();
        transaction.replace(R.id.fvContainer, fragment);
        transaction.commit();

    }
}
