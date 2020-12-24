//package com.example.recorder;
//
//import android.util.Log;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//
//public class FileManage extends AppCompatActivity {
//      File currentPath= new File(getSharedPreferences("pathData",MODE_PRIVATE).getString("currentPath","defValue"));
//
//
//    public static boolean changeCurrentPath(File path){
//        if(path.isDirectory()) {
//            currentPath = path;
//
//            return true;
//        }
//        else{
//                Log.d("myLog", "changePathError");
//                return false;
//            }
//    }
//
//
//    }
//
//}
