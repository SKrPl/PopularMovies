package com.example.siddhant.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by siddhant on 8/26/16.
 */

public class MovieAdapter extends ArrayAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.grid_item_movie, movies);
        this.mContext = context;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = (Movie) getItem(position);
        String url = movie.getPosterUrl();

        if (convertView == null)
            convertView = mInflater.inflate(R.layout.grid_item_movie, parent, false);

        Picasso.with(mContext).load(url).fit().into((ImageView) convertView);

        return convertView;
    }
}
