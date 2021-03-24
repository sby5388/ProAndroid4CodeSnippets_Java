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

package com.professionalandroid.apps.weatherstation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherStationActivity extends AppCompatActivity {
    public static final String TAG = "WeatherStation";

    private SensorManager mSensorManager;
    private TextView mTemperatureTextView;
    private TextView mPressureTextView;
    private TextView mHumidityTextView;
    private TextView mLightTextView;

    private float mLastTemperature = Float.NaN;
    private float mLastPressure = Float.NaN;
    private float mLastLight = Float.NaN;
    private float mLastHumidity = Float.NaN;

    private Timer mUpdateTimer;

    private final SensorEventListener mSensorEventListener
            = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case (Sensor.TYPE_AMBIENT_TEMPERATURE):
                    mLastTemperature = event.values[0];
                    break;
                case (Sensor.TYPE_RELATIVE_HUMIDITY):
                    mLastHumidity = event.values[0];
                    break;
                case (Sensor.TYPE_PRESSURE):
                    mLastPressure = event.values[0];
                    break;
                case (Sensor.TYPE_LIGHT):
                    mLastLight = event.values[0];
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_station);

        mTemperatureTextView = findViewById(R.id.temperature);
        mPressureTextView = findViewById(R.id.pressure);
        mLightTextView = findViewById(R.id.light);
        mHumidityTextView = findViewById(R.id.humidity);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // TODO: 2021/2/19 定时器的使用
        mUpdateTimer = new Timer("weatherUpdate");
        mUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            mSensorManager.registerListener(mSensorEventListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mLightTextView.setText(R.string.light_sensor_unavailable);
        }

        Sensor pressureSensor =
                mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor != null) {
            mSensorManager.registerListener(mSensorEventListener,
                    pressureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mPressureTextView.setText(R.string.barometer_unavailable);
        }

        Sensor temperatureSensor =
                mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (temperatureSensor != null) {
            mSensorManager.registerListener(mSensorEventListener,
                    temperatureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mTemperatureTextView.setText(R.string.thermometer_unavailable);
        }

        Sensor humiditySensor =
                mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (humiditySensor != null) {
            mSensorManager.registerListener(mSensorEventListener,
                    humiditySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mHumidityTextView.setText(R.string.humidity_sensor_unavailable);
        }
        temp();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorEventListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mUpdateTimer != null) {
            // TODO: 2021/2/19 需要及时取消定时任务
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
        super.onDestroy();
    }

    private void updateGUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!Float.isNaN(mLastPressure)) {
                    mPressureTextView.setText(mLastPressure + "hPa");
                    mPressureTextView.invalidate();
                }
                Log.d(TAG, "run: mLastLight = " + mLastLight);
                if (!Float.isNaN(mLastLight)) {
                    String lightStr = "Sunny";
                    if (mLastLight <= SensorManager.LIGHT_CLOUDY)
                        lightStr = "Night";
                    else if (mLastLight <= SensorManager.LIGHT_OVERCAST)
                        lightStr = "Cloudy";
                    else if (mLastLight <= SensorManager.LIGHT_SUNLIGHT)
                        lightStr = "Overcast";
                    mLightTextView.setText(lightStr);
                    mLightTextView.invalidate();
                }

                if (!Float.isNaN(mLastTemperature)) {
                    mTemperatureTextView.setText(mLastTemperature + "C");
                    mTemperatureTextView.invalidate();
                }

                if (!Float.isNaN(mLastHumidity)) {
                    mHumidityTextView.setText(mLastHumidity + "% Rel. Humidity");
                    mHumidityTextView.invalidate();
                }
            }
        });
    }


    private void temp() {
        //陀螺仪
        final Sensor gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Log.d(TAG, "temp: gyroscope==null ? " + (gyroscope == null));

        //获取所有的传感器列表
        final List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                Log.d(TAG, "sensor: " + sensor.getName() + "，" + sensor.getStringType());
            } else {
                Log.d(TAG, "sensor: " + sensor.getName());
            }
        }


    }
}
