# SAKI - Speech Assisted K Interface

  Saki is a library which can be used by android developers to enable a speech assisted **blind mode** just by just integrating it with the existing app.


## Download

Use gradle to add the dependancy - 

`
dependencies {
  compile 'com.dark.saki:1.0.0'
}
`


## How do I use SAKI ?

All you have to do is just create an instance of our class `Saki` in the required activity to enable the blind mode and register all kinds of UI elements using various funcions available with-in the `Saki` class.


`
Saki saki = new Saki(getApplicationContext());
`


### some use cases - 

register Button by passing the `button` instance and the **hint** for the use case of the button

`
saki.registerButton(button,"Book a cab!");
`

register EditText by passing the `editText` instance and the **hint** for the use case of the editText

`
saki.registerEditText(editText,"Set the pick-up location to Bengaluru");
`

optionally register the default back-button 

`
saki.registerBackButton("Go back!");
`


## Open source libraries used

`
Volley-android from official developers.android.com
Firebase from firebase.google.com
`
