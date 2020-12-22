package com.example.recorder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView text_location = findViewById(R.id.textView7);
        Button bt_back = findViewById(R.id.button_back);
        bt_back.setOnClickListener(this);
        text_location.setText("/storage/emulated/0/MyFolder");


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                this.finish();
                break;
            default:
        }
    }

}