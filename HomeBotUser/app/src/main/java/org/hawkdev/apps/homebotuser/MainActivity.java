package org.hawkdev.apps.homebotuser;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.access.KandyConnectServiceNotificationListener;
import com.genband.kandy.api.access.KandyConnectionState;
import com.genband.kandy.api.access.KandyLoginResponseListener;
import com.genband.kandy.api.access.KandyLogoutResponseListener;
import com.genband.kandy.api.access.KandyRegistrationState;
import com.genband.kandy.api.services.calls.IKandyCall;
import com.genband.kandy.api.services.calls.IKandyIncomingCall;
import com.genband.kandy.api.services.calls.IKandyOutgoingCall;
import com.genband.kandy.api.services.calls.KandyCallResponseListener;
import com.genband.kandy.api.services.calls.KandyCallServiceNotificationListener;
import com.genband.kandy.api.services.calls.KandyCallState;
import com.genband.kandy.api.services.calls.KandyOutgingVoipCallOptions;
import com.genband.kandy.api.services.calls.KandyRecord;
import com.genband.kandy.api.services.calls.KandyView;
import com.genband.kandy.api.services.common.KandyMissedCallMessage;
import com.genband.kandy.api.services.common.KandyWaitingVoiceMailMessage;
import com.genband.kandy.api.utils.KandyIllegalArgumentException;
import com.genband.kandy.api.utils.KandyLog;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import static android.R.attr.process;
import static com.genband.mobile.impl.utilities.Config.log;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //TODO add kandy api key and api secret here
    private static final String KANDY_API_KEY = "----";
    private static final String KANDY_API_SECRET = "----";

    //TODO add username and password for controlling user
    private static final java.lang.String HOMEBOT_USER_NAME = "----";
    private static final String HOMEBOT_USER_PASS = "----";

    //TODO add user name for robot app
    private static final java.lang.String HOMEBOT_ROBOT_NAME = "----";


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    private static final int CMD_STOP = 0;
    private static final int CMD_FORWARD = 1;
    private static final int CMD_REVERSE = 2;
    private static final int CMD_LEFT = 3;
    private static final int CMD_RIGHT = 4;


    Dpad dpad;
    private KandyView remoteVideoView;
    private KandyView localVideoView;
    private float[] mGravity;
    private float[] mGeomagnetic;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mAngle;
    private int mOrigin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDpadControl();

        localVideoView = (KandyView)findViewById (R.id.kandy_local_video_view);
        remoteVideoView = (KandyView)findViewById (R.id.kandy_remote_video_view);

        initializeKandy();

        initializeHeadTracking();

    }


    private void initializeKandy() {



        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


            //code for requesting permissions
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        else{
            Kandy.initialize(getApplicationContext(), KANDY_API_KEY, KANDY_API_SECRET);
            Kandy.getKandyLog().setLogLevel(KandyLog.Level.VERBOSE);

            registerKandyNotificationListener();

            loginHomeBotUser();

            registerCallNottificaitons();
        }
    }

    private void registerCallNottificaitons() {
        Kandy.getServices().getCallService().registerNotificationListener(new KandyCallServiceNotificationListener() {
            @Override
            public void onIncomingCall(IKandyIncomingCall iKandyIncomingCall) {
                iKandyIncomingCall.setLocalVideoView(localVideoView);
                iKandyIncomingCall.setRemoteVideoView(remoteVideoView);
                iKandyIncomingCall.accept(true, new KandyCallResponseListener() {
                    @Override
                    public void onRequestSucceeded(IKandyCall iKandyCall) {
                        Toast.makeText(MainActivity.this, "Call Accepted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRequestFailed(IKandyCall iKandyCall, int i, String s) {

                    }
                });
            }

            @Override
            public void onMissedCall(KandyMissedCallMessage kandyMissedCallMessage) {

            }

            @Override
            public void onWaitingVoiceMailCall(KandyWaitingVoiceMailMessage kandyWaitingVoiceMailMessage) {

            }

            @Override
            public void onCallStateChanged(KandyCallState kandyCallState, IKandyCall iKandyCall) {

            }

            @Override
            public void onVideoStateChanged(IKandyCall iKandyCall, boolean b, boolean b1) {

            }

            @Override
            public void onGSMCallIncoming(IKandyCall iKandyCall, String s) {

            }

            @Override
            public void onGSMCallConnected(IKandyCall iKandyCall, String s) {

            }

            @Override
            public void onGSMCallDisconnected(IKandyCall iKandyCall, String s) {

            }
        });
    }

    private void loginHomeBotUser() {
        KandyRecord kandyRecord = null;
        try {
            kandyRecord = new KandyRecord(HOMEBOT_USER_NAME);
        } catch (KandyIllegalArgumentException e) {
            //TODO insert your code here
            return;
        }
        String password = HOMEBOT_USER_PASS;

        //show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in to kandy");
        progressDialog.show();


        Kandy.getAccess().login(kandyRecord, password, new KandyLoginResponseListener() {

            @Override
            public void onRequestFailed(int responseCode, String err) {
                //TODO insert your code here
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed to login. " + err, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginSucceeded() {
                //TODO insert your code here.
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Successfully Logged in.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutHomeBotUser(){
        Kandy.getAccess().logout(new KandyLogoutResponseListener() {

            @Override
            public void onRequestFailed(int responseCode, String err) {
                //TODO insert your code here
                Toast.makeText(MainActivity.this, "Failed to logout. " + err, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutSucceeded() {
                //TODO insert your code here
                Toast.makeText(MainActivity.this, "Successfully Logged out.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onVideoCallClick(View view) {

        KandyRecord caller = null;
        KandyRecord callee = null;
        try {
            callee = new KandyRecord(HOMEBOT_ROBOT_NAME);
        } catch (KandyIllegalArgumentException e) {
            //TODO insert your code here
            return;
        }

        IKandyOutgoingCall currentCall = Kandy.getServices().getCallService().createVoipCall(caller,
                callee, KandyOutgingVoipCallOptions.START_CALL_WITH_VIDEO);

//YOU MUST TO PASS THE NON NULL VALUE OF LOCAL VIEW TO THE KandyOutgoingCall or/and KandyIncomingCall
        currentCall.setLocalVideoView(localVideoView);
//YOU MUST TO PASS THE NON NULL VALUE OF REMOTE VIEW TO THE KandyOutgoingCall or/and KandyIncomingCall
        currentCall.setRemoteVideoView(remoteVideoView);
        currentCall.establish(new KandyCallResponseListener() {

            @Override
            public void onRequestSucceeded(IKandyCall call) {
                //TODO insert your code here
            }

            @Override
            public void onRequestFailed(IKandyCall call, int arg1, String arg2) {
                //TODO insert your code here
            }
        });
    }

    private void registerKandyNotificationListener() {
        Kandy.getAccess().registerNotificationListener(new KandyConnectServiceNotificationListener() {
            @Override
            public void onRegistrationStateChanged(KandyRegistrationState kandyRegistrationState) {

            }

            @Override
            public void onConnectionStateChanged(KandyConnectionState kandyConnectionState) {

            }

            @Override
            public void onInvalidUser(String s) {

            }

            @Override
            public void onSessionExpired(String s) {

            }

            @Override
            public void onSDKNotSupported(String s) {

            }

            @Override
            public void onCertificateError(String s) {

            }

            @Override
            public void onServerConfigurationReceived(JSONObject jsonObject) {

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initializeKandy();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoutHomeBotUser();
    }


    private void initializeDpadControl() {
        dpad = new Dpad();

        View view = findViewById(R.id.activity_main);

        view.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (Dpad.isDpadDevice(event)){

                    int press = dpad.getDirectionPressed(event);

                    switch (press) {
                        case Dpad.LEFT:
                            // Do something for LEFT direction press
                            sendCommand(CMD_LEFT);
                            return true;
                        case Dpad.RIGHT:
                            // Do something for RIGHT direction press
                            sendCommand(CMD_RIGHT);
                            return true;
                        case Dpad.UP:
                            // Do something for UP direction press
                            sendCommand(CMD_FORWARD);
                            return true;

                        case Dpad.DOWN:
                            // Do something for UP direction press
                            sendCommand(CMD_REVERSE);
                            return true;

                        case Dpad.RELEASE:{
                            sendCommand(CMD_STOP);
                        }

                    }
                }
                return true;
            }
        });
    }

    public void onClickSwitch(View view) {
        switch (view.getId()) {
            case R.id.bt_stop:
                sendCommand(CMD_STOP);
                break;
            case R.id.bt_forward:
                sendCommand(CMD_FORWARD);
                break;
            case R.id.bt_backward:
                sendCommand(CMD_REVERSE);
                break;
            case R.id.bt_left:
                sendCommand(CMD_LEFT);
                break;
            case R.id.bt_right:
                sendCommand(CMD_RIGHT);
                break;
        }

    }


    private void sendCommand(int command) {
        FirebaseDatabase.getInstance().getReference("command").setValue(command);
    }


    private void sendAngle(int i) {
        if(i > 0 && i <= 180){
            i += 20 - 1;
            sendCommand(i);
        }
        else{
            Toast.makeText(this, "Not a valid angle", Toast.LENGTH_SHORT).show();
        }
    }

//    public void decAngle(View view) {
//        String angle = ((EditText) findViewById(R.id.angle)).getText().toString();
//        ((EditText) findViewById(R.id.angle)).setText("" + (Integer.parseInt(angle) - 10));
//    }
//
//    public void incAngle(View view) {
//        String angle = ((EditText) findViewById(R.id.angle)).getText().toString();
//        ((EditText) findViewById(R.id.angle)).setText("" + (Integer.parseInt(angle) + 10));
//    }



    private void initializeHeadTracking() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            int deg = (int)Math.toDegrees(event.values[0]);

        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimut = orientation[0]; // orientation contains: azimut, pitch and roll

                int newAngle = (int)(Math.toDegrees(azimut));
                if(newAngle > mAngle + 5 || newAngle < mAngle - 5 ) {
                    mAngle = newAngle;
                    Log.d("ROT", "rotation angle: " + mAngle);

                    processOrientationChange();
                }

            }
        }
    }

    private void processOrientationChange() {
        int servoAngle = mAngle - mOrigin;

        //mapping angle to servo.
        servoAngle = -servoAngle;
        servoAngle += 90;

        Log.d("Processed orientation", "Sending servo angle: " + servoAngle);
        sendAngle(servoAngle);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void resetOrientation(View view) {
        mOrigin = mAngle;
        sendAngle(90);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSensorManager != null) {
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 1000000);
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 1000000);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }
}
