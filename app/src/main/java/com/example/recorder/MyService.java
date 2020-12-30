package com.example.recorder;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {

    boolean isRcording = false;
    boolean isPause = false;
    private static final String ACTION_RECORD = "record";
    private static final String ACTION_FINISH = "finish";
    private static final String ACTION_PAUSE = "pause";
    File audioFile;
    File myPath;
    RemoteViews remoteViews;
    MediaRecorder mediaRecorder = new MediaRecorder();
    MyReceiver myReceiver ;

    private MyBinder myBinder = new MyBinder();


    class MyBinder extends Binder{

        public void startRecord(){


            Log.d("MyLog","binder->startRecord");
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void pauseRecord() throws IOException {
            mediaRecorder.pause();
            mediaRecorder.resume();
            Log.d("MyLog","binder->pauseRecord");

        }

        public void stopRecord(){
            if (isRcording==true) {
                mediaRecorder.stop();
                isRcording = false;
                Log.d("MyLog", "binder->stopRecord");
                Log.d("MyLog", "mediaRecorder->stop()byClick");
            }
        }



    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyLog","MyService.MyBinder->onBind"+this.toString());
        return myBinder;
    }

    @Override
    public void onCreate() {
        Log.d("myLog","MyService->onCrate");
        super.onCreate();
        myPath = new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));

        //注册广播监听
        myReceiver=new MyReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_RECORD);
        mIntentFilter.addAction(ACTION_FINISH);
        mIntentFilter.addAction(ACTION_PAUSE);
        registerReceiver(myReceiver,mIntentFilter);


        // 获取remoteViews（参数一：包名；参数二：布局资源）
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.layout_noti);
        Intent intent = new Intent(this,MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("name1","value777");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.button4,pendingIntent);

//        remoteViews.setTextViewText(R.id.textView11,"正在录音...");


//        Intent intent2 = new Intent(this,MainActivity.class);
//        intent2.putExtra("nana","value666");
//        PendingIntent pendingIntent2 = PendingIntent.getActivity(this,1,intent2,PendingIntent.FLAG_ONE_SHOT);
//        remoteViews.setOnClickPendingIntent(R.id.button8,pendingIntent2);
//
//
//        Intent intent3 = new Intent(this,MainActivity.class);
//        intent3.putExtra("nana2","value888");
//        PendingIntent pendingIntent3 = PendingIntent.getActivity(this,2,intent3,PendingIntent.FLAG_ONE_SHOT);
//        remoteViews.setOnClickPendingIntent(R.id.button9,pendingIntent3);

        Intent intent_bro2 = new Intent(ACTION_RECORD);
        PendingIntent pendingIntent_bro2 = PendingIntent.getBroadcast(this,3,intent_bro2,0);
        remoteViews.setOnClickPendingIntent(R.id.button,pendingIntent_bro2);

        Intent intent_bro0 = new Intent(ACTION_FINISH);
        PendingIntent pendingIntent_bro0 = PendingIntent.getBroadcast(this,1,intent_bro0,0);
        remoteViews.setOnClickPendingIntent(R.id.button2,pendingIntent_bro0);

        Intent intent_bro1 = new Intent(ACTION_PAUSE);
        PendingIntent pendingIntent_bro1 = PendingIntent.getBroadcast(this,2,intent_bro1,0);
        remoteViews.setOnClickPendingIntent(R.id.button3,pendingIntent_bro1);



        remoteViews.setTextViewText(R.id.textView11,"录音中...");
        //设置通知
        Notification notification = new NotificationCompat.Builder(this, "record")
                //   .setAutoCancel(true)
                .setContentTitle("recorder")
                //   .setContentText("正在录音...")
                .setContent(remoteViews)// 设置自定义的Notification内容,布局
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                //   .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //   .setAutoCancel(true)
                // .setContentIntent(pendingIntent)
                //在build()方法之前还可以添加其他方法
                .build();
        startForeground(1,notification);

//开始录音
//        initMediaRecorder();//初始化
        recorder_Media();//开始录音
        TextView textView = MainActivity.instance.findViewById(R.id.textView4);
        textView.setText("录音中...");
        Toast.makeText(MyService.this,"onRecording",Toast.LENGTH_SHORT).show();


//         Notification.Builder builder = new Notification.Builder(this.getApplicationContext())
//                 .setContent(remoteViews);// 设置自定义的Notification内容
//        builder.setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher);
//
//        Notification notification = builder.getNotification();// 获取构建好的通知--.build()最低要求在
//// API16及以上版本上使用，低版本上可以使用.getNotification()。
//
//        startForeground(110, notification);// 开始前台服务
//
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("myLog","MyService->onStartCommand");


//        Intent intent_rec = intent;
//        String str_rec = intent_rec.getStringExtra("name2");
//        if (str_rec!=null){
//            Toast.makeText(this,"str_rec:"+str_rec,Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(this,"str_rec:null",Toast.LENGTH_SHORT).show();
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("myLog","MyService->Destroy");

        if (isRcording==true) {
            mediaRecorder.stop();
            Log.d("MyLog", "mediaRecorder->stop()byDestroyService");
        }
        stopForeground(true);

        if(myReceiver!=null){
            unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }


    public void initMediaRecorder(){

        try {
            SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
            Date curDate =  new Date(System.currentTimeMillis());
            String   time   =   formatter.format(curDate);
            audioFile = File.createTempFile(time, ".mp3", myPath);
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        Log.d("MyLog","mediaRecorder->initMediaRecorder()");

    }

    public void recorder_Media() {


        initMediaRecorder();

        try {

            //    mediaRecorder.reset();
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRcording=true;
            Log.d("MyLog","mediaRecorder->start()");
            //countDownTimer.start();

            //isRecording = true;
            //bt2.setImageDrawable(getResources().getDrawable(R.mipmap.icon5));

            //       Toast.makeText(MainActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
            //       Toast.makeText(MainActivity.this, String.valueOf(isRecording), Toast.LENGTH_SHORT).show();

            //        amplitude.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class MyReceiver extends BroadcastReceiver{


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d("MyLog","receiver->"+action);

            switch (action){
                case ACTION_RECORD:
                    myPath = new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));
                    if(isRcording==false&&isPause==false) {
                        recorder_Media();
                        isRcording=true;
                        isPause=false;
                        TextView textView = MainActivity.instance.findViewById(R.id.textView4);
                        textView.setText("录音中...");
                        remoteViews.setTextViewText(R.id.textView11,"正在录音...");
                        //更新通知
                        Notification notification = new NotificationCompat.Builder(MyService.this, "record")
                                .setContentTitle("recorder")
                                .setContent(remoteViews)// 设置自定义的Notification内容,布局
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
                        startForeground(1,notification);
                        Toast.makeText(MyService.this, "receive,recoding!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MyService.this, "已经在录音中", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ACTION_FINISH:
                    if ((isRcording==true&&isPause==false)||(isRcording==false&&isPause==true)) {
                        mediaRecorder.stop();
                        isRcording = false;
                        isPause=false;
                        //刷新列表
                        Message msg = new Message();
                        msg.what = MainActivity.UPDATE_LIST;
                        MainActivity.instance.handler.sendMessage(msg);
                        TextView textView = MainActivity.instance.findViewById(R.id.textView4);
                        textView.setText("录制完成");
                        remoteViews.setTextViewText(R.id.textView11,"recorder");
                        //更新通知
                        Notification notification = new NotificationCompat.Builder(MyService.this, "record")
                                .setContentTitle("recorder")
                                .setContent(remoteViews)// 设置自定义的Notification内容,布局
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
                        startForeground(1,notification);
                        Toast.makeText(MyService.this,"receive,finish!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MyService.this,"请先开始录音",Toast.LENGTH_SHORT).show();

                    }

                    break;
                case ACTION_PAUSE:
                    if(isPause==true&&isRcording==false){
                        mediaRecorder.resume();
                        isPause = false;
                        isRcording=true;
                        TextView textView = MainActivity.instance.findViewById(R.id.textView4);
                        textView.setText("继续，正在录音...");
                        remoteViews.setTextViewText(R.id.textView11,"正在录音...");
                        //更新通知
                        Notification notification = new NotificationCompat.Builder(MyService.this, "record")
                                .setContentTitle("recorder")
                                .setContent(remoteViews)// 设置自定义的Notification内容,布局
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
                        startForeground(1,notification);
                        Toast.makeText(MyService.this, "receive,resume!", Toast.LENGTH_SHORT).show();
                    }
                    else if (isRcording && !isPause) {
                        mediaRecorder.pause();
                        TextView textView = MainActivity.instance.findViewById(R.id.textView4);
                        textView.setText("暂停中...");
                        remoteViews.setTextViewText(R.id.textView11,"暂停中...");
                        //更新通知
                        Notification notification = new NotificationCompat.Builder(MyService.this, "record")
                                .setContentTitle("recorder")
                                .setContent(remoteViews)// 设置自定义的Notification内容,布局
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
                        startForeground(1,notification);

                        Toast.makeText(MyService.this, "receive,pause!"+R.id.textView11, Toast.LENGTH_SHORT).show();
                        isPause = true;
                        isRcording = false;
                    }else{
                        Toast.makeText(MyService.this, "还没开始录音", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    }

}
