package com.example.siddhant.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.siddhant.popularmovies.models.Review;

import java.util.ArrayList;
import java.util.List;

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

        private TextView mAuthor;
        private TextView mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    mClickListener.onClick(mReviewList.get(position));
                }
            });
            mAuthor = (TextView) itemView.findViewById(R.id.author);
            mContent = (TextView) itemView.findViewById(R.id.content);
        }

        public void bind(Review review) {
            mAuthor.setText(review.getAuthor());
            mContent.setText(review.getContent());
        }
    }
}
