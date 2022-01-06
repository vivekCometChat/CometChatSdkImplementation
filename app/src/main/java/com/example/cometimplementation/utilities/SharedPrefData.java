package com.example.cometimplementation.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.models.UserPojo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefData {

    private static final String login = "LOGIN";
    private static final String login_uid = "uid";
    private static final String login_name = "name";
    private static final String contacts = "CONTACTS";
    private static final String contacts_details = "contact_details";
    private static final String users = "USERS";
    private static final String users_data = "users_data";

    public static void saveUserIdName(Context context, String id, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(login, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(login_uid, id);
        editor.putString(login_name, name);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(login, Context.MODE_PRIVATE);
        return sharedPreferences.getString(login_uid, "");
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(login, Context.MODE_PRIVATE);
        return sharedPreferences.getString(login_name, "");
    }

    public static void saveContacts(Context context, List<UserPojo> users) {
        Gson gson = new Gson();
        if (users != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(contacts, Context.MODE_PRIVATE);
            String jsonString = gson.toJson(users);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(contacts_details, jsonString);
            editor.apply();
        }
    }

    public static List<UserPojo> getSaveContacts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(contacts, Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(contacts_details, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserPojo>>() {
        }.getType();
        return gson.fromJson(jsonString, type);

    }

    public static void saveCommonUser(Context context, List<UserPojo> users_list) {
        Gson gson = new Gson();
        if (users_list != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(users, Context.MODE_PRIVATE);
            String jsonString = gson.toJson(users_list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(users_data, jsonString);
            editor.apply();
        }
    }

    public static List<UserPojo> getCommonUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(users, Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(users_data, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserPojo>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public static void clearAllPrefData(Context context){

        SharedPreferences preferences =context.getSharedPreferences(login,Context.MODE_PRIVATE);
        SharedPreferences preferences2 =context.getSharedPreferences(contacts,Context.MODE_PRIVATE);
        SharedPreferences preferences3 =context.getSharedPreferences(users,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor2 = preferences2.edit();
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor3 = preferences3.edit();
        editor.clear();
        editor2.clear();
        editor3.clear();
        editor.apply();
        editor2.apply();
        editor3.apply();

    }


}
