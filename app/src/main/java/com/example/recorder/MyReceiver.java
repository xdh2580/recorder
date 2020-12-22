package com.example.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import java.io.File;

public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
     //   System.out.printf("onReceive");
        Toast.makeText(context,"Received",Toast.LENGTH_SHORT).show();
        //  throw new UnsupportedOperationException("Not yet implemented");
    }
}