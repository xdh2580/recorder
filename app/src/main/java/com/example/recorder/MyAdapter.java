package com.example.recorder;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter{


    static List<File> fileList;   //创建一个File 类的对象 集合
    private LayoutInflater inflater;
    File currentDir=FileManage.currentPath;
//    ArrayList<String> fileList_string = new ArrayList<String>();




    public  MyAdapter (List<File> fileList, Context context) {
        this.fileList = fileList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return fileList == null?0:fileList.size();  //判断有说个Item
    }

    @Override
    public File getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        final View view = inflater.inflate(R.layout.list2_item,null);
         final File item = getItem(position);
         ImageView img = (ImageView) view.findViewById(R.id.imageView2);
        TextView tv_name = (TextView) view.findViewById(R.id.textView13);
        ImageButton bt_more =(ImageButton)  view.findViewById(R.id.imageButton5);
        if(item.isDirectory()){
            img.setImageResource(R.mipmap.icon_folder);
        }


        String filepath=item.toString();
        String b = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());

        tv_name.setText(b);

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入文件夹，刷新listview
                if(item.isDirectory()) {
                    fileList.clear();
                    File[] fileList_arr = item.listFiles();
                    for (File file:fileList_arr)
                        fileList.add(file);
                    notifyDataSetChanged();
                    currentDir=item;
                    Toast.makeText(view.getContext(),"item:"+item.toString()+"\ncurrentDir:"+currentDir.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("asdf","click"+position);
//                Toast.makeText(view.getContext(),"click"+position, Toast.LENGTH_SHORT).show();

                showPopupMenu(v,item);
            }
        });
//        StudentData mStudentData = (StudentData) getItem(position);
//
//        //在view 视图中查找 组件
//        TextView tv_name = (TextView) view.findViewById(R.id.textView13);
//        TextView tv_age = (TextView) view.findViewById(R.id.textView13);
//        ImageView im_photo = (ImageView) view.findViewById(R.id.imageView2);
//
//        //为Item 里面的组件设置相应的数据
//        tv_name.setText(mStudentData.getName());
//        tv_age.setText("age: "+ mStudentData.getAge());
//        im_photo.setImageResource(mStudentData.getPhoto());

        //返回含有数据的view
        return view;
    }

    private void showPopupMenu(final View view, final File file) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "重命名":
                        Toast.makeText(view.getContext(),file.toString(), Toast.LENGTH_SHORT).show();
                        File newFile = new File("/storage/emulated/0/MyFolder/recordingFile"+Math.random()+".mp3");
                        fileList.remove(file);
                        file.renameTo(newFile);
                        fileList.add(newFile);
                        notifyDataSetChanged();

                        break;
                    case "删除":
                        fileList.remove(file);
                        notifyDataSetChanged();
                        Toast.makeText(view.getContext(),String.valueOf(file.delete()), Toast.LENGTH_SHORT).show();

                        break;
                    case "移动":
                        ClipBoard.currentFile=file;
                        Toast.makeText(view.getContext(),ClipBoard.currentFile.toString(),Toast.LENGTH_SHORT).show();
                        break;
                    case "复制":

                        break;
                    default:
                        break;
                }

//                Toast.makeText(view.getContext(),"item.getTitle()="+item.getTitle()+"  getItemId="+item.getItemId(), Toast.LENGTH_SHORT).show();
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