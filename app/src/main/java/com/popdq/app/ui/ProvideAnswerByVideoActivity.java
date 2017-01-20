package com.popdq.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialcamera.MaterialCamera;
import com.popdq.app.R;
import com.popdq.app.model.FileModel;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProvideAnswerByVideoActivity extends BaseProvideAnswerActivity implements View.OnClickListener {
    private final static int CAMERA_RQ = 6969;
    private final static int PERMISSION_RQ = 84;

    private static final int VIDEO_CAPTURE = 101;
    private static final int LIST_PERMISSION = 5;
    private LinearLayout btnRecord, btnStop, btnPlay;
    private SeekBar seekbar;
    private VideoView videoView;
    private Chronometer mChronometer = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                int duration = videoView.getCurrentPosition();
                seekbar.setProgress(Math.round(videoView.getCurrentPosition() / 100));
            } catch (Exception e) {
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> listPermissionDenice = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                listPermissionDenice.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                listPermissionDenice.add(Manifest.permission.CAMERA);

            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                listPermissionDenice.add(Manifest.permission.RECORD_AUDIO);
            }
            if (listPermissionDenice.size() > 0) {
                ActivityCompat.requestPermissions(this, listPermissionDenice.toArray(new String[listPermissionDenice.size()]),
                        LIST_PERMISSION);
                return;
            }
        }
        init();


//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                    != PackageManager.PERMISSION_GRANTED && Utils.checkPermission(this)) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CAMERA},
//                        ContentQuestionActivity.CAMERA_PERMISSION);
//                return;
//            }
//            if (!Utils.checkPermission(this)) {
//                return;
//            }
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        ContentQuestionActivity.RECORD_REQUEST_CODE);
//                return;
//            }
//            init();
//        } else {
//            init();
//        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED && Utils.checkPermission(this)) {
//                reply(method);
//            } else {
//                return;
//            }
//        } else {
//            reply(method);
//        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LIST_PERMISSION)
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && Utils.checkPermission(this) && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                finish();
            }
    }

    public void init() {
        getDataFromIntent();
        setContentView(R.layout.activity_provide_answer_by_video);
        btnRecord = (LinearLayout) findViewById(R.id.btnRecord);
        btnStop = (LinearLayout) findViewById(R.id.btnStop);
        btnPlay = (LinearLayout) findViewById(R.id.btnPlay);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        videoView = (VideoView) findViewById(R.id.videoView);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        btnRecord.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        Utils.setBottomButton(this, getString(R.string.submit_answer), this);
        setTitleBarText(getString(R.string.provide_video_answer));
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int values = seekbar.getProgress();
                Log.e("seek", progress + ".." + fromUser);
                if (videoView != null && fromUser) {
                    videoView.seekTo(values * 100);
                    mChronometer.setText(Utils.getDurationFromLong(values * 100));

                } else {
                    mChronometer.setText(Utils.getDurationFromLong(values * 100));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    String videoName;
    File saveDir = null;

    public void startRecord() {
        videoName = System.currentTimeMillis() + ".mp4";
        File mediaFile =
                new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + videoName);
        mediaFile.delete();

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        Uri videoUri = Uri.fromFile(mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    public void startRecordUseLib() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "PopDQ");
            saveDir.mkdirs();
        }


        new MaterialCamera(this)
                .saveDir(saveDir)
                .qualityProfile(MaterialCamera.QUALITY_480P)
                .forceCamera1()
                .allowRetry(true)
                .autoSubmit(false)
                .showPortraitWarning(false)
                .countdownImmediately(false)
                .countdownMillis(60000)
                .defaultToFrontFacing(true)
                .start(CAMERA_RQ);
    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else {
            return false;
        }

    }

    private String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String fileSize(File file) {
        return readableFileSize(file.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RQ) {
            if (resultCode == RESULT_OK) {
                final File file = new File(data.getData().getPath());
                Toast.makeText(this, String.format("Saved to: %s, size: %s",
                        file.getAbsolutePath(), fileSize(file)), Toast.LENGTH_LONG).show();
                MediaPlayer mp = MediaPlayer.create(this, Uri.parse(file.toString()));
                int duration = mp.getDuration();

                videoView.setVideoPath(file.toString());
                FileModel fileModel = new FileModel(file.toString(), file.length(), duration);
                fileModels.add(fileModel);
                seekbar.setMax(Math.round(duration / 100));
//9643 dur, 1124374 length
                mChronometer.setText(Utils.getDurationFromLong(duration));
                loadedAnswer = true;

            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }


//        String realPath = "";
//        try {
//            realPath = RealPathUtil.getRealPathAll(this, data.getData());
//        } catch (Exception e) {
//
//        }
//        if (realPath == null || realPath.equals("") && data != null && data.getData() != null) {
//            realPath = data.getData().toString();
//        }
//        if (requestCode == VIDEO_CAPTURE) {
//            if (resultCode == RESULT_OK) {
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + videoName;
//                File file = new File(path);
//                long length = file.length();
//                MediaPlayer mp = MediaPlayer.create(this, Uri.parse(path));
//                int duration = mp.getDuration();
//
//                videoView.setVideoPath(path);
//                FileModel fileModel = new FileModel(path, length, duration);
//                fileModels.add(fileModel);
//                seekbar.setMax(Math.round(duration / 1000));
////9643 dur, 1124374 length
//                mChronometer.setText(Utils.getDurationFromLong(duration));
//                loadedAnswer = true;
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Video recording cancelled.",
//                        Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Failed to record video",
//                        Toast.LENGTH_LONG).show();
//            }
//        }
    }

    TimerTask timerTask;
    Timer timer;
    private boolean loadedAnswer = false;

    @Override
    protected boolean hasContent() {
        if (seekbar.getMax() > 0) {
            return true;
        } else return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRecord:
//                startRecord();
                if (loadedAnswer) {
                    final DialogBase dialogBase = new DialogBase(this);
                    dialogBase.setMessage(getString(R.string.do_you_want_to_record_again));
                    dialogBase.setOnClickOkListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startRecordUseLib();

                        }
                    });
                    dialogBase.setOnClickCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBase.dismiss();
                        }
                    });
                    dialogBase.setTitle(getString(R.string.notice));
                    dialogBase.show();
                } else
                    startRecordUseLib();
                break;
            case R.id.btnBottom:
                if(fileModels == null || fileModels.isEmpty()){
                    Toast.makeText(this,"Please record your answer first!",Toast.LENGTH_LONG).show();
                } else {
                    submitAnswer("video");
                }

                break;
            case R.id.btnPlay:
                if (loadedAnswer) {
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(1);
                        }
                    };
                    timer = new Timer();
                    timer.scheduleAtFixedRate(timerTask, 0, Values.PER_UPDATE_SEEKBAR);
                    videoView.start();
                } else {
                    Toast.makeText(getApplicationContext(), "Loading Answer....", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnStop:
                if (videoView != null && videoView.isPlaying()) {
                    videoView.pause();
                }
                break;
        }
    }
}

