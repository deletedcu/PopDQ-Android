package com.popdq.app.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.popdq.app.Horizon;
import com.popdq.app.R;
import com.popdq.app.fragment.RecordFragment;
import com.popdq.app.model.FileModel;
import com.popdq.app.service.RecordingService;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ProvideAnswerByVoiceActivityBackup extends BaseProvideAnswerActivity implements View.OnClickListener {
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();
    private LinearLayout btnRecord, btnStop, btnPlay;
    private GLSurfaceView surfaceView;
    private Chronometer mChronometer = null;
    long timeWhenPaused = 0;
    RecordingService recordingService;

    private boolean isStartRecord = false;
    private boolean isRecordDone = false;
    private boolean isPlaying = false;
    private String title;
    private static final int RECORDER_SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNELS = 1;
    private static final int RECORDER_ENCODING_BIT = 16;
    private CountDownTimer countDownTimer;

    Horizon mHorizon;
    private SeekBar seekbar;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                int currentPossition = Math.round(mediaPlayer.getCurrentPosition() / 100);
                seekbar.setProgress(currentPossition);
                mChronometer.setText(Utils.getDurationFromLong(currentPossition * 100));
            } catch (Exception e) {

            }
        }

    };
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            recordingService = ((RecordingService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    TimerTask timerTask;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_answer_by_voice_record);
        getDataFromIntent();
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        btnRecord = (LinearLayout) findViewById(R.id.btnRecord);
        btnStop = (LinearLayout) findViewById(R.id.btnStop);
        btnPlay = (LinearLayout) findViewById(R.id.btnPlay);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        btnRecord.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        Intent intent = new Intent(this, RecordingService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
//        surfaceView = (GLSurfaceView) findViewById(R.id.surfaceView);
        mHorizon = new Horizon(surfaceView, getResources().getColor(R.color.button_login_facebook_press),
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);

        Utils.setBottomButton(this, getString(R.string.submit_answer), this);
        setTitleBarText(getString(R.string.provide_voice_answer));
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int values = seekbar.getProgress();
                Log.e("seek", progress + ".." + fromUser);
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(values * 100);
                    mChronometer.setText(Utils.getDurationFromLong(values * 100));

                } else {
//                    if()

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mChronometer.setText("01:00");
    }

    @Override
    protected boolean hasContent() {
        if (isRecordDone || isStartRecord || isPlaying) {
            return true;
        } else return false;
    }

    public void startRecord() {
        Toast.makeText(this, R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
        File folder = new File(Environment.getExternalStorageDirectory() + "/azpop");
        if (!folder.exists()) {
            folder.mkdir();
        }
//        mChronometer.setBase(SystemClock.elapsedRealtime());
//        mChronometer.start();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        try {
            recordingService.startRecording();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                mChronometer.setText(Utils.getDurationFromLong(millisUntilFinished));
            }

            public void onFinish() {
                btnStop();
//                mChronometer.setText("done!");
            }
        };
        countDownTimer.start();
    }

    public void stopRecord() {
        countDownTimer.cancel();
//        mChronometer.stop();
//        mChronometer.setBase(SystemClock.elapsedRealtime());
        timeWhenPaused = 0;
        recordingService.stopRecording();
//        Toast.makeText(ProvideAnswerByVoiceActivity.this, recordingService.getmFilePath(), Toast.LENGTH_SHORT).show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    public void sendAnswer(String pathFileRecord) {
        File file = new File(pathFileRecord);
        int duration = 0;
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(pathFileRecord);

        String d = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Integer.parseInt(d);
        FileModel fileModel = new FileModel(pathFileRecord, file.length(), duration);
        fileModels = new ArrayList<>();
        fileModels.add(fileModel);
        submitAnswer("voice");
    }

    public void record() {
        isStartRecord = true;
        startRecord();
        isRecordDone = false;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (seekbar != null) {
            seekbar.post(new Runnable() {
                @Override
                public void run() {
                    seekbar.setProgress(0);
                }
            });
        }
    }

    MediaPlayer mediaPlayer;

    public void btnStop() {
        if (isStartRecord && !isRecordDone) {
            stopRecord();
            isRecordDone = true;
            isStartRecord = false;
//                    mediaPlayer = new MediaPlayer();
//                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(recordingService.getmFilePath()));
                int duration = (mediaPlayer.getDuration() / 1000) * 10;
//                        seekbar.setProgress(0);
//                        mChronometer.setText("00:00");
                seekbar.setMax(duration);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int audioSessionID = mediaPlayer.getAudioSessionId();

            visualizer = new Visualizer(audioSessionID);
            setVisualizer();
        }

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
        isPlaying = false;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRecord:
                if (isStartRecord) {
                    btnRecord.setBackgroundResource(R.drawable.record_icon);
                    btnStop();
                } else {
                    btnRecord.setBackgroundResource(R.drawable.stop_icon);
                    if (!isStartRecord && isRecordDone) {
                        final DialogBase dialogBase = new DialogBase(this);
                        dialogBase.setTitle(getString(R.string.notice));
                        dialogBase.setMessage(getString(R.string.do_you_want_to_record_again));
                        dialogBase.getBtnOk().setText("OK");
                        dialogBase.setOnClickOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnStop();
                                seekbar.setProgress(0);
                                record();
                                if (dialogBase != null && dialogBase.isShowing())
                                    dialogBase.dismiss();
                                return;

                            }
                        });
                        dialogBase.setOnClickCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dialogBase != null && dialogBase.isShowing())
                                    dialogBase.dismiss();
                            }
                        });
                        try {
                            dialogBase.show();

                        } catch (Exception e) {

                        }
                        return;
                    }
                    if (isPlaying) {
                        Toast.makeText(ProvideAnswerByVoiceActivityBackup.this, "Playing last record!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (!isStartRecord) {
                        record();
                    }
                }

                break;
            case R.id.btnStop:
                btnStop();

                break;

            case R.id.btnPlay:
                if (isRecordDone) {

                    if (!isPlaying) {
                        btnPlay.setBackgroundResource(R.drawable.pause_icon);
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        };
                        timer = new Timer();
                        timer.schedule(timerTask, 0, Values.PER_UPDATE_SEEKBAR);
                        try {
                            mediaPlayer.prepare();
                        } catch (IllegalStateException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                        }
                        mediaPlayer.start();
//                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                isPlaying = false;
//                            }
//                        });
                        isPlaying = true;
                        Toast.makeText(ProvideAnswerByVoiceActivityBackup.this, "Play", Toast.LENGTH_SHORT).show();

                    } else {
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
                        isPlaying = false;
                        Toast.makeText(ProvideAnswerByVoiceActivityBackup.this, "Pause", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Play error!", Toast.LENGTH_LONG).show();

                }

                break;

            case R.id.btnBottom:
                if (recordingService.getmFilePath() == null) {
                    Toast.makeText(ProvideAnswerByVoiceActivityBackup.this, "Recorder error!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendAnswer(recordingService.getmFilePath());
                break;
        }
    }

    Visualizer visualizer;

    public void setVisualizer() {

        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mHorizon.updateView(bytes);
                // Send bytes (waveform) to your custom UI view to show it.
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
        visualizer.setEnabled(true);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                visualizer.setEnabled(false);
                isPlaying = false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(serviceConnection);
        } catch (Exception e) {

        }
    }
}

