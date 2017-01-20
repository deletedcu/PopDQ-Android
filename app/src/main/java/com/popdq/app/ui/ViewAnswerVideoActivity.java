package com.popdq.app.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.popdq.app.R;
import com.popdq.app.model.Answer;
import com.popdq.app.model.Question;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.Timer;
import java.util.TimerTask;

public class ViewAnswerVideoActivity extends BaseViewAnswerActivity implements EasyVideoCallback {
//    private VideoView videoView;
    private EasyVideoPlayer videoViewFull;
    private ImageView rotate;
    private RelativeLayout layoutVideoFull;
    private SeekBar seekbar;
    private TextViewNormal tvDuration;
    private LinearLayout btnPlay, btnPause;
    private int maxDuration;
    private boolean loadedAnswer = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                int duration = videoViewFull.getCurrentPosition();
                seekbar.setProgress(Math.round(videoViewFull.getCurrentPosition() / 100));
            } catch (Exception e) {
            }
        }
    };
//    VideoViewCustom videoViewFull;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Question.Attachments attachments = answer.getAttachments()[0];
//        String link = Values.BASE_URL_AVATAR + attachments.link;
//
//        Uri video = Uri.parse(link);
//
//        videoViewFull.setVideoURI(video);
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//            videoViewFull.setDimensions(videoViewFull.getHeight(), videoViewFull.getWidth());
//            videoViewFull.getHolder().setFixedSize(videoViewFull.getHeight(), videoViewFull.getWidth());
//
//        } else {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//
//            videoViewFull.setDimensions(videoViewFull.getHeight(), videoViewFull.getWidth());
//            videoViewFull.getHolder().setFixedSize(videoViewFull.getHeight(), videoViewFull.getWidth());
//
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answer_video);
        Utils.checkStartActivityFromNotificationAndSendRead(this, null);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvDuration = (TextViewNormal) findViewById(R.id.tvDuration);
        btnPlay = (LinearLayout) findViewById(R.id.btnPlay);
        btnPause = (LinearLayout) findViewById(R.id.btnPause);
        rotate = (ImageView) findViewById(R.id.rotate);
        layoutVideoFull = (RelativeLayout) findViewById(R.id.layoutVideoFull);
        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        rotate.setOnClickListener(this);
        setRateListener();
//        videoView = (VideoView) findViewById(R.id.videoView);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager audioManager =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);

//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);

        Utils.setActionBar(this, getString(R.string.video_answer), R.drawable.btn_back);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int values = seekbar.getProgress();
                Log.e("seek", progress + ".." + fromUser);
//                if (videoView != null && fromUser) {
//                    videoView.seekTo(values * 100);
//                    tvDuration.setText(Utils.getDurationFromLong(values * 100));
//                } else {
//                    tvDuration.setText(Utils.getDurationFromLong(values * 100));
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        getData(new OnLoadAnswerListener() {
            @Override
            public void onSuccess(Answer answer) {

                if (answer.getAttachments() != null && answer.getAttachments().length > 0) {
                    Question.Attachments attachments = answer.getAttachments()[0];
                    String link = Values.BASE_URL_AVATAR + attachments.link;
//                    videoView = (VideoView) findViewById(R.id.videoView);
                    videoViewFull = (EasyVideoPlayer) findViewById(R.id.videoViewFull);
//                    MediaController mediaController = new MediaController(ViewAnswerVideoActivity.this);
//                    mediaController.setAnchorView(videoView);
                    Uri video = Uri.parse(link);
//                    videoViewFull.setMediaController(mediaController);

//                    videoView.setVideoURI(video);
//                    videoView.seekTo(100);
//                    videoViewFull.setVideoURI(video);
                    videoViewFull.setSource(video);
//                    videoViewFull.setAutoFullscreen(true);
                    videoViewFull.setCallback(ViewAnswerVideoActivity.this);
                    long dur = (long) attachments.info.getDuration();
                    ((TextViewNormal) findViewById(R.id.tvMaxDuration)).setText(Utils.getDurationFromLong(dur));
                    tvDuration.setText(Utils.getDurationFromLong(dur));
                    maxDuration = (int) Math.round(attachments.info.getDuration() / 100);
                    seekbar.setMax(maxDuration);
                    loadedAnswer = true;

                    Log.e("VIDEO ERROR", link);
                    Log.e("VIDEO ERROR #2", String.valueOf(video));


                    playvideo();

                } else {
                    Toast.makeText(ViewAnswerVideoActivity.this, "Get data error!", Toast.LENGTH_SHORT).show();
                }
//                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mediaPlayer) {
//                        if (timer != null) {
//                            timer.cancel();
//                            timer = null;
//                        }
//                        if (timerTask != null) {
//                            timerTask.cancel();
//                            timerTask = null;
//                        }
//                        seekbar.setProgress(0);
//
//                    }
//                });

            }
        });
//        if (!getIntentNotificationBar().getBooleanExtra(Values.isViewed, false))
//            ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Utils.showDialogShare(ViewAnswerVideoActivity.this);
//                }
//            });



    }

    TimerTask timerTask;
    Timer timer;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rotate:
                if(getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }

                break;

            case R.id.btnPlay:
//                Question.Attachments attachments = answer.getAttachments()[0];
//                String link = Values.BASE_URL_AVATAR + attachments.link;
//                Uri video = Uri.parse(link);
//                Intent intent = new Intent(Intent.ACTION_VIEW, video);
//                intent.setDataAndType(video, "video/*");
//                startActivity(intent);

                playvideo();

                break;
            case R.id.btnPause:
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (videoViewFull != null && videoViewFull.isPlaying()) {
                    videoViewFull.pause();
                }
                break;
        }
    }

    private void playvideo() {

        if (videoViewFull.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.play_icon);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (videoViewFull != null && videoViewFull.isPlaying()) {
                videoViewFull.pause();
            }
//                    videoView.pause();

        } else {

            btnPlay.setBackgroundResource(R.drawable.pause_icon);
            if (loadedAnswer) {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                };
                timer = new Timer();
                timer.scheduleAtFixedRate(timerTask, 0, Values.PER_UPDATE_SEEKBAR);
//                        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1,0,1);
//                        scaleAnimation.setDuration(500);
//                        layoutVideoFull.startAnimation(scaleAnimation);
//                        layoutVideoFull.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
                layoutVideoFull.setVisibility(View.VISIBLE);

//                        videoView.start();
//                            }
//                        },500);
//                        videoView.setVisibility(View.GONE);
                videoViewFull.setAutoPlay(true);
//
                videoViewFull.start();
            } else {
                Toast.makeText(getApplicationContext(), "Loading Answer....", Toast.LENGTH_LONG).show();

            }
        }


    }

    @Override
    public void onBackPressed() {
        if (layoutVideoFull != null && layoutVideoFull.getVisibility() == View.VISIBLE) {
            layoutVideoFull.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//            videoView.setVisibility(View.VISIBLE);
            btnPlay.setBackgroundResource(R.drawable.play_icon);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (videoViewFull != null && videoViewFull.isPlaying()) {
                videoViewFull.pause();
            }
        } else super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoViewFull != null && videoViewFull.isPlaying()) {
//            videoViewFull.stopPlayback();
            videoViewFull.stop();
            videoViewFull = null;
        }
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        player.pause();
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }
}
