package com.by5388.demo.mediademo0;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.by5388.demo.mediademo0.camera.CameraActivity;
import com.by5388.demo.mediademo0.video.player.VideoPlayerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PackageManager pm = getPackageManager();
        final Button buttonCamera = findViewById(R.id.button_camera);
        buttonCamera.setEnabled(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY));
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(CameraActivity.newIntent(MainActivity.this));
            }
        });
        final Button buttonVideoPlayer = findViewById(R.id.button_video_player);
        buttonVideoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(VideoPlayerActivity.newIntent(MainActivity.this));
            }
        });


    }
}
