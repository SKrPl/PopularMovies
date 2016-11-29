package com.example.siddhant.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by siddhant on 8/31/16.
 */
public class NoNetworkFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_network, container, false);

        Button retryButton = (Button) view.findViewById(R.id.button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.replaceFragment(
                        getActivity(),
                        R.id.fragment_container,
                        new PosterFragment(),
                        null
                );
            }
        });
        return view;
    }
}
