package com.example.siddhant.popularmovies.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.siddhant.popularmovies.R;
import com.example.siddhant.popularmovies.models.Review;
import com.example.siddhant.popularmovies.ui.fragment.MovieDetailFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReviewActivity extends AppCompatActivity {

    private final String REVIEW_PARCELABLE_SAVED = "review_saved";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.review_item_author) TextView author;
    @BindView(R.id.review_item_content) TextView content;

    private Review mReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        String title = "Review of " + intent.getStringExtra(MovieDetailFragment.MOVIE_NAME);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (savedInstanceState == null) {
            mReview = getIntent().getParcelableExtra(MovieDetailFragment.REVIEW_PARCELABLE_KEY);
        } else {
            mReview = savedInstanceState.getParcelable(REVIEW_PARCELABLE_SAVED);
        }

        author.setText(mReview.getAuthor());
        content.setText(mReview.getContent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(REVIEW_PARCELABLE_SAVED, mReview);
    }
}
