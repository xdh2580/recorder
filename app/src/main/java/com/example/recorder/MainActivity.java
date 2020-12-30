package com.example.recorder;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.icu.util.LocaleData;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //test 分支测试测试
    public static final int UPDATE_LIST=1;
    private Button bt_est;
    private static ListView listView;
    private static ArrayAdapter<String> adapter;
    private TextView textView_hint;

    public  static  MainActivity instance;

    private MediaRecorder mediaRecorder = new MediaRecorder();
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private File audioFile;
    //  private   File path = new File("/storage/sdcard0/MyRecodingFile");
    private   File path = new File("/data/data/com.example.recorder/cache/file/aaa");
    private  File testFile = new File("/storage/emulated/0/mtklog/file_tree.txt");
  //  private  File mypath =new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));
   private  File mypath;
    private  static boolean isRecording;
    private   NotificationManager manager;
    private ImageButton bt2;



    List<File> fileList = new ArrayList<File>();
    File[] files;
    ArrayList<String> fileList_string = new ArrayList<String>();


//计时
    private  CountDownTimer countDownTimer=new CountDownTimer(6000*100,100) {

        @Override
        public void onTick(long millisUntilFinished) {

            //秒转化成 00:00形式一
//            timeView2.setText(formatTime1(millisUntilFinished) + "");

            //秒转化成 00:00形式二
            textView_hint.setText(formatTime((6000*100-millisUntilFinished) / 1000));
      //      Log.e("mills remain", millisUntilFinished + " ");


        }

        @Override
        public void onFinish() {

        }
    };

    private String formatTime(long seconds) {
        return String.format(" %02d:%02d", seconds / 60, seconds % 60);
    }

    //handler刷新UI
    public   Handler handler =new Handler(){
        public  void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_LIST:
                    files = mypath.listFiles();
                    fileList_string.clear();
                    fileList.clear();
                    for(File item:files){
                        if(!item.isDirectory())
                        fileList.add(item);
                    }
                    for(File item:files){
                        if(!item.isDirectory())
                        fileList_string.add(item.toString());
                    }
                    adapter.notifyDataSetChanged();
             //       listView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mypath = new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));
        Message message = new Message();
        message.what=UPDATE_LIST;
        handler.sendMessage(message);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance=this;

        //第一次安装时默认根目录为存储路径，并写入到sharedPreferences
        if (getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue").equals("defValue")) {
            SharedPreferences.Editor editor = getSharedPreferences("pathData", MODE_PRIVATE).edit();
            editor.putString("currentPath", "/storage/emulated/0");
            editor.apply();
        }
        mypath = new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));
        files = mypath.listFiles();
//        initPlayer();

//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar!=null)
//        actionBar.hide();
//

        Intent intent = getIntent();
        String isRe = intent.getStringExtra("nana");
   //     Toast.makeText(this,isRe,Toast.LENGTH_SHORT).show();
  //      Toast.makeText(this,String.valueOf(isRecording),Toast.LENGTH_SHORT).show();
   //     if(isRe.equals("recoding"))
   //         isRecording=true;

//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "record";
            String channelName = "录音通知channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

        }

     //   new Thread(new GameThread()).start();

   //    boolean isDelete= path.delete();
  //     String a = String.valueOf(isDelete);
   //    Toast.makeText(MainActivity.this,a,Toast.LENGTH_SHORT).show();
   /*     try {
            deleteAllFlies(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] files =path.list();
        Toast.makeText(MainActivity.this,files[0]+"   1:"+files[1],Toast.LENGTH_SHORT).show();

    */
        path.mkdirs();

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        bt_est = (Button) findViewById(R.id.button_play);

        Button bt_del = (Button) findViewById(R.id.button_delete);
        ImageButton bt1 = (ImageButton) findViewById(R.id.imageButton);
        bt2 = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton bt3 = (ImageButton) findViewById(R.id.imageButton3);
        listView =findViewById(R.id.listview1);
        textView_hint=findViewById(R.id.textView4);

        for(File item:files){
            if(!item.isDirectory())
            fileList.add(item);
        }
        for(File item:files){
            if(!item.isDirectory())
            fileList_string.add(item.toString());
        }

        adapter =new ArrayAdapter<String>(this,R.layout.list_item,fileList_string);

        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

//listView列表点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File fi = fileList.get(position);

          if(fi.toString().endsWith(".mp3")){
                MediaPlayer mp =new MediaPlayer();
                initPlayer(mp,fi);
                goPlayer(mp);
            }else{
                Toast.makeText(MainActivity.this,"不支持的文件格式",Toast.LENGTH_SHORT).show();
            }
//                Toast.makeText(MainActivity.this,fi.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        bt_est.setOnClickListener(this);
        bt_del.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);



        //权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {

        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {

        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {

        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 当前播放完毕
                mediaPlayer.stop();

                Toast.makeText(MainActivity.this,"播放完毕",Toast.LENGTH_SHORT).show();
            }
        });



    }
    @Override
    public void onDestroy()
    {
        if (audioFile != null && audioFile.exists())
        {
            // 停止录音
            mediaRecorder.stop();
            // 释放资源
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
        Intent intent_stop_service = new Intent(this, MyService.class);
        stopService(intent_stop_service);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }



    //按键事件
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton:

                Intent intent5 = new Intent(this,MainActivity3.class);
                startActivity(intent5);
//                Message message = new Message();
//                message.what=UPDATE_TEST;
//                handler.sendMessage(message);
//               bt_est.setText("settings");

                break;
            case R.id.imageButton2:



                //开始或停止录音~~~
                recorder_Media();
                //发送通知~~~
                if(isRecording==true){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);

                    String isRecording_string = String.valueOf(isRecording);
//                    Toast.makeText(this,isRecording_string,Toast.LENGTH_SHORT).show();
                    //     intent.putExtra("nana","recoding");
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                    //创建Notification，传入Context和channelId
                    Notification notification = new NotificationCompat.Builder(this, "record")
                            .setAutoCancel(true)
                            .setContentTitle("recorder")
                            .setContentText("正在录音...")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            //在build()方法之前还可以添加其他方法
                            .build();
                    manager.notify(1, notification);
                }else{
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }


                break;
            case R.id.imageButton3:
           //     Toast.makeText(MainActivity.this, "clicked bt3", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent3);

//                try {
//                    BufferedReader br = new BufferedReader(new FileReader(testFile.toString()));
//                    String line1 = br.readLine();
//                    Toast.makeText(MainActivity.this,line1.toString(), Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                break;

            case R.id.button_play:

                Intent intent_start = new Intent(this,MyService.class);
                startService(intent_start);
                Toast.makeText(this, "have start service", Toast.LENGTH_SHORT).show();

       /*      File  exdata = new File(Environment.getExternalStoragePublicDirectory("").getAbsolutePath());

                //   File data = Environment.getDataDirectory();
             Toast.makeText(MainActivity.this,exdata.toString(),Toast.LENGTH_SHORT).show();

        */
//                if (mediaPlayer.isPlaying()==false)
//                    goPlayer(mediaPlayer);
//                if (mediaPlayer.isPlaying()==true)
//                    resetPlayer();

                break;
            case R.id.button_delete:
                try {

                    deleteList(mypath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(isRecording==false) {
//                    Intent intent_re = new Intent(MainActivity.this, MainActivity.class);
//                    intent_re.setAction(Intent.ACTION_MAIN);
//                    intent_re.addCategory(Intent.CATEGORY_LAUNCHER);
//                    startActivity(intent_re);
        //            adapter.
                      Message message = new Message();
                      message.what=UPDATE_LIST;
                      handler.sendMessage(message);
//                      adapter.notifyDataSetChanged();
//                      listView.setAdapter(adapter);
                    Toast.makeText(MainActivity.this,"Deleted all", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"正在录音，请先结束录制", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }

    }


    public void initMediaRecorder(){

            try {
                SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
                //    audioFile = File.createTempFile("recording", ".mp3", path);
                Date curDate =  new Date(System.currentTimeMillis());
                String   time   =   formatter.format(curDate);
                audioFile = File.createTempFile(time, ".mp3", mypath);
                mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
                Log.d("log","mypath:"+mypath.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


    }
    public void initPlayer(MediaPlayer mediaPlayerNew,File file){
            try {
                mediaPlayerNew.setDataSource(file.toString());
                Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayerNew.setAudioStreamType(AudioManager.STREAM_MUSIC);


    }
    public void resetPlayer(){
            mediaPlayer.stop();
            Toast.makeText(MainActivity.this,"取消播放",Toast.LENGTH_SHORT).show();

    }



    public void goPlayer(MediaPlayer mediaPlayerNew){


        // 通过异步的方式装载媒体资源
        mediaPlayerNew.prepareAsync();
        mediaPlayerNew.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 装载完毕回调
                mp.start();
               // Toast.makeText(MainActivity.this,"开始播放",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //开始
    public void recorder_Media() {

        if (isRecording == true) {
            //    mediaRecorder.pause();
            mediaRecorder.stop();
  //          mediaRecorder.reset();
    //        mediaRecorder.release();
            countDownTimer.cancel();
            textView_hint.setText("点击下方按钮开始录音");
            isRecording=false;
            bt2.setImageDrawable(getResources().getDrawable(R.mipmap.icon1));
            Message message = new Message();
            message.what=UPDATE_LIST;
            handler.sendMessage(message);
            Toast.makeText(MainActivity.this, "结束录制,文件名：\n"+audioFile.getName()+"\n文件位置:\n"+mypath, Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(MainActivity.this,MainActivity.class);
//            startActivity(intent);
        } else {
            //    amplitude = new RecordAmplitude();
                 initMediaRecorder();

            try {

            //    mediaRecorder.reset();
                mediaRecorder.prepare();
                mediaRecorder.start();
                countDownTimer.start();


                isRecording = true;
                bt2.setImageDrawable(getResources().getDrawable(R.mipmap.icon5));
         //       Toast.makeText(MainActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
         //       Toast.makeText(MainActivity.this, String.valueOf(isRecording), Toast.LENGTH_SHORT).show();

                //        amplitude.execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

//删除所有录音文件
    protected void deleteList(File file){

        File[] files = file.listFiles();
        for(File item:files){
//            if(item.toString().equals("/storage/emulated/0/MyFolder/mymusic.mp3"))
//                continue;
            if(!item.isDirectory())
            item.delete();
        }
    }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //耗时操作
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //更新界面
//
//
//                    }
//                });
//            }
//        }).start();


//    class GameThread implements Runnable {
//        public void run()
//        {
//            while (!Thread.currentThread().isInterrupted())
//            {
//                try
//                {
//                    Thread.sleep(100);
//                }
//                catch (InterruptedException e)
//                {
//                    Thread.currentThread().interrupt();
//                }
//                //使用postInvalidate可以直接在线程中更新界面
//                .postInvalidate();
//            }
//        }
//    }
}