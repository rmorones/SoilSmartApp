package soilsmart.soilsmartapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    UserRegisterTask mAuthTask = null;

    AutoCompleteTextView mEmailView;
    EditText mPasswordView;
    EditText mProductkeyView;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Button blogin = (Button) findViewById(R.id.register_button);
        final Button breturn = (Button) findViewById(R.id.back_to_login_button);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mProductkeyView = (EditText) findViewById(R.id.product_key);

        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                attemptRegister();
            }
        });

        breturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                launchActivity(LoginActivity.class);
                finish();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        mRegisterFormView = findViewById(R.id.registration_form);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int id, final KeyEvent keyEvent) {
                if (isPasswordValid(v.getText().toString())) {
                    mProductkeyView.requestFocus();
                    return true;
                } else {
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                }
                return false;
            }
        });

        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if (isEmailValid(v.getText().toString())) {
                    mPasswordView.requestFocus();
                    return true;
                } else {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                }
                return false;
            }
        });

        mProductkeyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int id, final KeyEvent keyEvent) {
                if (isValidKey(v.getText().toString())) {
                    blogin.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.matches(getString(R.string.email_regex));
    }

    private boolean isPasswordValid(String password) {
        return password.matches(getString(R.string.password_regex));
    }

    private boolean isValidKey(String key) {
        return key.length() > 10;
    }

    private void attemptRegister() {
        if (mAuthTask != null)
            return;

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the registration attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String productKey = mProductkeyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid product key, if the user entered one.
        if (TextUtils.isEmpty(productKey)) {
            mProductkeyView.setError(getString(R.string.error_field_required));
            focusView = mProductkeyView;
            cancel = true;
        }

        if (!isValidKey(productKey)) {
            showMessage(null);
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuthTask = new UserRegisterTask(new User(email, password), productKey);
            mAuthTask.execute((Void) null);
        }
    }

    private void launchActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    void showMessage(Object obj) {
        if (obj != null)
            Toast.makeText(this,
                    R.string.register_success,
                    Toast.LENGTH_LONG).show();
        else
            Snackbar.make(mRegisterFormView,
                    getString(R.string.registration_failed),
                    Snackbar.LENGTH_LONG).show();
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, User> {

        private final User user;
        private final String key;

        UserRegisterTask(User user, String key) {
            this.user = user;
            this.key = key;
        }

        @Override
        protected User doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            final SoilSmartService soilSmartService = SoilSmartService.getInstance();
            try {
                if (soilSmartService.registerUser(user, key)) {
                    return user;
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final User user) {
            mAuthTask = null;
            showProgress(false);

            if (user != null) {
                showMessage(user);
                launchActivity(LoginActivity.class);
                finish();
            } else {
                showMessage(null);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
