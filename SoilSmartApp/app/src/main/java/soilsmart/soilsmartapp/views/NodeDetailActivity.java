package soilsmart.soilsmartapp.views;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.UserLocalStore;

public class NodeDetailActivity extends BaseMenuActivity {

    private UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_detail);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }

        userLocalStore = new UserLocalStore(this);
    }

}
