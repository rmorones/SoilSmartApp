package soilsmart.soilsmartapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import soilsmart.soilsmartapp.R;

public class BaseMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle presses on the action bar items
        // View v = findViewById(R.id.f);
        switch (item.getItemId()) {
            case R.id.locations_activity:
                //action
                launchActivity(NodeLocationsActivity.class);
                return true;

            case R.id.all_nodes_activity:
                //action
                launchActivity(AllNodesActivity.class);
                return true;

            case R.id.leakage_activity:
                //action
                return true;

            case R.id.settings_activity:
                //action
                launchActivity(SettingsActivity.class);
                return true;

            case R.id.ar_activity:
                //action
                launchActivity(ARActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void launchActivity(final Class clazz) {
        startActivity(new Intent(this, clazz));
        finish();
    }

}
