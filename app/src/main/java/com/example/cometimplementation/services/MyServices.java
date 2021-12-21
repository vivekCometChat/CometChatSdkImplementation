package com.example.cometimplementation.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.CallingActivity;

public class MyServices extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        createNotificationChannel();
        isGettingCall();
        Intent intent1 = new Intent(this, CallingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification notification = new NotificationCompat.Builder(this, "channelId1")
                .setContentTitle("fdfsd")
                .setContentText("dfsf")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void isGettingCall() {
        String TAG = "check";
        String listenerId = "123456";
        CometChat.addCallListener(listenerId, new CometChat.CallListener() {
            @Override
            public void onIncomingCallReceived(com.cometchat.pro.core.Call call) {
                Log.d(TAG, "Incoming call: " + call.toString());
                createNotificationChannel();

            }

            @Override
            public void onOutgoingCallAccepted(com.cometchat.pro.core.Call call) {
                Log.d(TAG, "Outgoing call accepted: " +
                        call.toString());
            }

            @Override
            public void onOutgoingCallRejected(com.cometchat.pro.core.Call call) {
                Log.d(TAG, "Outgoing call rejected: " +
                        call.toString());
            }

            @Override
            public void onIncomingCallCancelled(Call call) {
                Log.d(TAG, "Incoming call cancelled: " +
                        call.toString());

//                stopForeground(true);
//                stopSelf();

            }

        });

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    "channelId1", "Foreground notification", NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();

        super.onDestroy();
    }
}
