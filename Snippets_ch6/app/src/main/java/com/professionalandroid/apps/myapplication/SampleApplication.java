package com.professionalandroid.apps.myapplication;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * @author Administrator  on 2020/5/15.
 */
public class SampleApplication extends Application {
    public static final String TAG = "SampleApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        registerNotificationChannels();
    }

    /**
     * TODO Android 8.0 之后注册通知渠道
     */
    private void registerNotificationChannels() {
        final int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt < Build.VERSION_CODES.O) {
            return;
        }
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e(TAG, "registerNotificationChannels: ", new NullPointerException("notificationManager == null"));
            return;
        }
        final String channelId = BuildConfig.APPLICATION_ID;
        final String channelName = "通知地震消息";
        final int importance = NotificationManager.IMPORTANCE_DEFAULT;
        final String description = "及时向用户通知最新的消息";

        final NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }
}
