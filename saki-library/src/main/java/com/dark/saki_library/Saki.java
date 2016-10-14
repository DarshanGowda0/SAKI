package com.dark.saki_library;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by darshan on 14/10/16.
 */

public class Saki implements RecognitionListener {

    public final String PACKAGE_TABLE = "packages";
    public final String ACTIVITY_TABLE = "activities";
    public final String VIEW_TABLE = "views";

    ArrayList<Integer> listOfIds = new ArrayList<>();
    HashMap<Integer, String> idsHash = new HashMap<>();

    DatabaseReference databaseReference;

    private static final String TAG = "Darshan";
    Activity activity;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;


    String activityName;
    String packageName;

    public Saki(Activity activity) {
        this.activity = activity;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        showChatHead();

        packageName = activity.getPackageName().replaceAll("\\.", "_");
        activityName = activity.getClass().getSimpleName();

    }

    void showChatHead() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, 1234);
            }
        } else {
            Intent intent = new Intent(activity, ChatHeadService.class);
            activity.startService(intent);
        }

//        activity.startService(new Intent(activity, ChatHeadService.class));
    }


    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech: ");

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech: ");

    }

    @Override
    public void onError(int error) {
        String errorMessage = getErrorText(error);
        Log.d(TAG, "FAILED " + errorMessage);
        startListener();
    }

    @Override
    public void onResults(Bundle results) {

        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches) {
            text += result + "\n";
            if (result.toLowerCase().startsWith("saki")) {
                Log.d(TAG, "onResults: send this to saki:" + result);
//            ChatHeadService.chatHead.setImageResource(R.drawable.mic_go);
//                sendData(result.substring(5));

                break;
            }
            if (result.toLowerCase().contains("goodbye saki")) {
//                ChatHeadService.chatHead.setImageResource(R.drawable.mic_stop);
                Log.d(TAG, "onResults: bye saki");
                speech.stopListening();
                speech.destroy();
            }
        }
        Log.i(TAG, "onResults:" + text);

        startListener();

    }

    private void sendData(String data) {


    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public void startListener() {
        speech = SpeechRecognizer.createSpeechRecognizer(activity);
        speech.setRecognitionListener(this);


        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                activity.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);


        speech.startListening(recognizerIntent);

    }

    public void registerButton(final Button button, final String hint) {

        listOfIds.add(button.getId());

        idsHash.put(button.getId(), "Button");
        // add to the database

        final DatabaseReference localRef = databaseReference.child(PACKAGE_TABLE).child(packageName).child(ACTIVITY_TABLE).child(activityName).child(VIEW_TABLE);

        localRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    localRef.child("" + button.getId()).child("hint").setValue(hint);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d(TAG, "onCancelled: ");

            }
        });


    }

    public void destroy() {
        speech.destroy();
        activity.stopService(new Intent(activity, ChatHeadService.class));

    }

}
