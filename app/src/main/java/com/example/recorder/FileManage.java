package com.example.recorder;

import android.util.Log;

import java.io.File;

public class FileManage {
    static File currentPath= new File("/storage/emulated/0/MyFolder");


    public static boolean changeCurrentPath(File path){
        if(path.isDirectory()) {
            currentPath = path;
            return true;
        }
        else{
                Log.d("myLog", "changePathError");
                return false;
            }
    }

}
