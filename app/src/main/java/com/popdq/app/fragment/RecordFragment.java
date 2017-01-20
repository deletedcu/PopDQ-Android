package com.popdq.app.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.service.RecordingService;
import com.popdq.app.service.RecordingServiceBackup;
import com.popdq.app.view.textview.TextViewNormal;

import java.io.File;
import java.io.IOException;


public class RecordFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();


    //Recording controls
    private FloatingActionButton mRecordButton = null;
//    private Button mPauseButton = null;

    private TextViewNormal mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button

    RecordingServiceBackup recordingService;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            recordingService =((RecordingServiceBackup.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), RecordingService.class);

        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        //update recording prompt text
        mRecordingPrompt = (TextViewNormal) recordView.findViewById(R.id.recording_status_text);

        mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
//        mRecordButton.setColo(getResources().getColor(R.color.primary));
//        mRecordButton.setColorPressed(getResources().getColor(R.color.primary_dark));
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;

            }
        });

//        mPauseButton = (Button) recordView.findViewById(R.id.btnPause);
//        mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
//        mPauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onPauseRecord(mPauseRecording);
//                mPauseRecording = !mPauseRecording;
//            }
//        });

        return recordView;
    }

    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start) {

        Intent intent = new Intent(getActivity(), RecordingService.class);

        if (start) {
            try {
                recordingService.startRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            mPauseButton.setVisibility(View.VISIBLE);
            // start recording
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/azpop");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }

            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });

            //start RecordingService
            getActivity().startService(intent);
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            //stop recording
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));

            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void showDialogRecordDone() {

    }

}