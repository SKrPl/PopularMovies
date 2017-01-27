package com.example.siddhant.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public static void replaceFragment(Activity activity,
                                       int layoutId,
                                       Fragment newFragment,
                                       String tag) {
        activity.getFragmentManager().beginTransaction()
                .replace(layoutId, newFragment, tag)
                .commit();
    }
}
