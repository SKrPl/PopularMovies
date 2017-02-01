package com.example.siddhant.popularmovies.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.siddhant.popularmovies.R;
import com.example.siddhant.popularmovies.Utility;

/**
 * Created by siddhant on 8/31/16.
 */
public class NoNetworkFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_network, container, false);

        Button retryButton = (Button) view.findViewById(R.id.button);
        /*retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment_container, new PosterFragment())
                        .commit();
            }
        });*/
        return view;
    }
}
