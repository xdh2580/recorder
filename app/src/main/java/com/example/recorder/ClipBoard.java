package com.example.recorder;

import java.io.File;

//自己设计的剪切板类
public class ClipBoard {

    static final int COPYING=1;
    static final int CUTTING=-1;
    static final int WAITING_FOR=0;
    static File currentFile;
    static int cutOrCopyState=WAITING_FOR;

    public  ClipBoard(){
    }
    public ClipBoard(File file){
        currentFile=file;
    }

}
