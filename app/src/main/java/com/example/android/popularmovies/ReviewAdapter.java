package com.example.android.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.ReviewArrays;

import java.util.ArrayList;

/**
 * Created by thib146 on 15/02/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    // Global variable containing all the reviews currently loaded
    private ReviewArrays mReviewsData;

    // Global int for the position of an item
    public int adapterPosition;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ReviewAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ReviewAdapterOnClickHandler {
        void onClick(String reviews);
    }

    /**
     * Creates a ReviewAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a review list item.
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {;
        public final TextView mAuthorInitialTextView;
        public final TextView mAuthorNameTextView;
        public final TextView mReviewTextView;
        public final TextView mReadMoreTextView;

        private boolean isReadMoreClicked = false;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mAuthorInitialTextView = (TextView) view.findViewById(R.id.tv_author_initial);
            mAuthorNameTextView = (TextView) view.findViewById(R.id.tv_author_name);
            mReviewTextView = (TextView) view.findViewById(R.id.tv_content);
            mReadMoreTextView = (TextView) view.findViewById(R.id.tv_reviews_read_more);

            mReadMoreTextView.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            adapterPosition = getAdapterPosition();
            String reviewId = mReviewsData.id.get(adapterPosition);
            String oneReviewText = mReviewsData.content.get(adapterPosition);

            // Handle the click on the Read More button
            if (v.getId() == mReadMoreTextView.getId() && !isReadMoreClicked) { // If the "Read More" button is clicked
                mReviewTextView.setText(oneReviewText); // Display the whole review
                mReadMoreTextView.setText(mReviewsData.readLessText); // Change the text of the button to "Read Less"

                isReadMoreClicked = true;
                mClickHandler.onClick(reviewId);
            } else if (v.getId() == mReadMoreTextView.getId() && isReadMoreClicked) { // If the "Read Less" button is clicked
                String readLessText = oneReviewText.substring(0,100) + "...";
                mReviewTextView.setText(readLessText); // Display the 100 first characters + "..."
                mReadMoreTextView.setText(mReviewsData.readMoreText); // Change the text of the button to "Read More"

                isReadMoreClicked = false;
                mClickHandler.onClick(reviewId);
            }
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType In case of multiple view types
     * @return A new MovieAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param reviewAdapterViewHolder    The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {

        String oneAuthorName = mReviewsData.author.get(position); // Author data
        String oneReviewText = mReviewsData.content.get(position); // Content data

        String oneAuthorInitial = oneAuthorName.substring(0, 1).toUpperCase(); // Create the initial in the circle image

        if (oneReviewText.length() >= 100) { // If the Review is too long, cut it to the 1st 100 characters by default and display "Read More"
            String reviewShortText = oneReviewText.substring(0,100) + "...";
            reviewAdapterViewHolder.mReviewTextView.setText(reviewShortText);
            reviewAdapterViewHolder.mReadMoreTextView.setVisibility(View.VISIBLE);
        } else { // If the review is short, display it entirely with no "Read More" button
            reviewAdapterViewHolder.mReviewTextView.setText(oneReviewText);
            reviewAdapterViewHolder.mReadMoreTextView.setVisibility(View.INVISIBLE);
        }

        reviewAdapterViewHolder.mAuthorNameTextView.setText(oneAuthorName);
        reviewAdapterViewHolder.mAuthorInitialTextView.setText(oneAuthorInitial);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mReviewsData) return 0;
        return mReviewsData.author.size();
    }

    /**
     * This method is used to set the review data on a ReviewAdapter if we've already
     * created one.
     *
     * @param reviews The new review data to be displayed.
     */
    public void setReviewData(ReviewArrays reviews) {

        // Check if the data passed is null
        if (reviews == null) {
            mReviewsData = null;
            return;
        }

        // Instantiate of all the variable that we need
        mReviewsData = new ReviewArrays();
        mReviewsData.author = new ArrayList<String>();
        mReviewsData.id = new ArrayList<String>();
        mReviewsData.content = new ArrayList<String>();
        mReviewsData.url = new ArrayList<String>();

        // Add the data passed (reviews) to each member of mReviewsData at the right position
        for (int i = 0; i < reviews.author.size(); i++) {
            mReviewsData.author.add(i, reviews.author.get(i));
            mReviewsData.id.add(i, reviews.id.get(i));
            mReviewsData.content.add(i, reviews.content.get(i));
            mReviewsData.url.add(i, reviews.url.get(i));
        }
        mReviewsData.readMoreText = reviews.readMoreText;
        mReviewsData.readLessText = reviews.readLessText;

        notifyDataSetChanged();
    }
}