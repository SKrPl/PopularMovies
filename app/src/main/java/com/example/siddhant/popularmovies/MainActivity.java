package com.example.siddhant.popularmovies;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private ConnectivityManager mConnectivityManager;
    private PosterFragment mFragment;

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean isOnline() {
        /**
         * Checks internet connectivity, so that app won't crash
         * Returns: boolean according to internet connectivity
         */
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        mFragment = new PosterFragment();

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /*
        if connected show movie posters in grid view,
        adding PosterFragment
         */
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.main_setting:
                openSettingsActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    public void retryConnecting(View view) {
        /**
         * onClickListener for Retry button in NoNetworkFragment
         */
        if (isOnline()) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

}
