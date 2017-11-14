package com.project.beachnav.beachnav.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.project.beachnav.beachnav.R;
import com.project.beachnav.beachnav.fragment.BNMapFragment;
import com.project.beachnav.beachnav.fragment.HelpFragment;
import com.project.beachnav.beachnav.fragment.ItemFragment;
import com.project.beachnav.beachnav.fragment.SettingsFragment;
import com.project.beachnav.beachnav.other.SearchActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] activityTitles;
    private static int navItemIndex = 0;

    private static final String TAG_HOME = "map", TAG_ITEM = "item",
            TAG_SETTINGS = "settings", TAG_HELP = "help";
    private static String CURRENT_TAG =  TAG_HOME;

    private boolean shouldLoadHomeFragOnBackPress = true;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.appbar_toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
//        drawer.addDrawerListener(mToggle);
        mToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        } if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
        }   }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (navItemIndex == 0) getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.search_location:
//               gets the node out, then makes that into a marker and puts it into mapFragment
                SearchActivity searchActivity = new SearchActivity();
//                (BNMapFragment)currentFragment.dropLocationMarker(searchActivity.getLocation());
                return true;
            case R.id.route_location:
                Toast.makeText(this, "Route to Location!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.current_location:
                Toast.makeText(this, "Current Location!", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (item == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        } else {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
            } else if (id == R.id.nav_item) {
                navItemIndex = 1;
                CURRENT_TAG = TAG_ITEM;
            } else if (id == R.id.nav_settings) {
                navItemIndex = 2;
                CURRENT_TAG = TAG_SETTINGS;
            } else if (id == R.id.nav_help) {
                navItemIndex = 3;
                CURRENT_TAG = TAG_HELP;
        }   }

        loadHomeFragment();

        return true;
    }

    private void loadHomeFragment() {

        selectNavMenu(); // selecting appropriate nav menu item
        setToolbarTitle(); // set toolbar title

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fT = getSupportFragmentManager().beginTransaction();
                fT.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fT.replace(R.id.frame, fragment, CURRENT_TAG);
                fT.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers(); //Closing drawer on item click
        invalidateOptionsMenu(); // refresh toolbar menu
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }


    private static Fragment currentFragment = null;

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0: currentFragment = new BNMapFragment();
            case 1: currentFragment = new ItemFragment();
            case 2: currentFragment = new SettingsFragment();
            case 3: currentFragment = new HelpFragment();
            default: currentFragment = new BNMapFragment();
        } return currentFragment;
    }
}
