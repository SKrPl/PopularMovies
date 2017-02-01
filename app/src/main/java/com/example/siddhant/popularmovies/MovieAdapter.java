package com.example.siddhant.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.siddhant.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by siddhant on 8/26/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MoviePosterViewHolder> {

    private Context mContext;

    private OnItemClickListener mClickListener;

    private ArrayList<Movie> mMovieList;

    public MovieAdapter(Context context,
                        ArrayList<Movie> movies,
                        OnItemClickListener itemClickListener) {
        mContext = context;
        mMovieList = movies;
        mClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        public void setOnClickListener(Movie movie);
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder {

        ImageView mMoviePoster;

        public MoviePosterViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Movie movie = mMovieList.get(position);
                    mClickListener.setOnClickListener(movie);
                }
            });

            mMoviePoster = (ImageView) itemView.findViewById(R.id.poster);
        }
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grid_item_movie, parent, false);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        String posterUrl = movie.getPosterUrl();
        Picasso.with(mContext).load(posterUrl).fit().into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void setMovieList(ArrayList<Movie> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovieList() {
        return mMovieList;
    }
}
