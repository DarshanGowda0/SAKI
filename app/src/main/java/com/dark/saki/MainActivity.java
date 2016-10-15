package com.dark.saki;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.dark.saki_library.Saki;

public class MainActivity extends AppCompatActivity {

    Saki saki;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saki = new Saki(MainActivity.this);
        saki.startListener();

//        button = (Button) findViewById(R.id.button2);
//
//        saki.registerButton(button, "testing this hint");
//
//        saki.storeTheSpeechText(123, "this is a test speech");
//
//        saki.setActivityHint("Hey! This is a random message.");
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//
//                    saki.initTTS();
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();

        saki.sendData("Chandy something");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saki.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saki.onPause();
    }
}
