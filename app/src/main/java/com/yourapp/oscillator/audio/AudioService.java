package com.yourapp.oscillator.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.yourapp.oscillator.R;

public class AudioService extends Service {
    private AudioEngine audioEngine;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "audio_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        audioEngine = new AudioEngine();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        audioEngine.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        audioEngine.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "音频播放", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("振荡器")
                .setContentText("正在播放音频")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .build();
    }
}
