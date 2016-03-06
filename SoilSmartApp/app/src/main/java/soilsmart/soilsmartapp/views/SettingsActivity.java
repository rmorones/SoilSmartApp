package soilsmart.soilsmartapp.views;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.UserLocalStore;

/**
 * Created by jesus on 3/4/16.
 */
public class SettingsActivity extends BaseMenuActivity {
    private UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.all_nodes);

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }
        userLocalStore = new UserLocalStore(this);
    }
}
