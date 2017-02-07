package com.example.siddhant.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.siddhant.popularmovies.models.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by siddhant on 2/4/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private List<Review> mReviewList = new ArrayList<>();
    private OnReviewClickListener mClickListener;

    public interface OnReviewClickListener {
        void onClick(Review review);
    }

    public ReviewAdapter(List<Review> reviews, OnReviewClickListener clickListener) {
        mReviewList = reviews;
        mClickListener = clickListener;
    }

    public void setReviewList(List<Review> reviews) {
        mReviewList = reviews;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ReviewViewHolder(inflater.inflate(R.layout.row_movie_review, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(mReviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public final class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author) TextView mAuthor;
        @BindView(R.id.review_content) TextView mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.row_movie_review)
        public void onReviewRowClick() {
            int position = getLayoutPosition();
            mClickListener.onClick(mReviewList.get(position));
        }

        public void bind(Review review) {
            mAuthor.setText(review.getAuthor());
            mContent.setText(review.getContent());
        }
    }
}
