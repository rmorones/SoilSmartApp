package soilsmart.soilsmartapp.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import soilsmart.soilsmartapp.R;

public class BaseMenuActivity extends AppCompatActivity {

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
            //user should only be able to get to NodeDetailActivity by selecting a node on the map
            /*case R.id.node_info_activity:
                //action
                launchActivity(NodeDetailActivity.class);
                return true;*/
            case R.id.all_nodes_activity:
                //action
                launchActivity(AllNodesActivity.class);
                return true;
            case R.id.leakage_activity:
                //action
                return true;
            case R.id.settings_activity:
                //action
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
