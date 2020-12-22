package com.example.recorder;

import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private static ListView listView;
    private static ArrayAdapter<File> adapter;
    Button bt_operate;
    Button bt_last;

    private  File mypath = FileManage.currentPath;

    private ListView mListView;
    File[] files;

    //handler刷新UI


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mypath = FileManage.currentPath;

        files =mypath.listFiles();


        List<File> fileList = new ArrayList<>();  //创建File 对象集合
        for(File item:files)
            fileList.add(item);

        //找到listview 组件
        mListView = (ListView) findViewById(R.id.listview2);
        //创建Adapter 实例化对象， 调用构造函数传参，将数据和adapter  绑定
        final MyAdapter mMyListAdapter = new MyAdapter(fileList,this);
        mListView.setAdapter(mMyListAdapter);   //将定义的adapter 和 listView 绑定

//        File[] filess =mypath.listFiles();
//        File oldFile =filess[0].getAbsoluteFile();
//
//        File newFile = new File("/storage/emulated/0/MyFolder/myMusiccc.mp3");
//
//        boolean su = oldFile.renameTo(newFile);
//        Toast.makeText(this,filess[0].getAbsoluteFile().toString(),Toast.LENGTH_SHORT).show();

        bt_last=(Button) findViewById(R.id.button_last);
        bt_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMyListAdapter.currentDir.toString().equals("/storage/emulated/0"))
                    Toast.makeText(MainActivity3.this,"你已在手机存储根目录",Toast.LENGTH_SHORT).show();
                else {
                    //返回上一层并刷新listview
                    File curpath = mMyListAdapter.currentDir;
                    File lastDir = curpath.getParentFile();

                    File[] filesTemp = lastDir.listFiles();
                    MyAdapter.fileList.clear();
                    for (File file : filesTemp) {
                        MyAdapter.fileList.add(file);
                    }
                    mMyListAdapter.notifyDataSetChanged();
                    mMyListAdapter.currentDir = lastDir;
                    Toast.makeText(MainActivity3.this,"mMyListAdapter.currentDir:\n"+
                            mMyListAdapter.currentDir.toString()+"\nlastDir:\n"+lastDir.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_operate = (Button) findViewById(R.id.button_oprate);
        bt_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        //        String a = mListView.getAdapter().getItem(0).toString();

       //        Toast.makeText(MainActivity3.this,mMyListAdapter.currentDir.toString(),Toast.LENGTH_SHORT).show();

//                FileManage.changeCurrentPath(mMyListAdapter.currentDir);
//                Toast.makeText(MainActivity3.this,String.valueOf(FileManage.changeCurrentPath(mMyListAdapter.currentDir)),Toast.LENGTH_SHORT).show();
                showPopupMenu(bt_operate);
            }
        });


    }


    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.oprate, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
          //      Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }


}