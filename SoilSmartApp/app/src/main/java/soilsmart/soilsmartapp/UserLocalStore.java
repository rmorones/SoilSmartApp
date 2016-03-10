package soilsmart.soilsmartapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * SoilSmartApp
 * Created by Ricardo Morones on 2/27/16.
 */
public class UserLocalStore {
    final static public String SP_NAME = "userDetails";
    final SharedPreferences userLocalDatabase;
    final Gson gson;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
        gson = new Gson();
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("email", user.getEmail());
        spEditor.putString("password", user.getPasswordHash());
        spEditor.putString("nodes", gson.toJson(user.getNodes()));
        spEditor.apply();
    }

    public User getLoggedInUser() {
        final String email = userLocalDatabase.getString("email", "");
        final String password = userLocalDatabase.getString("password", "");
        final String nodesJson = userLocalDatabase.getString("nodes", "");
        final Type type = new TypeToken<ArrayList<SoilSmartNode>>() {}.getType();
        ArrayList<SoilSmartNode> nodes = gson.fromJson(nodesJson, type);
        return new User(email, password, nodes);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();
    }

    public void storeToken(String token) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("token", token);
        spEditor.apply();
    }

    public String getToken() {
        return userLocalDatabase.getString("token", "");
    }

    public boolean getUserLoggedIn() {
       return userLocalDatabase.getBoolean("loggedIn", false);
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
