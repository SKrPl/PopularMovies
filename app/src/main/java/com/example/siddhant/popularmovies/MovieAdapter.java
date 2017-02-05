package com.example.siddhant.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    private final int ITEM_LEFT = 0;
    private final int ITEM_RIGHT = 1;

    public interface OnItemClickListener {
        void setOnClickListener(Movie movie);
    }

    public MovieAdapter(Context context,
                        ArrayList<Movie> movies,
                        OnItemClickListener itemClickListener) {
        mContext = context;
        mMovieList = movies;
        mClickListener = itemClickListener;
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder {

        private ImageView mMoviePoster;
        private TextView mMovieName;

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

            mMoviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            mMovieName = (TextView) itemView.findViewById(R.id.movie_name);
        }

        public void bind(Movie movie) {
            Picasso.with(mContext).load(movie.getPosterUrl()).fit().into(mMoviePoster);
            mMovieName.setText(movie.getTitle());
        }
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grid_item_movie_poster, parent, false);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? ITEM_RIGHT : ITEM_LEFT;
    }

    public void setMovieList(ArrayList<Movie> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovieList() {
        return mMovieList;
    }
}
