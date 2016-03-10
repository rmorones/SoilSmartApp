package soilsmart.soilsmartapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SoilSmartApp
 * Created by Ricardo Morones on 2/28/16.
 */
public class SoilSmartService implements ISoilSmartService, IAuthenticateUser {

    private String async_result;
    private String token;
    private String register_user;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:Hello12", "bar@example.com:World12"
    };

    private SoilSmartService() {}

    private static class SingletonHolder {
        public static final SoilSmartService instance = new SoilSmartService();
    }

    public static SoilSmartService getInstance() {
        return SingletonHolder.instance;
    }
    //grant_type=password&username=pmarcelo%40gmail.com&password=Password5!
    @Override
    public boolean authenticate(User user) {
        String credentials;
        credentials = "grant_type=password&username=" +user.getEmail() + "&password=" + user.getPasswordHash();
        new getData().execute("http://alphasoilsmart.azurewebsites.net/token", credentials);
        Log.w("myApp", async_result);
        try {
            JSONObject tokn = new JSONObject(async_result);
            token = tokn.getString("access_token");
            Log.w("myApp", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (token != null) {
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public List<SoilSmartNode> getNodes(final User user) {
        return null;
    }

    @Override
    public double[] getLastWeek(final long nodeId) {
        return new double[0];
    }

    @Override
    public double[] getLastMonth(final long nodeId) {
        return new double[0];
    }

    @Override
    public void appendPoint(final long nodeId, final Date date, final double value) {

    }

    @Override
    public boolean registerUser(final User user, final String key) {
        JSONObject newUser = new JSONObject();
        try {
            newUser.put("Email", user.getEmail());
            newUser.put("Password", user.getPasswordHash());
            newUser.put("ConfirmPassword", user.getPasswordHash());
            newUser.put("ProductKey", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            register_user = newUser.toString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("myApp", register_user);

        new getData().execute("http://alphasoilsmart.azurewebsites.net/api/Account/Register", register_user);
        if (async_result == "success") {
            return true;
        }
        else{
            return false;
        }
    }

    public class getData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                //urlConnection.setRequestProperty("Accept", "application/json; */*");
                //urlConnection.setRequestProperty("Content-Type", "application/json");
                //urlConnection.setRequestProperty("Content-Length", ""+args[1].getBytes().length);
                urlConnection.setChunkedStreamingMode(0);
                Log.w("myApp", args[1]);

                OutputStream os = urlConnection.getOutputStream();
                os.write(args[1].getBytes("UTF-8"));
                os.close();

                urlConnection.connect();
                Log.w("myApp", "CONNECTED");
                try {
                    Log.w("myAPP", String.valueOf(urlConnection.getResponseCode()));
                    if (urlConnection.getResponseCode() == 200 && args[0] == "http://alphasoilsmart.azurewebsites.net/api/Account/Register") {
                        return "success";
                    }
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }catch(IOException ex){
                    throw ex;
                }
            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.w("myApp", result);
            async_result = result;
            //Do something with the JSON string

        }

    }

}
