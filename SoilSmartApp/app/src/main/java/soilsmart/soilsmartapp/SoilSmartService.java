package soilsmart.soilsmartapp;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SoilSmartApp
 * Created by Ricardo Morones & Jesus Vega on 2/28/16.
 */
public class SoilSmartService implements ISoilSmartService, IAuthenticateUser {

    private String async_result;
    private String token;
    private String register_user;
    private UserLocalStore userLocalStore;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:Hello12", "bar@example.com:World12"
    };

    private SoilSmartService() {
        userLocalStore = null;
    }

    private static class SingletonHolder {
        public static final SoilSmartService instance = new SoilSmartService();
    }

    public static SoilSmartService getInstance() {
        return SingletonHolder.instance;
    }

    public void setUserLocalStore(UserLocalStore userLocalStore) {
        if (this.userLocalStore == null)
            this.userLocalStore = userLocalStore;
    }
    //grant_type=password&username=pmarcelo%40gmail.com&password=Password5!
    @Override
    public boolean authenticate(User user) {
        String credentials;
        credentials = "grant_type=password&username=" +user.getEmail() + "&password=" + user.getPasswordHash();
        new getLogin().execute("http://alphasoilsmart.azurewebsites.net/token", credentials, "POST");
        //Log.w("myApp", async_result);
        try {
            JSONObject tokn = new JSONObject(async_result);
            async_result = null;
            token = tokn.getString("access_token");
            userLocalStore.storeToken(token);
            //Log.w("myApp", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token != null;
    }

    @Override
    public List<SoilSmartNode> getNodes(final User user) {
        final String API_URL = "http://alphasoilsmart.azurewebsites.net/api/Nodes?username=" + user.getEmail();
        HttpURLConnection urlConnection = null;
        final StringBuilder result = new StringBuilder();
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(false);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Fiddler");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + userLocalStore.getToken());

            int status = urlConnection.getResponseCode();
            try {
                Log.w("myAPP", String.valueOf(API_URL));
                if (status != HttpURLConnection.HTTP_OK) {

                    urlConnection.disconnect();
                    return null;
                }
                final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONArray jsonResponse = new JSONArray(result.toString());
                List<NodeDTO> myList = new ArrayList<>(jsonResponse.length());
                for (int i =0; i < jsonResponse.length(); ++i) {
                    NodeDTO nodeDTO = new NodeDTO();
                    JSONObject node = jsonResponse.getJSONObject(i);
                    nodeDTO.setId(node.getString("Id"));
                    nodeDTO.setLatitude(node.getDouble("Latitude"));
                    nodeDTO.setLongitude(node.getDouble("Longitude"));
                    nodeDTO.setProductKey(node.getString("ProductKey"));
                    nodeDTO.setZone(node.getString("Zone"));
                    JSONArray jsonDates = node.getJSONArray("Dates");
                    List<Date> dateList = new ArrayList<>(jsonDates.length());
                    dateList.add(new Date());
                    Date[] dates = new Date[dateList.size()];
                    dates[0] = dateList.get(0);
                    nodeDTO.setDates(dates);
                    JSONArray values1 =  node.getJSONArray("Level_1");
                    double[] one = new double[values1.length()];
                    for (int j = 0; j < values1.length(); ++j) {
                        one[j] = values1.getDouble(j);

                    }
                    nodeDTO.setLevel_1(one);
                    JSONArray values2 =  node.getJSONArray("Level_2");
                    double[] two = new double[values2.length()];
                    for (int j = 0; j < values2.length(); ++j) {
                        two[j] = values2.getDouble(j);
                    }
                    nodeDTO.setLevel_2(two);
                    JSONArray values3 =  node.getJSONArray("Level_3");
                    double[] three = new double[values3.length()];
                    for (int j = 0; j < values3.length(); ++j) {
                        three[j] = values3.getDouble(j);
                    }
                    nodeDTO.setLevel_3(three);
                    myList.add(nodeDTO);
                }
                return convertNodes(myList);
            }catch(IOException ex){
                throw ex;
            }
        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection !=  null)
                urlConnection.disconnect();
        }

        return null;
    }

    private List<SoilSmartNode> convertNodes(List<NodeDTO> list) {
        List<SoilSmartNode> retval = new ArrayList<>(list.size());
        for (NodeDTO node : list) {
            SoilSmartNode soilSmartNode =
                    new SoilSmartNode(node.getId(), node.getZone(),
                    node.getLatitude(), node.getLongitude(), node.getDates()[0],
                    node.getLevel_1(), node.getLevel_2(), node.getLevel_3());
            retval.add(soilSmartNode);
        }
        return retval;
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
        //Log.w("myApp", register_user);

        new getLogin().execute("http://alphasoilsmart.azurewebsites.net/api/Account/Register", register_user, "POST");
        if (async_result == "success") {
            async_result = null;
            return true;
        }
        else{
            async_result = null;
            return false;
        }
    }

    @Override
    public JSONArray getIrrigation(final User user) {
        final String API_URL = "http://alphasoilsmart.azurewebsites.net/api/IrrigationControls";
        HttpURLConnection urlConnection = null;
        final StringBuilder result = new StringBuilder();
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(false);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Fiddler");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + userLocalStore.getToken());

            int status = urlConnection.getResponseCode();
            try {
                Log.w("myAPP", String.valueOf(API_URL));
                if (status != HttpURLConnection.HTTP_OK) {

                    urlConnection.disconnect();
                    return null;
                }
                final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONArray jsonResponse = new JSONArray(result.toString());
                //for (int i =0; i < jsonResponse.length(); ++i) {
                //}
                //do my own return here
                return jsonResponse;
            }catch(IOException ex){
                throw ex;
            }
        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection !=  null)
                urlConnection.disconnect();
        }

        return null;
    }

    @Override
    public void postIrrigate(User user){

    }

    @Override
    public void postForceOff(User user, String str){
        final String API_URL = "http://alphasoilsmart.azurewebsites.net/api/IrrigationControls/forceoff?nodeId=" + str;
        HttpURLConnection urlConnection = null;
        //final StringBuilder result = new StringBuilder();
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(false);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("User-Agent", "Fiddler");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + userLocalStore.getToken());

            int status = urlConnection.getResponseCode();
            Log.w("myAPP", String.valueOf(API_URL));
            if (status != HttpURLConnection.HTTP_OK) {

                urlConnection.disconnect();
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection !=  null)
                urlConnection.disconnect();
        }

    }

    public class getLogin extends AsyncTask<String, String, String> {

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
                urlConnection.setRequestMethod(args[2]);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                if(token != null){
                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                }


                OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
                os.write(args[1]);
                os.close();


                try {
                    //Log.w("myAPP", String.valueOf(urlConnection.getResponseCode()));
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
            //Log.w("myApp", result);
            async_result = result;
            //Do something with the JSON string

        }

    }

    @Override
    public boolean isLeakageDetected(final User user) {
        final String API_URL = "http://alphasoilsmart.azurewebsites.net/api/leakage?username=" + user.getEmail();
        HttpURLConnection urlConnection = null;
        final StringBuilder result = new StringBuilder();
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(false);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Fiddler");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + userLocalStore.getToken());

            int status = urlConnection.getResponseCode();
            try {
                Log.w("myAPP", String.valueOf(API_URL));
                if (status != HttpURLConnection.HTTP_OK) {

                    urlConnection.disconnect();
                    return false;
                }
                final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return Boolean.parseBoolean(result.toString());
            }catch(IOException ex){
                throw ex;
            }
        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection !=  null)
                urlConnection.disconnect();
        }
        return false;
    }

}
