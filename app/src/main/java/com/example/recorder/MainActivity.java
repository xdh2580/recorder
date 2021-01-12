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

    public static final int UPDATE_LIST=1;

    private Button bt_start_on_service;
    private static ListView listView;
    private static ArrayAdapter<String> adapter;
    private TextView textView_hint;

    public  static  MainActivity instance;//用于给其他类获取这个activity的实例，在onCreate中返回，思路邪门但可行

    private MediaRecorder mediaRecorder = new MediaRecorder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private File audioFile;
    private   File path = new File("/data/data/com.example.recorder/cache/file/aaa");//没啥用
   private  File mypath;//当前定义的录音文件保存路径
    private  static boolean isRecording;//MediaRecorder在录音时手动更改，记录当前录音状态
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
                    //重新装载一遍，达到刷新效果
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
        //更新listView
        Message message = new Message();
        message.what=UPDATE_LIST;
        handler.sendMessage(message);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance=this;//创建MainActivity实例时返回到静态属性中，便于其他类获取到该activity实例

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





        //第一次安装时默认根目录为存储路径，并写入到sharedPreferences
        if (getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue").equals("defValue")) {
            SharedPreferences.Editor editor = getSharedPreferences("pathData", MODE_PRIVATE).edit();
//            editor.putString("currentPath", "/storage/emulated/0");
            editor.putString("currentPath",this.getExternalCacheDir().getAbsolutePath());
            editor.apply();
        }

        //从sharedPreferences中读取设置的路径数据
        mypath = new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));
        files = mypath.listFiles();

//        尝试取消系统顶部Actionbar
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar!=null)
//        actionBar.hide();
//

        //暂时用不到
        Intent intent = getIntent();


//复杂手段创建通知channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "record";
            String channelName = "录音通知channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            createNotificationChannel(channelId, channelName, importance);

        }


        //尝试递归删除文件夹，后搁置
   /*     try {
            deleteAllFlies(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] files =path.list();
        Toast.makeText(MainActivity.this,files[0]+"   1:"+files[1],Toast.LENGTH_SHORT).show();

    */
        path.mkdirs();

        //获得NotificationManager实例
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//主界面最上方开启服务录音按钮
        bt_start_on_service = (Button) findViewById(R.id.button_play);

        //"删除"按钮
        Button bt_del = (Button) findViewById(R.id.button_delete);
        //下方三个imageButton，作用分别是打开文件预览，开始/结束录音，查看简单的配置信息
        ImageButton bt1 = (ImageButton) findViewById(R.id.imageButton);
        bt2 = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton bt3 = (ImageButton) findViewById(R.id.imageButton3);

        //显示录音文件的listView
        listView =findViewById(R.id.listview1);

        //三个imageButton上方用于提示的textView，录音时会显示时长
        textView_hint=findViewById(R.id.textView4);

        //往listView里填充数据
        for(File item:files){
            if(!item.isDirectory())
            fileList.add(item);//先把路径下的所有非目录文件放在fileList集合中
        }
        for(File item:files){
            if(!item.isDirectory())
            fileList_string.add(item.toString());//在把fileList集合中的文件遍历保存到字符串集合fileList_string,用于放到ArrayAdapter
        }
        //new一个ArrayAdapter，绑定子项布局与数据（该数据变动后listView自动刷新）
        adapter =new ArrayAdapter<String>(this,R.layout.list_item,fileList_string);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

//listView列表点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File fi = fileList.get(position);

          //如果是.mp3文件，点击播放
          if(fi.toString().endsWith(".mp3")){
                MediaPlayer mp =new MediaPlayer();//直接new一个新的MediaPlayer用于播放，可行但不太合理，暂时就这样
                initPlayer(mp,fi);
                goPlayer(mp);
            }else{
                Toast.makeText(MainActivity.this,"不支持的文件格式",Toast.LENGTH_SHORT).show();
            }
//                Toast.makeText(MainActivity.this,fi.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //勿忘给按钮设置监听器
        bt_start_on_service.setOnClickListener(this);
        bt_del.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);




        //因为每次都是new新的player，这也就没什么用了
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
        //关闭服务，否则前台服务还会继续运行
        Intent intent_stop_service = new Intent(this, MyService.class);
        stopService(intent_stop_service);
    }

    //创建通知channel
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
                //进入文件预览界面
                Intent intent5 = new Intent(this,MainActivity3.class);
                startActivity(intent5);
                break;

            case R.id.imageButton2:
                //开始或停止录音~~~
                recorder_Media();
                //发送通知~~~
                if(isRecording==true){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    //加上如下两句可实现点击通知返回应用当前界面，原理暂未搞懂
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    //构建pendingIntent
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                    //创建Notification，传入Context和channelId
                    Notification notification = new NotificationCompat.Builder(this, "record")
                            .setAutoCancel(true)//自动取消，点击通知进入界面后通知消失
                            .setContentTitle("recorder")
                            .setContentText("正在录音...")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setContentIntent(pendingIntent)
                            //在build()方法之前还可以添加其他方法
                            .build();
                    manager.notify(1, notification);
                }else{
                    //录音结束后也取消通知
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }
                break;

            case R.id.imageButton3:
                //进入查看配置信息界面
                Intent intent3 = new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent3);
                break;

            case R.id.button_play:
                //开启服务录音
                Intent intent_start = new Intent(this,MyService.class);
                startService(intent_start);
                Toast.makeText(this, "have start service", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_delete:
                //删除定义路径下的所有文件，不含文件夹
                try {
                    deleteList(mypath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(isRecording==false) {
                    //删除后更新listView
                      Message message = new Message();
                      message.what=UPDATE_LIST;
                      handler.sendMessage(message);
                    Toast.makeText(MainActivity.this,"Deleted all", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"正在录音，请先结束录制", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }

    }

//初始化MediaRecorder
    public void initMediaRecorder(){
            try {
                SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
                //    audioFile = File.createTempFile("recording", ".mp3", path);
                Date curDate =  new Date(System.currentTimeMillis());
                String   time   =   formatter.format(curDate);
                //audioFile以时间命名
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

    //初始化Player
    public void initPlayer(MediaPlayer mediaPlayerNew,File file){
            try {
                mediaPlayerNew.setDataSource(file.toString());
                Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayerNew.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
    //暂时不能停止播放
    public void resetPlayer(){
            mediaPlayer.stop();
            Toast.makeText(MainActivity.this,"取消播放",Toast.LENGTH_SHORT).show();

    }


//init之后可开始播放音频
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

    //开始或结束录音
    public void recorder_Media() {
        if (isRecording == true) {
            //在录音就结束
            mediaRecorder.stop();
            countDownTimer.cancel();
            textView_hint.setText("点击下方按钮开始录音");
            isRecording=false;
            bt2.setImageDrawable(getResources().getDrawable(R.mipmap.icon1));
            Message message = new Message();
            message.what=UPDATE_LIST;
            handler.sendMessage(message);//刷新listview
            Toast.makeText(MainActivity.this, "结束录制,文件名：\n"+audioFile.getName()+"\n文件位置:\n"+mypath, Toast.LENGTH_LONG).show();

        } else {
            //不在录音就开始录
                 initMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                countDownTimer.start();
                isRecording = true;
                bt2.setImageDrawable(getResources().getDrawable(R.mipmap.icon5));
         //       Toast.makeText(MainActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
         //       Toast.makeText(MainActivity.this, String.valueOf(isRecording), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

//删除所有listview中显示的文件，也即自定义保存路径下的非文件夹的所有文件，也会删除非mp3的文件
    protected void deleteList(File file){
        File[] files = file.listFiles();
        for(File item:files){
            if(!item.isDirectory())
            item.delete();
        }
    }

}