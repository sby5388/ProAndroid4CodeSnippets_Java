package com.by5388.demo.mediademo0.camera;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.by5388.demo.mediademo0.BaseSingleFragmentActivity;

/**
 * @author Administrator  on 2020/8/7.
 */
public class CameraActivity extends BaseSingleFragmentActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, CameraActivity.class);
    }

    @Override
    public Fragment createFragment() {
        return CameraFragment.newInstance();
    }
}
