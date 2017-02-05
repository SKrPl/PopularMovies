package com.example.siddhant.popularmovies.ui.fragment;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siddhant.popularmovies.BuildConfig;
import com.example.siddhant.popularmovies.R;
import com.example.siddhant.popularmovies.ReviewAdapter;
import com.example.siddhant.popularmovies.api.ApiClient;
import com.example.siddhant.popularmovies.api.MovieApiRequests;
import com.example.siddhant.popularmovies.data.PopMoviesContract;
import com.example.siddhant.popularmovies.models.Movie;
import com.example.siddhant.popularmovies.models.Review;
import com.example.siddhant.popularmovies.models.ReviewApiResponse;
import com.example.siddhant.popularmovies.models.Video;
import com.example.siddhant.popularmovies.models.VideoApiResponse;
import com.example.siddhant.popularmovies.ui.activity.ReviewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by siddhant on 8/28/16.
 */
public class MovieDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ReviewAdapter.OnReviewClickListener {

    public static final String REVIEW_PARCELABLE_KEY = "review_parcelable";
    public static final String MOVIE_NAME = "movie_name";

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private final String MOVIE_PARCELABLE_SAVED = "movie_parcel";
    private final String VIDEOS_PARCELABLE_SAVED = "videos_parcel";
    private final String REVIEWS_PARCELABLE_SAVED = "reviews_parcel";
    private final int MOVIE_DETAIL_LOADER_ID = 200;

    private static final String IS_TWO_PANE = "two_pane";
    private static final String TWO_PANE_MOVIE = "movie";

    private ShareActionProvider mShareActionProvider;

    private ImageView mMovieBackdrop;
    private ImageView mMoviePoster;
    private TextView mMovieName;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mPlot;

    private CardView mVideoCardView;
    private TextView mNumVideos;
    private LinearLayout mVideoContents;
    private ImageButton mVideoPrevious;
    private ImageButton mVideoNext;
    private LinearLayout mVideoIntent;
    private TextView mVideoTitle;
    private View mVideoDivider;

    private CardView mReviewCardView;
    private TextView mReviewHeading;
    private RecyclerView mReviewRecyclerView;

    private FloatingActionButton mMovieFavourite;

    private Cursor mVideoCursor;
    private Cursor mReviewCursor;

    private Movie mMovie;
    private List<Review> mReviewList = new ArrayList<>();
    private List<Video> mVideoList = new ArrayList<>();
    private ReviewAdapter mReviewAdapter;
    private DbMovieUiUpdateListener mDbMovieUiUpdateListener;


    private boolean mTwoPane;
    private boolean mFirstTimeLoaderUse = true;
    private boolean mMovieInDb = false;
    private int mCurrentVideo = 0;

    public interface DbMovieUiUpdateListener {
        void updatePosterFragmentUi();
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
        setHasOptionsMenu(true);
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (!mTwoPane) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        mMovieBackdrop = (ImageView) rootView.findViewById(R.id.movie_backdrop);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);

        mMovieName = (TextView) rootView.findViewById(R.id.movie_name);
        mReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        mRating = (TextView) rootView.findViewById(R.id.movie_rating);

        mPlot = (TextView) rootView.findViewById(R.id.movie_plot);

        mVideoCardView = (CardView) rootView.findViewById(R.id.video_card_view);
        mNumVideos = (TextView) rootView.findViewById(R.id.movie_videos_num);
        mVideoContents = (LinearLayout) rootView.findViewById(R.id.video_contents);
        mVideoTitle = (TextView) rootView.findViewById(R.id.video_title);
        mVideoDivider = rootView.findViewById(R.id.video_divider);
        mVideoIntent = (LinearLayout) rootView.findViewById(R.id.video_intent);
        mVideoPrevious = (ImageButton) rootView.findViewById(R.id.video_previous);
        mVideoNext = (ImageButton) rootView.findViewById(R.id.video_next);

        mReviewCardView = (CardView) rootView.findViewById(R.id.review_card_view);
        mReviewHeading = (TextView) rootView.findViewById(R.id.review_heading);
        mReviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_list);

        mMovieFavourite = (FloatingActionButton)
                rootView.findViewById(R.id.movie_favourite);

        mVideoCardView.setVisibility(View.GONE);
        mReviewCardView.setVisibility(View.GONE);

        SharedPreferences sharedefPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        boolean onClickedFavoritePrefValue = sharedefPref
                .getBoolean(PosterFragment.SHARED_PREF_ON_CLICKED_FAVOURITE, false);

        // Data is loaded from the loader if the user is visiting favourite movie and that too only
        // once, and then retrived from parcel in case of an orientation change.
        if (mFirstTimeLoaderUse && onClickedFavoritePrefValue) {
            getLoaderManager().restartLoader(
                    MOVIE_DETAIL_LOADER_ID, null, this);
            mFirstTimeLoaderUse = false;
        }

        if (!mTwoPane) {
            Intent intent = getActivity().getIntent();
            mMovie = intent.getParcelableExtra(PosterFragment.MOVIE_PARCELABLE_KEY);
        }

        toolbar.setTitle(mMovie.getTitle());

        // checks whether the movie is already present in database or not, if yes the changes
        // the color of the star to yellow
        Cursor cursor = getActivity().getContentResolver().query(
                ContentUris.withAppendedId(PopMoviesContract.Movie.CONTENT_URI, mMovie.getId()),
                null, null, null, null);
        try {
            int movieIdColIndex = cursor.getColumnIndex(PopMoviesContract.Movie._ID);
            cursor.moveToFirst();
            if (cursor.getInt(movieIdColIndex) == mMovie.getId()) {
                mMovieFavourite.setImageResource(R.drawable.ic_star_yellow);
                mMovieInDb = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        // network request won't be send while viewing favourite movies
        if (savedInstanceState == null && !onClickedFavoritePrefValue) {
            getVideos();
            getReviews();
        } else if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED);
            mVideoCardView.setVisibility(View.VISIBLE);
            mReviewCardView.setVisibility(View.VISIBLE);

            mVideoList = savedInstanceState.getParcelableArrayList(VIDEOS_PARCELABLE_SAVED);
            Log.d(LOG_TAG, "Number of videos restored from Parcel: " + mVideoList.size());
            if (mVideoList.size() == 0) {
                setNoVideos();
            } else {
                inflateVideos();
            }

            mReviewList = savedInstanceState.getParcelableArrayList(REVIEWS_PARCELABLE_SAVED);
            Log.d(LOG_TAG, "Number of reviews restored from Parcel: " + mReviewList.size());
            if (mReviewList.size() == 0) {
                setNoReviews();
            }
        }

        mReviewAdapter = new ReviewAdapter(mReviewList, this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String backdropUrl = mMovie.getBackdropUrl();
        Picasso.with(getActivity()).load(backdropUrl).fit().into(mMovieBackdrop);

        String posterUrl = mMovie.getPosterUrl();
        Picasso.with(getActivity()).load(posterUrl).fit().into(mMoviePoster);

        mMovieName.setText(mMovie.getTitle());

        String mReleaseDate = mMovie.getReleaseDate();
        this.mReleaseDate.setText(mReleaseDate);

        String mRating = String.valueOf(mMovie.getVoteAverage());
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
                        "Video: " + String.valueOf(mCurrentVideo + 1) + "/" + mVideoList.size()
                );

                if (mCurrentVideo == 0) {
                    mVideoPrevious.setVisibility(View.GONE);
                }
                if (mCurrentVideo + 1 < mVideoList.size() && mVideoNext.getVisibility() == View.GONE) {
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
                        "Video: " + String.valueOf(mCurrentVideo + 1) + "/" + mVideoList.size()
                );

                if (mCurrentVideo + 1 == mVideoList.size()) {
                    mVideoNext.setVisibility(View.GONE);
                }
                if (mCurrentVideo > 0 && mVideoPrevious.getVisibility() == View.GONE) {
                    mVideoPrevious.setVisibility(View.VISIBLE);
                }
            }
        });

        mMovieFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = getActivity().getContentResolver();

                if (!mMovieInDb) {
                    addMovieToDatabase(resolver);
                } else {
                    removeMovieFromDatabase(resolver);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onClick(Review review) {
        Intent intent = new Intent(getActivity(), ReviewActivity.class);
        intent.putExtra(REVIEW_PARCELABLE_KEY, review);
        intent.putExtra(MOVIE_NAME, mMovie.getTitle());
        startActivity(intent);
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
        mVideoCardView.setVisibility(View.VISIBLE);
        if (hasData) {
            mVideoList = cursorToVideoList(mVideoCursor);
            inflateVideos();
        } else {
            setNoVideos();
        }

        mReviewCardView.setVisibility(View.VISIBLE);
        hasData = mReviewCursor.moveToFirst();
        if (hasData) {
            mReviewList = cursorToReviewArrayList(mReviewCursor);
            mReviewAdapter.setReviewList(mReviewList);
        } else {
            setNoReviews();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addMovieToDatabase(ContentResolver resolver) {
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

        mMovieFavourite.setImageResource(R.drawable.ic_star_yellow);

        Toast.makeText(
                getActivity(),
                mMovie.getTitle() + " saved as favourite.",
                Toast.LENGTH_SHORT).show();
    }

    private void removeMovieFromDatabase(ContentResolver resolver) {
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

        Toast.makeText(
                getActivity(),
                mMovie.getTitle() + " removed from favourite.",
                Toast.LENGTH_SHORT).show();

        mMovieFavourite.setImageResource(R.drawable.ic_star_white);

        if (mTwoPane) {
            mDbMovieUiUpdateListener.updatePosterFragmentUi();
        }
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

    private void getVideos() {
        MovieApiRequests apiRequests = ApiClient.getRequest(MovieApiRequests.class);
        Call<VideoApiResponse> call = apiRequests.getVideos(mMovie.getId(), BuildConfig.API_KEY);
        call.enqueue(new Callback<VideoApiResponse>() {
            @Override
            public void onResponse(
                    Call<VideoApiResponse> call,
                    Response<VideoApiResponse> response) {
                mVideoCardView.setVisibility(View.VISIBLE);
                mVideoList = response.body().getResults();
                if (mVideoList.size() == 0) {
                    setNoVideos();
                    return;
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
                "Video: " + String.valueOf(mCurrentVideo + 1) + "/" + mVideoList.size()
        );
        mVideoTitle.setText(mVideoList.get(0).getName());

        if (mVideoList.size() == 1) {
            mVideoPrevious.setVisibility(View.GONE);
            mVideoNext.setVisibility(View.GONE);
        } else {
            mVideoPrevious.setVisibility(View.GONE);
        }
    }

    private void getReviews() {
        MovieApiRequests apiRequests = ApiClient.getRequest(MovieApiRequests.class);
        Call<ReviewApiResponse> call = apiRequests.getReviews(mMovie.getId(), BuildConfig.API_KEY);

        call.enqueue(new Callback<ReviewApiResponse>() {
            @Override
            public void onResponse(
                    Call<ReviewApiResponse> call,
                    Response<ReviewApiResponse> response) {
                mReviewCardView.setVisibility(View.VISIBLE);
                mReviewList = response.body().getResults();
                if (mReviewList.size() == 0) {
                    setNoReviews();
                    return;
                }
                Log.d(LOG_TAG, "Number of reviews received: " + mReviewList.size());
                mReviewAdapter.setReviewList(mReviewList);
            }

            @Override
            public void onFailure(Call<ReviewApiResponse> call, Throwable t) {

            }
        });
    }

    private void setNoReviews() {
        mReviewHeading.setText("No reviews available");
    }

    private ArrayList<Video> cursorToVideoList(Cursor cursor) {
        ArrayList<Video> videos = new ArrayList<>();

        int keyColIndex = cursor.getColumnIndex(PopMoviesContract.Video.COLUMN_KEY);
        int nameColIndex = cursor.getColumnIndex(PopMoviesContract.Video.COLUMN_NAME);

        String key = cursor.getString(keyColIndex);
        String name = cursor.getString(nameColIndex);
        videos.add(new Video(key, name));

        while (cursor.moveToNext()) {
            key = cursor.getString(keyColIndex);
            name = cursor.getString(nameColIndex);
            videos.add(new Video(key, name));
        }
        return videos;
    }

    private ArrayList<Review> cursorToReviewArrayList(Cursor cursor) {
        ArrayList<Review> reviews = new ArrayList<>();

        int authorColIndex = cursor.getColumnIndex(PopMoviesContract.Review.COLUMN_AUTHOR);
        int contentColIndex = cursor.getColumnIndex(PopMoviesContract.Review.COLUMN_CONTENT);
        int urlColIndex = cursor.getColumnIndex(PopMoviesContract.Review.COLUMN_URL);

        String author = cursor.getString(authorColIndex);
        String content = cursor.getString(contentColIndex);
        String url = cursor.getString(urlColIndex);
        reviews.add(new Review(author, content, url));

        while (cursor.moveToNext()) {
            author = cursor.getString(authorColIndex);
            content = cursor.getString(contentColIndex);
            url = cursor.getString(urlColIndex);
            reviews.add(new Review(author, content, url));
        }
        return reviews;
    }
}
