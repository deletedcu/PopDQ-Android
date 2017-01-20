package com.popdq.app.ui;

import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.popdq.app.R;
import com.popdq.app.model.Answer;
import com.popdq.app.model.Question;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ViewAnswerVoiceRecordActivity extends BaseViewAnswerActivity implements View.OnClickListener {
    private SeekBar seekbar;
    private TextViewNormal tvDuration;
    private LinearLayout btnPlay, btnPause;
    private MediaPlayer mediaPlayer;
    private int maxDuration;
    private ImageView imgAnswer;
    private boolean loadedAnswer = false;
//    private GLSurfaceView surfaceView;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                seekbar.setProgress(Math.round(mediaPlayer.getCurrentPosition() / 100));
            } catch (Exception e) {

            }
        }

    };
    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 1;

    private static final int RECORDER_SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNELS = 1;
    private static final int RECORDER_ENCODING_BIT = 16;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MAX_DECIBELS = 120;
//    Horizon mHorizon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO},
//                    ContentQuestionActivity.RECORD_REQUEST_CODE_VISUAL);
//            return;
//        } else {
            init();
//        }


    }

    public void init() {
        setContentView(R.layout.activity_view_answer_voice_record);
        Utils.checkStartActivityFromNotificationAndSendRead(this, null);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvDuration = (TextViewNormal) findViewById(R.id.tvDuration);
        btnPlay = (LinearLayout) findViewById(R.id.btnPlay);
        btnPause = (LinearLayout) findViewById(R.id.btnPause);
        imgAnswer = (ImageView) findViewById(R.id.imgAnswer);
        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        setRateListener();
        Utils.setActionBar(this, getString(R.string.audio_answer), R.drawable.btn_back);
        getData(new OnLoadAnswerListener() {
            @Override
            public void onSuccess(Answer answer) {

                Question.Attachments attachments = answer.getAttachments()[0];
                long dur = (long) attachments.info.getDuration();
                ((TextViewNormal) findViewById(R.id.tvMaxDuration)).setText(Utils.getDurationFromLong(dur));
                tvDuration.setText(Utils.getDurationFromLong(dur));
                mediaPlayer = new MediaPlayer();
                maxDuration = (int) attachments.info.getDuration() / 100;
                seekbar.setMax(maxDuration);
                try {
                    String url = Values.BASE_URL_AVATAR + attachments.link;
                    mediaPlayer.setDataSource(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loadedAnswer = true;

            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int values = seekbar.getProgress();
                Log.e("seek", progress + ".." + fromUser);
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(values * 100);
                    tvDuration.setText(Utils.getDurationFromLong(values * 100));

                } else {
                    tvDuration.setText(Utils.getDurationFromLong(values * 100));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        if (!getIntentNotificationBar().getBooleanExtra(Values.isViewed, false))
//            ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Utils.showDialogShare(ViewAnswerVoiceRecordActivity.this);
//                }
//            });
    }

//    Visualizer visualizer;
//
//    public void setVisualizer() {
//
//        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
//        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
//            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
//                                              int samplingRate) {
//                mHorizon.updateView(bytes);
//                // Send bytes (waveform) to your custom UI view to show it.
//            }
//
//            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//            }
//        }, Visualizer.getMaxCaptureRate() / 2, true, false);
//        visualizer.setEnabled(true);
//
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                visualizer.setEnabled(false);
//            }
//        });
//
//    }

    TimerTask timerTask;
    Timer timer;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                if (mediaPlayer.isPlaying()) {
                    btnPlay.setBackgroundResource(R.drawable.play_icon);
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                    }
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                } else {
                    btnPlay.setBackgroundResource(R.drawable.pause_icon);
                    if (loadedAnswer) {
//                        if (visualizer != null) {
//                            visualizer.setEnabled(true);
//                        }
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        };
                        timer = new Timer();
                        timer.scheduleAtFixedRate(timerTask, 0, Values.PER_UPDATE_SEEKBAR);
                        try {
                            mediaPlayer.prepare();
                        } catch (IllegalStateException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                        }
                        mediaPlayer.start();
                    } else {
                        Toast.makeText(getApplicationContext(), "Loading Answer....", Toast.LENGTH_LONG).show();

                    }
                }

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
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == ContentQuestionActivity.RECORD_REQUEST_CODE_VISUAL)
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                init();
//            } else {
//                finish();
//                return;
//            }
//    }
}
