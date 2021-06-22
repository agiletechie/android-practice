package com.example.recorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private Button recordBtn,stopBtn,playBtn,playStopBtn;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private static final String LOG_TAG = "Audio Recording";
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordBtn = findViewById(R.id.record);
        stopBtn = findViewById(R.id.stop);
        playBtn = findViewById(R.id.play);
        playStopBtn = findViewById(R.id.playStop);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        playStopBtn.setEnabled(false);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/AudioRecording.3gp";

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermissions()){
                    recordBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.setOutputFile(mFileName);
                    try {
                        mediaRecorder.prepare();
                    }
                    catch (IOException ex){
                        Log.e(LOG_TAG,"prepare() failed");
                    }
                    mediaRecorder.start();
                    Toast.makeText(getApplicationContext(),"Recording Started",Toast.LENGTH_LONG).show();
                }
                else {
                    RequestPermissions();
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBtn.setEnabled(false);
                recordBtn.setEnabled(true);
                playBtn.setEnabled(true);
                playStopBtn.setEnabled(true);
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Toast.makeText(getApplicationContext(),"Recording Stopped",Toast.LENGTH_LONG).show();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBtn.setEnabled(false);
                playStopBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                recordBtn.setEnabled(true);
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(mFileName);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(),"Playing Audio",Toast.LENGTH_LONG).show();
                }
                catch (IOException ex){
                    Log.e(LOG_TAG,"prepare() failed");
                }
            }
        });

        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playStopBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                recordBtn.setEnabled(true);
                playBtn.setEnabled(true);
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                Toast.makeText(getApplicationContext(),"Stopped playing audio",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_AUDIO_PERMISSION_CODE:
                if(grantResults.length > 0){
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(permissionToRecord && permissionToStore){
                        Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean CheckPermissions(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),RECORD_AUDIO);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions(){
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[] {RECORD_AUDIO,WRITE_EXTERNAL_STORAGE},
                REQUEST_AUDIO_PERMISSION_CODE);
    }
}