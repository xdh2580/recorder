package com.example.recorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
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
    File currentDir;//记录当前listview所显示的文件所在的路径
//    ArrayList<String> fileList_string = new ArrayList<String>();




    public  MyAdapter (List<File> fileList, Context context) {
        this.fileList = fileList;
        this.inflater = LayoutInflater.from(context);

        this.currentDir=new File(this.inflater.getContext()
                .getSharedPreferences("pathData",this.inflater.getContext().MODE_PRIVATE).getString("currentPath","defValue"));
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
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
                    //textView更新
//                    在此输出刷新后的textView的text，显示当前所在文件夹
//                    View activity_main3 = inflater.inflate(R.layout.activity_main3,null);//获得activity的layout的引用
//                    TextView textViewShowShow = activity_main3.findViewById(R.id.textView_showDir);//获得在该布局中的控件的引用,可以获得，但不能改内容
//                    textViewShowShow.setText(currentDir.toString());//并不能直接改，下面发送Massage使用handler改
                    Message msg = new Message();
                    msg.what=1;
                    MainActivity3.instance.handler.sendMessage(msg);//获得MainActivity3的handler，并sendMassage
//                    Toast.makeText(view.getContext(),"success",Toast.LENGTH_SHORT).show();

           //         Toast.makeText(view.getContext(),"item:"+item.toString()+"\ncurrentDir:"+currentDir.toString(), Toast.LENGTH_SHORT).show();
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
                        if(!file.isDirectory()) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                            builder.setTitle("请输入文件名称");
                            final EditText editText =new EditText(inflater.getContext());
                            builder.setView(editText);
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (editText.getText().toString().isEmpty()) {
                                        Toast.makeText(view.getContext(), "文件名不能为空", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String newName = editText.getText().toString().concat(".mp3");
                                        File newFile = new File(currentDir.toString().concat("/"+newName));
                                        file.renameTo(newFile);
                                        fileList.remove(file);
                                        fileList.add(newFile);
                                        notifyDataSetChanged();
                                        Toast.makeText(view.getContext(), "重命名成功\nnewFile:\n"+file.toString(), Toast.LENGTH_SHORT).show();
                                }
                                }
                            });
                            builder.setNegativeButton("取消",null);
                            builder.show();
                        }
                        else {
                            Toast.makeText(view.getContext(),"暂不支持重命名文件夹", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "删除":
                        if(file.isDirectory()){
                            Toast.makeText(view.getContext(),"暂不支持删除文件夹", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            fileList.remove(file);
                            notifyDataSetChanged();
                            boolean isDelete = file.delete();
                            if(isDelete==true)
                                Toast.makeText(view.getContext(),"删除成功", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(view.getContext(),"删除失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "移动":
                        if(file.isDirectory()){
                            Toast.makeText(view.getContext(),"暂不支持移动文件夹", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ClipBoard.currentFile = file;
                            ClipBoard.cutOrCopyState = ClipBoard.CUTTING;
                        //    Toast.makeText(view.getContext(), ClipBoard.currentFile.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(view.getContext(),"已映射到剪切板", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case "复制":
                        if(!file.isDirectory()){
                            ClipBoard.currentFile = file;
                            ClipBoard.cutOrCopyState = ClipBoard.COPYING;
                            Toast.makeText(view.getContext(),"已映射到剪切板", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(view.getContext(),"暂不支持复制文件夹", Toast.LENGTH_SHORT).show();
                        }
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