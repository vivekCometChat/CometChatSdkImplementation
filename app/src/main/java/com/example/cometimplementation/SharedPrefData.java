package com.example.cometimplementation;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.ConnectException;

public class SharedPrefData {

    public static void saveUserIdName(Context context,String id,String name){
        SharedPreferences sharedPreferences=context.getSharedPreferences("LOGIN",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", id);
        editor.putString("name", name);
        editor.apply();

    }

    public static String getUserId(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("LOGIN",Context.MODE_PRIVATE);
        return sharedPreferences.getString("uid","");
    }

    public static String getUserName(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("LOGIN",Context.MODE_PRIVATE);
        return sharedPreferences.getString("name","");
    }

}
