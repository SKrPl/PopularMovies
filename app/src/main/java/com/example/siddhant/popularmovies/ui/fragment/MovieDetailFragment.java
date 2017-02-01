package com.example.siddhant.popularmovies.ui.fragment;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siddhant.popularmovies.BuildConfig;
import com.example.siddhant.popularmovies.R;
import com.example.siddhant.popularmovies.api.ApiClient;
import com.example.siddhant.popularmovies.api.MovieApiRequests;
import com.example.siddhant.popularmovies.data.PopMoviesContract;
import com.example.siddhant.popularmovies.models.Movie;
import com.example.siddhant.popularmovies.models.Review;
import com.example.siddhant.popularmovies.models.ReviewApiResponse;
import com.example.siddhant.popularmovies.models.VideoApiResponse;
import com.example.siddhant.popularmovies.models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by siddhant on 8/28/16.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private final String MOVIE_PARCELABLE_SAVED = "movie_parcel";
    private final String REVIEWS_PARCELABLE_SAVED = "review_parcel";
    private final String VIDEOS_PARCELABLE_SAVED = "video_parcel";
    private final int MOVIE_DETAIL_LOADER_ID = 200;

    private static final String IS_TWO_PANE = "two_pane";
    private static final String TWO_PANE_MOVIE = "movie";

    private Movie mMovie;

    private ImageView mMoviePoster;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mPlot;
    private TextView mNumVideos;
    private LinearLayout mVideoContents;
    private ImageButton mVideoPrevious;
    private ImageButton mVideoNext;
    private LinearLayout mVideoIntent;
    private TextView mVideoTitle;
    private View mVideoDivider;
    private TextView mReviewHeading;
    private LinearLayout mReviewContainer;
    private FloatingActionButton mFavouriteMovieWhite;
    private FloatingActionButton mFavouriteMovieYellow;

    private List<Review> mReviewList = new ArrayList<>();
    private List<Video> mVideoList = new ArrayList<>();
    private Cursor mVideoCursor;
    private Cursor mReviewCursor;
    private DbMovieUiUpdateListener mDbMovieUiUpdateListener;

    private boolean mTwoPane;
    private boolean mFirstTimeLoaderUse = true;
    private int mCurrentVideo = 0;


    public interface DbMovieUiUpdateListener {
        public void updatePosterFragmentUi();
    }

    public static MovieDetailFragment newInstance(Movie movie, boolean isTwoPane) {
        Bundle args = new Bundle();
        args.putBoolean(IS_TWO_PANE, isTwoPane);
        args.putParcelable(TWO_PANE_MOVIE, movie);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTwoPane = args.getBoolean(IS_TWO_PANE);
            mMovie = args.getParcelable(TWO_PANE_MOVIE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mTwoPane) {
            mDbMovieUiUpdateListener = (DbMovieUiUpdateListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mRating = (TextView) rootView.findViewById(R.id.rating);
        mPlot = (TextView) rootView.findViewById(R.id.plot);
        mNumVideos = (TextView) rootView.findViewById(R.id.num_videos);
        mVideoContents = (LinearLayout) rootView.findViewById(R.id.video_contents);
        mVideoTitle = (TextView) rootView.findViewById(R.id.video_title);
        mVideoDivider = rootView.findViewById(R.id.video_divider);
        mVideoIntent = (LinearLayout) rootView.findViewById(R.id.video_intent);
        mVideoPrevious = (ImageButton) rootView.findViewById(R.id.video_previous);
        mVideoNext = (ImageButton) rootView.findViewById(R.id.video_next);
        mReviewHeading = (TextView) rootView.findViewById(R.id.review_heading);
        mReviewContainer = (LinearLayout) rootView.findViewById(R.id.review_container);
        mFavouriteMovieWhite = (FloatingActionButton)
                rootView.findViewById(R.id.favourite_movie_white);
        mFavouriteMovieYellow = (FloatingActionButton)
                rootView.findViewById(R.id.favourite_movie_yellow);

        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (mFirstTimeLoaderUse && defPref.getBoolean(PosterFragment.SHARED_PREF_DB_KEY, false)) {
            getLoaderManager().restartLoader(
                    MOVIE_DETAIL_LOADER_ID, null, this);
            mFirstTimeLoaderUse = false;
        }

        if (!mTwoPane) {
            Intent intent = getActivity().getIntent();
            mMovie = intent.getParcelableExtra(PosterFragment.MOVIE_PARCELABLE_KEY);
        }

        if (savedInstanceState == null) {
            getReviews();
            getVideos();
        } else {
            mMovie = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED);

            mReviewList = savedInstanceState.getParcelableArrayList(REVIEWS_PARCELABLE_SAVED);
            if (mReviewList.size() == 0) {
                setNoReviews();
            } else {
                inflateReviews();
            }

            mVideoList = savedInstanceState.getParcelableArrayList(VIDEOS_PARCELABLE_SAVED);
            if (mVideoList.size() == 0) {
                setNoVideos();
            } else {
                inflateVideos();
            }
        }

        Cursor cursor = getActivity().getContentResolver().query(
                ContentUris.withAppendedId(PopMoviesContract.Movie.CONTENT_URI, mMovie.getId()),
                null, null, null, null);
        try {
            int movieIdColIndex = cursor.getColumnIndex(PopMoviesContract.Movie._ID);
            cursor.moveToFirst();
            if (cursor.getInt(movieIdColIndex) == mMovie.getId()) {
                mFavouriteMovieWhite.setVisibility(View.GONE);
                mFavouriteMovieYellow.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mTwoPane) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        toolbar.setTitle(mMovie.getTitle());
        toolbar.setTitleTextColor(Color.WHITE);

        String mPosterUrl = mMovie.getBackdropUrl();
        Picasso.with(getActivity()).load(mPosterUrl).fit().into(mMoviePoster);


        String mReleaseDate = mMovie.getReleaseDate();
        this.mReleaseDate.setText(mReleaseDate);

        String mRating = mMovie.getVoteAverage() + "/10";
        this.mRating.setText(mRating);

        String mPlot = mMovie.getOverview();
        this.mPlot.setText(mPlot);

        mVideoIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Video video = mVideoList.get(mCurrentVideo);
                String youtubeUrl = "https://www.youtube.com/watch?v=" + video.getKey();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        mVideoPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentVideo--;
                mVideoTitle.setText(mVideoList.get(mCurrentVideo).getName());
                mNumVideos.setText(
                        "Video: " + String.valueOf(mCurrentVideo+1) + "/" + mVideoList.size()
                );

                if (mCurrentVideo == 0) {
                    mVideoPrevious.setVisibility(View.GONE);
                }
                if (mCurrentVideo+1 < mVideoList.size() && mVideoNext.getVisibility() == View.GONE)
                {
                    mVideoNext.setVisibility(View.VISIBLE);
                }
            }
        });

        mVideoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentVideo++;
                mVideoTitle.setText(mVideoList.get(mCurrentVideo).getName());
                mNumVideos.setText(
                        "Video: " + String.valueOf(mCurrentVideo+1) + "/" + mVideoList.size()
                );

                if (mCurrentVideo+1 == mVideoList.size()) {
                    mVideoNext.setVisibility(View.GONE);
                }
                if (mCurrentVideo > 0 && mVideoPrevious.getVisibility() == View.GONE) {
                    mVideoPrevious.setVisibility(View.VISIBLE);
                }
            }
        });

        mFavouriteMovieWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = getActivity().getContentResolver();

                resolver.insert(
                        PopMoviesContract.Movie.CONTENT_URI,
                        createMovieContentValues(mMovie)
                );

                for (Video video : mVideoList) {
                    resolver.insert(
                            PopMoviesContract.Video.CONTENT_URI,
                            createVideoContentValues(video));
                }

                for (Review review : mReviewList) {
                    resolver.insert(
                            PopMoviesContract.Review.CONTENT_URI,
                            createReviewContentValues(review)
                    );
                }

                mFavouriteMovieWhite.setVisibility(View.GONE);
                mFavouriteMovieYellow.setVisibility(View.VISIBLE);
                Toast.makeText(
                        getActivity(),
                        mMovie.getTitle() + " saved as favourite.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mFavouriteMovieYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = getActivity().getContentResolver();
                int numDeleted;

                numDeleted = resolver.delete(
                        PopMoviesContract.Video.CONTENT_URI,
                        PopMoviesContract.Video.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                        null);
                Log.d(LOG_TAG, "Number of videos deleted: " + numDeleted);

                numDeleted = resolver.delete(
                        PopMoviesContract.Review.CONTENT_URI,
                        PopMoviesContract.Review.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                        null
                );
                Log.d(LOG_TAG, "Number of reviews deleted: " + numDeleted);

                numDeleted = resolver.delete(
                        ContentUris.withAppendedId(
                                PopMoviesContract.Movie.CONTENT_URI,
                                (long) mMovie.getId()),
                        null, null);
                Log.d(LOG_TAG, "Number of movie deleted: " + numDeleted);

                mFavouriteMovieYellow.setVisibility(View.GONE);
                mFavouriteMovieWhite.setVisibility(View.VISIBLE);
                Toast. makeText(
                        getActivity(),
                        mMovie.getTitle() + " removed from favourite.",
                        Toast.LENGTH_SHORT).show();

                if(mTwoPane) {
                    mDbMovieUiUpdateListener.updatePosterFragmentUi();
                }
            }
        });

        return rootView;
    }

    private ContentValues createMovieContentValues(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopMoviesContract.Movie._ID, movie.getId());
        contentValues.put(PopMoviesContract.Movie.COLUMN_POSTER_URL, movie.getPosterUrl());
        contentValues.put(PopMoviesContract.Movie.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(PopMoviesContract.Movie.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(PopMoviesContract.Movie.COLUMN_TITLE, movie.getTitle());
        contentValues.put(PopMoviesContract.Movie.COLUMN_BACKDROP_URL, movie.getBackdropUrl());
        contentValues.put(PopMoviesContract.Movie.COLUMN_RATING, movie.getVoteAverage());
        return contentValues;
    }

    private ContentValues createVideoContentValues(Video video) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopMoviesContract.Video.COLUMN_MOVIE_ID, mMovie.getId());
        contentValues.put(PopMoviesContract.Video.COLUMN_KEY, video.getKey());
        contentValues.put(PopMoviesContract.Video.COLUMN_NAME, video.getName());
        return contentValues;
    }

    private ContentValues createReviewContentValues(Review review) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopMoviesContract.Review.COLUMN_MOVIE_ID, mMovie.getId());
        contentValues.put(PopMoviesContract.Review.COLUMN_AUTHOR, review.getAuthor());
        contentValues.put(PopMoviesContract.Review.COLUMN_CONTENT, review.getContent());
        contentValues.put(PopMoviesContract.Review.COLUMN_URL, review.getUrl());
        return contentValues;
    }

    private void getReviews() {
        MovieApiRequests apiRequests = ApiClient.getRequest(MovieApiRequests.class);
        Call<ReviewApiResponse> call = apiRequests.getReviews(mMovie.getId(), BuildConfig.API_KEY);

        call.enqueue(new Callback<ReviewApiResponse>() {
            @Override
            public void onResponse(
                    Call<ReviewApiResponse> call,
                    Response<ReviewApiResponse> response) {
                mReviewList = response.body().getResults();
                Log.d(LOG_TAG, "Number of reviews received: " + mReviewList.size());
                if (mReviewList.size() == 0) {
                    setNoReviews();
                    return;
                }

                inflateReviews();
            }

            @Override
            public void onFailure(Call<ReviewApiResponse> call, Throwable t) {

            }
        });
    }


    private void setNoReviews() {
        mReviewHeading.setText("No reviews available");
    }

    private void inflateReviews() {
        for (Review review : mReviewList) {
            View rowReview = getLayoutInflater(null).
                    inflate(R.layout.row_movie_review, mReviewContainer, false);
            TextView author = (TextView) rowReview.findViewById(R.id.author);
            TextView content = (TextView) rowReview.findViewById(R.id.content);

            author.setText(review.getAuthor());
            content.setText(review.getContent());
            mReviewContainer.addView(rowReview);
        }
    }

    private void getVideos() {
        MovieApiRequests apiRequests = ApiClient.getRequest(MovieApiRequests.class);
        Call<VideoApiResponse> call = apiRequests.getVideos(mMovie.getId(), BuildConfig.API_KEY);
        call.enqueue(new Callback<VideoApiResponse>() {
            @Override
            public void onResponse(
                    Call<VideoApiResponse> call,
                    Response<VideoApiResponse> response) {
                mVideoList = response.body().getResults();
                if (mVideoList.size() == 0) {
                    setNoVideos();
                    return ;
                }

                inflateVideos();
                Log.d(LOG_TAG, "Number of Video received: " + mVideoList.size());
            }

            @Override
            public void onFailure(Call<VideoApiResponse> call, Throwable t) {

            }
        });
    }

    private void setNoVideos() {
        mNumVideos.setText("No videos available");
        mVideoContents.setVisibility(View.GONE);
        mVideoDivider.setVisibility(View.GONE);
    }

    private void inflateVideos() {
        mNumVideos.setText(
                "Video: " + String.valueOf(mCurrentVideo+1) + "/" + mVideoList.size()
        );
        mVideoTitle.setText(mVideoList.get(0).getName());

        if (mVideoList.size() == 1) {
            mVideoPrevious.setVisibility(View.GONE);
            mVideoNext.setVisibility(View.GONE);
        } else {
            mVideoPrevious.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_PARCELABLE_SAVED, mMovie);
        outState.putParcelableArrayList(REVIEWS_PARCELABLE_SAVED, new ArrayList<>(mReviewList));
        outState.putParcelableArrayList(VIDEOS_PARCELABLE_SAVED, new ArrayList<>(mVideoList));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity()) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
            }

            @Override
            public Cursor loadInBackground() {
                ContentResolver resolver = getActivity().getContentResolver();

                mReviewCursor = resolver.query(
                        PopMoviesContract.Review.CONTENT_URI,
                        null,
                        PopMoviesContract.Review.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                        null, null);

                mVideoCursor = resolver.query(
                        PopMoviesContract.Video.CONTENT_URI,
                        null,
                        PopMoviesContract.Video.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                        null, null);

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean hasData = mVideoCursor.moveToFirst();
        if (hasData) {
            mVideoList = cursorToVideoList(mVideoCursor);
            inflateVideos();
        } else {
            setNoVideos();
        }

        hasData = mReviewCursor.moveToFirst();
        if (hasData) {
            mReviewList = cursorToReviewArrayList(mReviewCursor);
            inflateReviews();
        } else {
            setNoReviews();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ArrayList<Video> cursorToVideoList(Cursor cursor) {
        ArrayList<Video> videos = new ArrayList<>();

        int keyColIndex = cursor.getColumnIndex(PopMoviesContract.Video.COLUMN_KEY);
        int nameColIndex = cursor.getColumnIndex(PopMoviesContract.Video.COLUMN_NAME);

        while (cursor.moveToNext()) {
            String key = cursor.getString(keyColIndex);
            String name = cursor.getString(nameColIndex);
            videos.add(new Video(key, name));
        }
        return videos;
    }

    private  ArrayList<Review> cursorToReviewArrayList(Cursor cursor) {
        ArrayList<Review> reviews = new ArrayList<>();

        int authorColIndex = cursor.getColumnIndex(PopMoviesContract.Review.COLUMN_AUTHOR);
        int contentColIndex = cursor.getColumnIndex(PopMoviesContract.Review.COLUMN_CONTENT);
        int urlColIndex = cursor.getColumnIndex(PopMoviesContract.Review.COLUMN_URL);

        while (cursor.moveToNext()) {
            String author = cursor.getString(authorColIndex);
            String content = cursor.getString(contentColIndex);
            String url = cursor.getString(urlColIndex);
            reviews.add(new Review(author, content, url));
        }
        return reviews;
    }
}
