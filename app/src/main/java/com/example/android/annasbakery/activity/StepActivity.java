package com.example.android.annasbakery.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.annasbakery.R;
import com.example.android.annasbakery.RecipeUtils;
import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.data.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity implements ExoPlayer.EventListener{

    @BindView(R.id.step_exoplayer_view)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.step_empty_iv)
    ImageView mEmptyImageView;
    @BindView(R.id.step_desc_tv)
    TextView mDescTextView;
    @BindView(R.id.step_short_desc_tv)
    TextView mShortDescTextView;

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Recipe mRecipe;
    private boolean mHasTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        ButterKnife.bind(this);

        // Activate up navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        Step step;
        if (bundle != null) {
            mRecipe = bundle.getParcelable(MainActivity.DETAIL_KEY);
            step = bundle.getParcelable(MainActivity.STEP_KEY);
            if (step != null) {
                String videoUrl = step.getVideoUrl();
                String thumbnailUrl = step.getThumbnailUrl();
                String desc = step.getDescription();
                int id = step.getId();
                final String shortDesc = step.getShortDescription();
                if (getSupportActionBar() != null) {
                    String title = String.format(getString(R.string.step_title_text), mRecipe.getName(), id);
                    getSupportActionBar().setTitle(title);
                }
                mShortDescTextView.setText(shortDesc);
                mDescTextView.setText(desc);
                if(!TextUtils.isEmpty(videoUrl)) {
                    mEmptyImageView.setVisibility(View.GONE);
                    mPlayerView.setVisibility(View.VISIBLE);
                    initializeMediaSession();
                    initializePlayer(videoUrl);
                } else {
                    mPlayerView.setVisibility(View.GONE);
                    if(!TextUtils.isEmpty(thumbnailUrl)) {
                        Picasso.with(this).load(thumbnailUrl)
                                .into(mEmptyImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        mEmptyImageView.setImageResource(RecipeUtils.findCorrectStockImage(mRecipe.getName()));
                                        mEmptyImageView.setContentDescription(shortDesc);
                                    }
                                });
                    } else {
                        mEmptyImageView.setImageResource(RecipeUtils.findCorrectStockImage(mRecipe.getName()));
                        mEmptyImageView.setContentDescription(shortDesc);
                        mEmptyImageView.setTag(RecipeUtils.findCorrectStockImage(mRecipe.getName()));
                    }
                    mEmptyImageView.setVisibility(View.VISIBLE);
                }
            }
            if (bundle.containsKey(MainActivity.TWO_PANE_KEY)){
                mHasTwoPane = bundle.getBoolean(MainActivity.TWO_PANE_KEY);
            }
        }
    }

    private void initializeMediaSession() {

        // Create a new MediaSession
        mMediaSession = new MediaSessionCompat(this, StepActivity.class.getSimpleName());

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // Handle callbacks from a media controller
        mMediaSession.setCallback(new StepCallback());

        // Start the Media Session since the activity is active
        mMediaSession.setActive(true);
    }

    private void initializePlayer(String videoUrl) {
        if (mExoPlayer == null){

            // Create a new SimpleExoPlayer instance and set it to the View
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(), new DefaultLoadControl());
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity
            mExoPlayer.addListener(this);

            // Prepare the MediaSource
            String userAgent = Util.getUserAgent(this, getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(
                    Uri.parse(videoUrl),
                    new DefaultDataSourceFactory(this, userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            releasePlayer();
        }
        mMediaSession.setActive(false);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class StepCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    public static class RecipeMediaButtonReceiver extends BroadcastReceiver {

        public RecipeMediaButtonReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    /* Override the standard behaviour because if navigate up,
     * DetailActivity/DetailFragment won't have a Recipe to display so we have to pass it
     */
    @Override
    public void onBackPressed() {
        if (mExoPlayer != null) {
            releasePlayer();
        }
        Intent backToDetailIntent = new Intent(StepActivity.this, DetailActivity.class);
        if (mHasTwoPane) {
            backToDetailIntent = new Intent(StepActivity.this, MainActivity.class);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.DETAIL_KEY, mRecipe);
        backToDetailIntent.putExtras(bundle);
        startActivity(backToDetailIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }
}
