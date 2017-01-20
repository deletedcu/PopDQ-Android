package com.popdq.app.ui;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.popdq.app.R;

import java.io.IOException;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback {
    private static final String TAG = "Main2Activity";
    private VideoView videoViewFull;
    private ImageView rotate;
    private int position;
    private SurfaceHolder holder;
    private MediaPlayer mMediaPlayer;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private int mVideoWidth;
    private int mVideoHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri video = Uri.parse("http://api.popdq.com/api/static/attachment/attachments_user_292_1478836728_vid_20161008_212905.mp4");
        setContentView(R.layout.activity_main2);

        videoViewFull = (VideoView) findViewById(R.id.videoViewFull);
//        videoViewFull.setVideoURI(video);
//        videoViewFull.start();
//        int t = videoViewFull.getWidth();
//        int t2 = videoViewFull.getMinimumWidth();
//        int t3 = videoViewFull.getHeight();
//        int t4 = videoViewFull.getMinimumHeight();



        rotate = (ImageView) findViewById(R.id.rotate);
        rotate.setOnClickListener(this);

////        videoViewFull.setSource(video);
////        videoViewFull.setAutoPlay(true);
//
        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(videoViewFull);
        videoViewFull.setMediaController(mediaController);
//
        videoViewFull.setVideoURI(video);
        videoViewFull.requestFocus();
        videoViewFull.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {

                videoViewFull.start();
            }
        });
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putInt("Position", videoViewFull.getCurrentPosition());
//        videoViewFull.pause();
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        position = savedInstanceState.getInt("Position");
//        videoViewFull.seekTo(position);
//    }

    public void play() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource("http://api.popdq.com/api/static/attachment/attachments_user_149_1476796353_vid_20161008_131945.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setDisplay(holder);
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rotate:
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }

                break;
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height
                    + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        play();

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
    }
}

