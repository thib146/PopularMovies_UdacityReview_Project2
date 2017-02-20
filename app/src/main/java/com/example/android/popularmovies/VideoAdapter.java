package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.MovieArrays;
import com.example.android.popularmovies.utilities.VideoArrays;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by thib146 on 14/02/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    // Global variable containing all the videos currently loaded
    private VideoArrays mVideosData;

    // Global int for the position of an item
    public int adapterPosition;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final VideoAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface VideoAdapterOnClickHandler {
        void onClick(String videos);
    }

    /**
     * Creates a VideoAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public VideoAdapter(VideoAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a video list item.
     */
    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mVideoImageView;

        public VideoAdapterViewHolder(View view) {
            super(view);
            mVideoImageView = (ImageView) view.findViewById(R.id.iv_video_view);
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

            String video = mVideosData.key.get(adapterPosition); // Send the video Key for the Youtube launch

            mClickHandler.onClick(video);
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
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new VideoAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param videoAdapterViewHolder    The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(VideoAdapterViewHolder videoAdapterViewHolder, int position) {

        String oneVideoPoster = mVideosData.imagePath.get(position);

        Context context = videoAdapterViewHolder.mVideoImageView.getContext();

        // Display the video poster in the RecyclerView
        Picasso.with(context).load(oneVideoPoster).into(videoAdapterViewHolder.mVideoImageView);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mVideosData) return 0;
        return mVideosData.imagePath.size();
    }

    /**
     * This method is used to set the video data on a VideoAdapter if we've already
     * created one.
     *
     * @param videos The new video data to be displayed.
     */
    public void setVideoData(VideoArrays videos) {

        // Instantiate of all the variable that we need
        mVideosData = new VideoArrays();
        mVideosData.videoId = new ArrayList<String>();
        mVideosData.iso6391 = new ArrayList<String>();
        mVideosData.iso31661 = new ArrayList<String>();
        mVideosData.key = new ArrayList<String>();
        mVideosData.site = new ArrayList<String>();
        mVideosData.name = new ArrayList<String>();
        mVideosData.size = new ArrayList<String>();
        mVideosData.type = new ArrayList<String>();
        mVideosData.imagePath = new ArrayList<String>();

        // Check if the data passed is null
        if (videos == null) {
            mVideosData = null;
            return;
        }

        // Add the data passed (videos) to each member of mVideosData at the right position
        for (int i = 0; i < videos.videoId.size(); i++) {
            mVideosData.videoId.add(i, videos.videoId.get(i));
            mVideosData.iso6391.add(i, videos.iso6391.get(i));
            mVideosData.iso31661.add(i, videos.iso31661.get(i));
            mVideosData.key.add(i, videos.key.get(i));
            mVideosData.site.add(i, videos.site.get(i));
            mVideosData.name.add(i, videos.name.get(i));
            mVideosData.size.add(i, videos.size.get(i));
            mVideosData.type.add(i, videos.type.get(i));
            mVideosData.imagePath.add(i, videos.imagePath.get(i));
        }

        notifyDataSetChanged();
    }
}
