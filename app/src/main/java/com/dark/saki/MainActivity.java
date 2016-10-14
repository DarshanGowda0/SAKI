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

        button = (Button) findViewById(R.id.button2);

        saki.registerButton(button, "testing this hint");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saki.destroy();
    }
}
