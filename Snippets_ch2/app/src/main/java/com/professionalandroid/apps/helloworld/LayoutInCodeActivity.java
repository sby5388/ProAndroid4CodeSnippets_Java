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

package com.professionalandroid.apps.helloworld;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

public class LayoutInCodeActivity extends AppCompatActivity {
    private static final String TAG = "LayoutInCode";

    /*
     * Listing 2-3: Creating layouts in code
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams textViewLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        Resources res = getResources();
        int hpad = res.getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int vpad = res.getDimensionPixelSize(R.dimen.activity_vertical_margin);

        RelativeLayout rl = new RelativeLayout(this);
        rl.setPadding(hpad, vpad, hpad, vpad);

        TextView myTextView = new TextView(this);
        myTextView.setText("Hello World!");

        rl.addView(myTextView, textViewLP);

        addContentView(rl, lp);


        temp();
    }

    /**
     * TODO 三个函数的差别
     * {@link Resources#getDimension(int)}
     * {@link Resources#getDimensionPixelSize(int)}
     * {@link Resources#getDimensionPixelOffset(int)}
     */
    private void temp() {
        // TODO: 2020/4/24 华为 P30 打印的时候 ，60dp -->都是同一个值 180.0 （180）
        Context mContext = this;
        Log.d(TAG, String.format(Locale.getDefault(), "getDimension: %f", mContext.getResources().getDimension(R.dimen.activity_horizontal_margin)));

        Log.d(TAG, String.format(Locale.getDefault(), "getDimensionPixelSize: %d", mContext.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin)));

        Log.d(TAG, String.format(Locale.getDefault(), "getDimensionPixelOffset:%d", mContext.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin)));
        /*
         作者：大浪捉鱼
         链接：https:
         //TODO http://www.jianshu.com/p/282032797637
         来源：简书
         著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
         */
    }
}
