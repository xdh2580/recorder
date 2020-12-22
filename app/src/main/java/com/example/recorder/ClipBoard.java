package com.example.recorder;

import java.io.File;

public class ClipBoard {
    static File currentFile;

    public  ClipBoard(){
    }
    public ClipBoard(File file){
        currentFile=file;
    }

}
