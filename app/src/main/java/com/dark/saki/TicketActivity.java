package com.dark.saki;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dark.saki_library.Saki;

public class TicketActivity extends AppCompatActivity {

//    Sailboat sailboat;

    Saki saki;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        saki = new Saki(TicketActivity.this);

        saki.startListener();
//
//        ArrayList<Integer> ids = new ArrayList<>();
//
//        ids.add(R.id.btnCallDriver);
//        Log.d("TAG", "onCreate: "+R.id.btnCallDriver);
//        sailboat.initialize(TicketActivity.this,ids);

        button = (Button) findViewById(R.id.btnCallDriver);
        saki.registerButton(button, "Call the driver");

        Log.d("TAG", "onCreate: call " + button.getId());

        Toolbar toolbar;


    }

    public void callDriver(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:+919742934099"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saki.onPause();
    }
}
