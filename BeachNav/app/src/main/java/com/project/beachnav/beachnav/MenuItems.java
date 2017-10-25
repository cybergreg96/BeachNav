package com.project.beachnav.beachnav;

import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Austin Tao on 10/24/2017.
 */

public class MenuItems extends FragmentActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.search_location: return true;
            case R.id.settings: return true;
            case R.id.help: return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

}
