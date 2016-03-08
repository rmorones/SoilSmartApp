package soilsmart.soilsmartapp.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.User;
import soilsmart.soilsmartapp.UserLocalStore;

public class SettingsActivity extends BaseMenuActivity {

    private UserLocalStore userLocalStore;
    private User user;
    View changePassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }
        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        final LinearLayout logout = (LinearLayout) findViewById(R.id.log_out_row);
        final LinearLayout changepass = (LinearLayout) findViewById(R.id.change_pass_row);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassword();
            }
        });

    }

    private void LogOut() {
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TextView confirmLogout = new TextView(this);
        confirmLogout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        confirmLogout.setText("Are you sure you want to log out?");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });
        builder.show();

    }

    private void ChangePassword() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builderResult = new AlertDialog.Builder(this);
        LayoutInflater factory;

        factory = LayoutInflater.from(this);
        changePassView = factory.inflate(R.layout.dialog_change_password, null);

        builder.setTitle("Change Password");
        builder.setView(changePassView);

        final TextView setPassResult = new TextView(this);
        setPassResult.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

        builderResult.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText cp = (EditText) changePassView.findViewById(R.id.curr_pass);
                EditText np = (EditText) changePassView.findViewById(R.id.pass);
                EditText npr = (EditText) changePassView.findViewById(R.id.retype_pass);
                TextView result = (TextView) changePassView.findViewById(R.id.change_pass_result);
                String currPass = cp.getText().toString();
                Log.d("currPass",currPass);
                String newPass = np.getText().toString();
                String newPassRetype = npr.getText().toString();

                if (user.getPasswordHash().contentEquals(currPass)) {
                    if (newPass.contentEquals(newPassRetype)) {
                        // SoilSmartService method for new password
                        if (newPass.matches(getString(R.string.password_regex))) {
                            user.setPasswordHash(newPass);
                            setPassResult.setText("Password Incorrect.");
                            builderResult.setView(setPassResult);
                        } else {
                            setPassResult.setText(getString(R.string.error_invalid_password));
                            builderResult.setView(setPassResult);
                        }
                    } else {
                        setPassResult.setText("Password mismatch, please retry.");
                        builderResult.setView(setPassResult);
                    }
                } else {
                    setPassResult.setText("Password Incorrect.");
                    builderResult.setView(setPassResult);
                }

                dialog.dismiss();
                builderResult.show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.show();
    }
}
