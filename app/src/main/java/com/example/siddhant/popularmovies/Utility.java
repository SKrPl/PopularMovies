package com.example.siddhant.popularmovies;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by siddhant on 11/21/16.
 */

public class Utility {

    public static String getSortingCriteria(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context
        );
        return sharedPreferences.getString(
                context.getResources().getString(R.string.order_key),
                context.getResources().getString(R.string.order_value_default)
        );
    }
}
