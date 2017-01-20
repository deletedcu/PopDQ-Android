package com.popdq.app.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.yalantis.audio.lib.AudioUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Daniel on 12/28/2014.
 */
public class RecordingServiceBackup extends Service {



//    private void startTimer() {
//        mTimer = new Timer();
//        mIncrementTimerTask = new TimerTask() {
//            @Override
//            public void run() {
//                mElapsedSeconds++;
//                if (onTimerChangedListener != null)
//                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
//                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mgr.notify(1, createNotification());
//            }
//        };
//        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
//    }
//
//    //TODO:
//    private Notification createNotification() {
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.ic_mic_white_36dp)
//                        .setContentTitle(getString(R.string.notification_recording))
//                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
//                        .setOngoing(true);
//
//        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
//                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));
//
//        return mBuilder.build();
//    }
private String TAG = "RecordingService";
    private static MediaRecorder mRecorder;
    private static MediaPlayer mPlayer;

    private static String audioFilePath;
    private static String audioFilePathTemp;

    private boolean isRecording = false;

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    public static final int RECORDER_SAMPLE_RATE = 44100;
    public static final int RECORDER_CHANNELS = 12;
    public static final int RECORDER_ENCODING_BIT = 16;
    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;

    String filePlace;
    //    private Horizon mHorizon;
    private byte[] buffer;
    private GLSurfaceView glSurfaceView;

//    public void setmHorizon(Horizon mHorizon) {
//        this.mHorizon = mHorizon;
//    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
    }

    public class MyBinder extends Binder {
        public RecordingServiceBackup getService() {
            return RecordingServiceBackup.this;
        }
    }


    public MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    public AudioRecord getRecorder() {
        return recorder;
    }


    private AudioRecord.OnRecordPositionUpdateListener recordPositionUpdateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        @Override
        public void onMarkerReached(AudioRecord recorder) {
            //empty for now
        }

        @Override
        public void onPeriodicNotification(AudioRecord recorder) {
            if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
                    && recorder.read(buffer, 0, buffer.length) != -1) {
//                mHorizon.updateView(buffer);
                Log.e(TAG, buffer.length + "");

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_STICKY;
    }

    public void init() {
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "myaudio.wav";//"/myaudio.3gp";
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void stopRecording() {


        if (null != recorder) {
            isRecording = false;

            recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        filePlace = getFilename();
        copyWaveFile(getTempFilename(), getFilename());//copies the latest recording to the permanent folder.
        deleteTempFile();

    }


    public void startRecording() throws IOException {
        final int bufferSize = 2 * AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        isRecording = true;

        //enables/disables appropriate buttons


        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
        AudioUtil.initProcessor(RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);
//

        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                buffer = new byte[bufferSize];
//                Looper.prepare();
//                recorder.setRecordPositionUpdateListener(recordPositionUpdateListener, new Handler(Looper.myLooper()));
//                int bytePerSample = RECORDER_ENCODING_BIT / 8;
//                float samplesToDraw = bufferSize / bytePerSample;
//                recorder.setPositionNotificationPeriod((int) samplesToDraw);
//
//                //We need to read first chunk to motivate recordPositionUpdateListener.
//                //Mostly, for lower versions - https://code.google.com/p/android/issues/detail?id=53996
////                recorder.read(buffer, 0, bufferSize);
//                Looper.loop();
//            }
//        });
//


        recordingThread.start();

//        thread.start();

//        try {
//            int t = recorder.getAudioSessionId();
//            mVisualizer = new Visualizer(t);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public Visualizer mVisualizer;

    public String getmFilePath() {
        return audioFilePath;
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];

        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if (null != os) {
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "myAudioFile" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            //AppLog.logString("File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }



}