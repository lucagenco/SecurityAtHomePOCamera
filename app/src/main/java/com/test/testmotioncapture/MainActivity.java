package com.test.testmotioncapture;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {
    private TextView txtStatus;
    private TextView txtResult;
    private TextView txtTimer;
    private MotionDetector motionDetector;
    private MediaPlayer mp;
    private SurfaceView surfaceView;
    private String status = "calm";
    private boolean isPrevent = false;
    private int limit = 0;
    private boolean canceled = false;
    private Button btn_cancel;
    private int timer = 0;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        txtStatus = findViewById(R.id.txtStatus);
        txtResult = findViewById(R.id.txtResult);
        btn_cancel = findViewById(R.id.cancel_btn);
        txtTimer = findViewById(R.id.txtTimer);

        mp = MediaPlayer.create(this, R.raw.movment);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 4, TimeUnit.SECONDS);

        btn_cancel.setVisibility(View.GONE);
        txtTimer.setVisibility(View.GONE);

        Request request = new Request.Builder().url("ws://localhost:8080/SocketSendToken/actions").build();
        WebSocketListener webSocketListener = new WebSocketListener();
        WebSocket ws = client.newWebSocket(request, webSocketListener);
        client.dispatcher().executorService().shutdown();

        motionDetector = new MotionDetector(this, surfaceView);
        motionDetector.setMotionDetectorCallback(new MotionDetectorCallback() {
            @Override
            public void onMotionDetected() {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(80);
                limit++;
                txtStatus.setText(limit + "");
                if(limit == 1){
                    status = "calm";
                }else if(limit > 1 && limit < 4){
                    status = "presence";
                }else if(limit > 3){
                    status = "things detected";
                    if(!isPrevent){
                        preventAgent();
                        isPrevent = true;
                    }
                    mp.start();
                    btn_cancel.setVisibility(View.VISIBLE);
                }
                txtResult.setText(status);
            }

            @Override
            public void onTooDark() {
                txtStatus.setText("Too dark here");
            }
        });

        ////// Config Options
        motionDetector.setCheckInterval(500);
        motionDetector.setLeniency(5);
        motionDetector.setMinLuma(1000);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceled = true;
                txtTimer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        motionDetector.onResume();

        if (motionDetector.checkCameraHardware()) {
            txtStatus.setText("Camera found");
        } else {
            txtStatus.setText("No camera available");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        motionDetector.onPause();
    }

    Runnable helloRunnable = new Runnable() {
        public void run() {
            limit = 0;
            txtStatus.setText(limit + "");
            txtResult.setText("calm");
        }
    };

    private void preventAgent(){
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(timerS, 0, 1, TimeUnit.SECONDS);
        txtTimer.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!canceled){
                    Toast.makeText(getApplicationContext(), "Agent prévenus !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Alarme annulé !", Toast.LENGTH_SHORT).show();
                }
                executor.shutdown();
            }
        }, 5000);
    }

    Runnable timerS = new Runnable() {
        public void run() {
            txtTimer.setText(timer + "");
            timer++;
        }
    };

}