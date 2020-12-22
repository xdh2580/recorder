package com.example.recorder;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationUtil {
    private static final int NOTIFICATION_MUSIC_ID = 10000;
    private static NotificationManager notificationManager;


    //初始化NotificationManager
    private static void initNotificationManager(Context context){
        if (notificationManager == null){
            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        }
        //判断是否为8.0以上：Build.VERSION_CODES.O为26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道ID
            String channelId = "musicNotification";
            //创建通知渠道名称
            String channelName = "音乐播放器通知栏";
            //创建通知渠道重要性
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(context, channelId, channelName, importance);
        }
    }

    //创建通知渠道
    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context, String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        //channel有很多set方法

        //为NotificationManager设置通知渠道
        notificationManager.createNotificationChannel(channel);
    }


}
