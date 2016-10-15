package com.dark.saki;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dark.saki_library.Saki;

public class BookingActivity extends AppCompatActivity {

    EditText etPickup, etDrop;
    //    Sailboat sailboat;
    Button button;

    Saki saki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        saki = new Saki(BookingActivity.this);

        /*ArrayList<Integer> ids = new ArrayList<>();

        ids.add(R.id.et_pickup);
        ids.add(R.id.et_drop);
        ids.add(R.id.bookCab);
        sailboat.initialize(BookingActivity.this,ids);*/

        etPickup = (EditText) findViewById(R.id.et_pickup);
        etDrop = (EditText) findViewById(R.id.et_drop);
        button = (Button) findViewById(R.id.bookCab);

        saki.registerButton(button, "book a cab!");
        saki.registerEditText(etPickup, "pick up location is Bangalore");
        saki.registerEditText(etDrop, "drop location is Bangalore");

        Log.d("TAG", "onCreate: btn" + button.getId());
        Log.d("TAG", "onCreate: pickup" + etPickup.getId());
        Log.d("TAG", "onCreate: drop" + etDrop.getId());

        saki.startListener();

    }

    public void bookACab(View view) {
        String pickupLoc = etPickup.getText().toString();
        String dropLoc = etDrop.getText().toString();

        if (pickupLoc.isEmpty()) {
            etPickup.setError("Please enter a pickup location!");
            etPickup.requestFocus();
            return;
        }

        if (dropLoc.isEmpty()) {
            etDrop.setError("Please enter a drop location!");
            etDrop.requestFocus();
            return;
        }

        callProgressDialog();
    }

    private void callProgressDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(BookingActivity.this);
        progressDialog.setMessage("We're searching a cab for you, hold on...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    progressDialog.dismiss();

                    Intent intent = new Intent(BookingActivity.this, TicketActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saki.onPause();
        saki.destroy();
    }


}
