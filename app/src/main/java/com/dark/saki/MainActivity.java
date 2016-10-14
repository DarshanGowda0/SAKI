package com.dark.saki;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dark.saki_library.Saki;

public class MainActivity extends AppCompatActivity {

    Saki saki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saki = new Saki(MainActivity.this);


        saki.startListener();

    }
}
