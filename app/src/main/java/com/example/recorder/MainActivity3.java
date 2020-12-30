package com.example.recorder;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//文件预览界面，可实现很多文件操作
public class MainActivity3 extends AppCompatActivity {

    private static ListView listView;
    private static ArrayAdapter<File> adapter;
    public  static  MainActivity3 instance;
    Button bt_operate;
    Button bt_last;

    private  File mypath;

    private ListView mListView;
    File[] files;
    TextView tv_showDir;
    MyAdapter mMyListAdapter;//继承自baseAdapter实现自定义子项布局与数据填充，点击事件等
    List<File> fileList = new ArrayList<>();  //创建File对象集合,listview数据源
    //handler刷新UI
    public Handler handler =new Handler(){
        public  void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    //textView更新
                    tv_showDir.setText(mMyListAdapter.currentDir.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        instance=this;

        mypath = new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));

        files =mypath.listFiles();


        //装载数据到fileList
        for(File item:files)
            fileList.add(item);

        //找到listview 组件
        mListView = (ListView) findViewById(R.id.listview2);
        //创建Adapter 实例化对象， 调用构造函数传参，将数据（fileList）和adapter绑定
         mMyListAdapter = new MyAdapter(fileList,this);
        mListView.setAdapter(mMyListAdapter);   //将定义的adapter 和 listView 绑定

        tv_showDir=(TextView) findViewById(R.id.textView_showDir);
        tv_showDir.setText(mMyListAdapter.currentDir.toString());

        //返回上一层
        bt_last=(Button) findViewById(R.id.button_last);
        bt_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMyListAdapter.currentDir.toString().equals("/storage/emulated/0"))
                    Toast.makeText(MainActivity3.this,"你已在手机存储根目录",Toast.LENGTH_SHORT).show();
                else {
                    //返回上一层并刷新listview，直接更新fileList便可刷新listView
                    File curpath = mMyListAdapter.currentDir;
                    File lastDir = curpath.getParentFile();

                    File[] filesTemp = lastDir.listFiles();
                    MyAdapter.fileList.clear();
                    for (File file : filesTemp) {
                        MyAdapter.fileList.add(file);
                    }
                    mMyListAdapter.notifyDataSetChanged();
                    mMyListAdapter.currentDir = lastDir;

                    //textView更新
                    tv_showDir.setText(mMyListAdapter.currentDir.toString());

                }
            }
        });
        bt_operate = (Button) findViewById(R.id.button_oprate);
        bt_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(bt_operate);
            }
        });
    }

    //弹出子菜单
    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.oprate, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "选择当前路径保存录音文件":
                        SharedPreferences.Editor editor = getSharedPreferences("pathData",MODE_PRIVATE).edit();
                        editor.putString("currentPath",mMyListAdapter.currentDir.toString());
                        editor.apply();
                        Toast.makeText(MainActivity3.this,"currentPath:"+mMyListAdapter.currentDir.toString(),Toast.LENGTH_SHORT).show();
                        break;
                    case "粘贴到当前路径":

                        if(!(ClipBoard.currentFile==null)) {
                            //判断文件是否已在当前路径
                            String dirOfClipFile = ClipBoard.currentFile.toString();
                            int temp = dirOfClipFile.lastIndexOf('/');
                            dirOfClipFile = dirOfClipFile.substring(0, temp);
//                        Toast.makeText(MainActivity3.this,"dirOfClipFile:\n"+dirOfClipFile+
//                                "\nmMyListAdapter.currentDir:\n"+mMyListAdapter.currentDir.toString(), Toast.LENGTH_SHORT).show();
                            if (!dirOfClipFile.equals(mMyListAdapter.currentDir.toString())) {
                                if (!(ClipBoard.cutOrCopyState == ClipBoard.WAITING_FOR)) {
                                    switch (ClipBoard.cutOrCopyState) {
                                        case ClipBoard.CUTTING:
                                            File readyToCopy = ClipBoard.currentFile;
                                            fileList.add(readyToCopy);
                                            mMyListAdapter.notifyDataSetChanged();
                                            File newFile = new File(mMyListAdapter.currentDir.toString() + "/" + readyToCopy.getName());
                                            readyToCopy.renameTo(newFile);
                                            ClipBoard.cutOrCopyState = ClipBoard.WAITING_FOR;
                                            Toast.makeText(MainActivity3.this, "移动成功", Toast.LENGTH_SHORT).show();
                                            break;
                                        case ClipBoard.COPYING:
                                            File fileForCopy = new File(ClipBoard.currentFile.toString());
                                            File newFileOfCopy = new File(mMyListAdapter.currentDir.toString().concat("/" + ClipBoard.currentFile.getName()));
                                            try {
                                                FileInputStream fis = new FileInputStream(fileForCopy);
                                                FileOutputStream fos = new FileOutputStream(newFileOfCopy); //创建输出流对象
                                                byte datas[] = new byte[1024 * 8];//创建搬运工具
                                                int len = 0;//创建长度
                                                while ((len = fis.read(datas)) != -1)//循环读取数据
                                                {
                                                    fos.write(datas, 0, len);
                                                }
                                                fis.close();//释放资源
                                                fis.close();//释放资源
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            fileList.add(newFileOfCopy);
                                            mMyListAdapter.notifyDataSetChanged();
                                            ClipBoard.cutOrCopyState = ClipBoard.WAITING_FOR;
                                            Toast.makeText(MainActivity3.this, "fileForCopy:\n" + fileForCopy.toString()
                                                    + "\nnewFileOfCopy:\n" + newFileOfCopy.toString(), Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(MainActivity3.this, "请先选中文件到剪切板", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity3.this, "文件已经在当前路径", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(MainActivity3.this, "没有选中文件", Toast.LENGTH_SHORT).show();
                        }

//                        String currentPathFile_content_string = mMyListAdapter.currentDir.toString();
//                        FileOutputStream out = null;
//                        BufferedWriter writer = null;
//                        try {
//                            out = openFileOutput("path_file.txt",MODE_APPEND);
//                            writer = new BufferedWriter(new  OutputStreamWriter(out));
//                            writer.write("123445");
//                            Toast.makeText(MainActivity3.this,currentPathFile_content_string,Toast.LENGTH_SHORT).show();
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    case "测试":



//                        Toast.makeText(MainActivity3.this,
//                                getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"),Toast.LENGTH_SHORT).show();

//                            FileInputStream in = null;
//                            BufferedReader reader = null;
//                            StringBuilder content = new StringBuilder();
//                        try {
//                            in = openFileInput("path_file");
//                            reader = new BufferedReader(new InputStreamReader(in));
//                            String line = "";
//                            while ((line=reader.readLine())!=null){
//                                content.append(line);
//                            }
//                            Toast.makeText(MainActivity3.this,content.toString(),Toast.LENGTH_SHORT).show();
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    default:
                        break;


                }

//                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
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