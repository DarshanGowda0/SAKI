package com.dark.saki_library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by darshan on 14/10/16.
 */

public class Saki implements RecognitionListener {

    public final String PACKAGE_TABLE = "packages";
    public final String ACTIVITY_TABLE = "activities";
    public final String VIEW_TABLE = "views";

    TextToSpeech textToSpeech;

    HashMap<Integer, String> idsHash = new HashMap<>();

    DatabaseReference databaseReference;

    private static final String TAG = "Darshan";
    Activity activity;
    String activityHint = "Sorry! I don't have that information!";

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

        getActivityHint();

        registerBackButton("Go back");

    }


    public void initTTS() {


        textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                            //TODO handle the stt

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                    speakActivityHint();
                }
            }
        });
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

        idsHash.put(button.getId(), ViewTypes.BUTTON);

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
            }
        });
    }


    public void registerBackButton(final String hint) {

        idsHash.put(0, ViewTypes.BACK_BUTTON);

        // add to the database
        final DatabaseReference localRef = databaseReference.child(PACKAGE_TABLE).child(packageName).child(ACTIVITY_TABLE).child(activityName).child(VIEW_TABLE);
        localRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    localRef.child("" + 0).child("hint").setValue(hint);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void registerEditText(final EditText editText, final String hint) {

        idsHash.put(editText.getId(), ViewTypes.EDIT_TEXT);
        // add to the database

        final DatabaseReference localRef = databaseReference.child(PACKAGE_TABLE).child(packageName).child(ACTIVITY_TABLE).child(activityName).child(VIEW_TABLE);

        localRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    localRef.child("" + editText.getId()).child("hint").setValue(hint);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void registerTextView(final TextView textView, final String hint) {

        idsHash.put(textView.getId(), ViewTypes.TEXT_VIEW);
        // add to the database

        final DatabaseReference localRef = databaseReference.child(PACKAGE_TABLE).child(packageName).child(ACTIVITY_TABLE).child(activityName).child(VIEW_TABLE);

        localRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    localRef.child("" + textView.getId()).child("hint").setValue(hint);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    public void destroy() {
        speech.destroy();
        activity.stopService(new Intent(activity, ChatHeadService.class));

    }

    public void sendData(String speech) {

    }

    public void parseData(String data, String speech) {

        Integer id = 0;
        String splitString = "";

        //parse the json and store in those objects

        if (idsHash.containsKey(id)) {
            View view = activity.findViewById(id);

            String type = idsHash.get(id);

            switch (type) {

                case ViewTypes.BUTTON:
                    if (view.hasOnClickListeners()) view.callOnClick();
                    break;

                case ViewTypes.EDIT_TEXT:
                    ((EditText) view).setText(splitString);
                    break;

                case ViewTypes.TEXT_VIEW:
                    ((TextView) view).setText(splitString);
                    break;

            }

            storeTheSpeechText(id, speech);

        }


    }

    public void storeTheSpeechText(Integer id, String speech) {

        String key = databaseReference.child(PACKAGE_TABLE).child(packageName).child("bucket").child(String.valueOf(id)).push().getKey();
        databaseReference.child(PACKAGE_TABLE).child(packageName).child("bucket").child(String.valueOf(id)).child(key).setValue(speech)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e);
            }
        });

    }

    public void setActivityHint(String hint) {

        databaseReference.child(PACKAGE_TABLE).child(packageName).child(ACTIVITY_TABLE).child(activityName).child("hint").setValue(hint);

    }

    public void getActivityHint() {

        databaseReference.child(PACKAGE_TABLE).child(packageName).child(ACTIVITY_TABLE).child(activityName).child("hint").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    activityHint = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    public void speakActivityHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(activityHint);
        } else {
            ttsUnder20(activityHint);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {

        Log.d(TAG, "ttsUnder20() called with: text = [" + text + "]");

        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {

        Log.d(TAG, "ttsGreater21() called with: text = [" + text + "]");

        String utteranceId = activity.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);


    }

}


