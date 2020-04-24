/*
 * Professional Android, 4th Edition
 * Reto Meier and Ian Lake
 * Copyright 2018 John Wiley Wiley & Sons, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.professionalandroid.apps.chapter4application;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MyActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(android.R.id.text1);
        Log.d(TAG, "onCreate: ");
    }

    /*
     * Listing 4-5: Handling configuration changes in code
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // [ ... Update any UI based on resource values ... ]

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // [ ... React to different orientation ... ]
            Log.d(TAG, "onConfigurationChanged: 横屏");
            mTextView.setText(R.string.temp_text);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // [ ... React to different orientation ... ]
            Log.d(TAG, "onConfigurationChanged: 竖屏");
            mTextView.setText(R.string.temp_text);
        }

        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            // [ ... React to changed keyboard visibility ... ]
            Log.d(TAG, "onConfigurationChanged: KEYBOARDHIDDEN_NO");
        }
    }
}
