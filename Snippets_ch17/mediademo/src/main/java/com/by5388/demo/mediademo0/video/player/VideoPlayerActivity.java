package com.by5388.demo.mediademo0.video.player;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.by5388.demo.mediademo0.BaseSingleFragmentActivity;

/**
 * @author Administrator  on 2020/8/7.
 */
public class VideoPlayerActivity extends BaseSingleFragmentActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, VideoPlayerActivity.class);
    }

    @Override
    public Fragment createFragment() {
        return VideoPlayerFragment.newInstance();
    }
}
