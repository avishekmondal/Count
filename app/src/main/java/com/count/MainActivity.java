package com.count;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fragment.HomeFragment;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout navDrawer;
    private ListView lvMenuList;
    private String[] menuItem;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        lvMenuList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_item_layout,
                R.id.tvMenuIem, menuItem));
        navDrawer.setDrawerListener(drawerToggle);
        onClick();
        loadHomeFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private void initialize(){
        navDrawer = (DrawerLayout) findViewById(R.id.navDrawer);
        lvMenuList = (ListView) findViewById(R.id.lvMenuList);
        menuItem = getResources().getStringArray(R.array.menu_item);

        drawerToggle = new ActionBarDrawerToggle(this, navDrawer,
                new Toolbar(this), R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
    }

    private void onClick(){
        lvMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });
    }

    private void setFragment(int position){
        switch (position){
            case 1:
                loadHomeFragment();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    private void loadHomeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HomeFragment fragment = new HomeFragment();
        transaction.replace(R.id.fvContainer, fragment);
        transaction.commit();
        navDrawer.closeDrawers();
    }
}
