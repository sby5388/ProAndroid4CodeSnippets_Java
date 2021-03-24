package com.by5388.demo.mediademo0.video.player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.by5388.demo.mediademo0.R;

/**
 * @author Administrator  on 2020/8/7.
 */
public class VideoPlayerFragment extends Fragment implements View.OnClickListener {
    private Button mButtonPlay;
    private Button mButtonSkip;
    private Button mButtonPause;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;

    private MediaPlayer mMediaPlayer;


    public static VideoPlayerFragment newInstance() {
        return new VideoPlayerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonPlay = view.findViewById(R.id.button_video_play);
        mButtonSkip = view.findViewById(R.id.button_video_skip);
        mButtonPause = view.findViewById(R.id.button_video_pause);

        mButtonPlay.setOnClickListener(this);
        mButtonSkip.setOnClickListener(this);
        mButtonPause.setOnClickListener(this);
        mSurfaceView = view.findViewById(R.id.surface_view_video);
        mHolder = mSurfaceView.getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(createSurfaceViewHolderCallback());
    }


    @Override
    public void onClick(View v) {
        if (v == mButtonPlay) {
            final boolean playing = mMediaPlayer.isPlaying();
            if (!playing) {
                mMediaPlayer.start();
            }
        } else if (v == mButtonPause) {
            final boolean playing = mMediaPlayer.isPlaying();
            if (playing) {
                mMediaPlayer.pause();
            }
        } else if (v == mButtonSkip) {
            //跳到一半
            final int duration = mMediaPlayer.getDuration();
            mMediaPlayer.seekTo(duration / 2);

        }
    }

    private SurfaceHolder.Callback createSurfaceViewHolderCallback() {
        return new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO: 2020/8/8
//                mMediaPlayer.setDisplay(holder);
//                mMediaPlayer.setDataSource();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        };
    }
}
