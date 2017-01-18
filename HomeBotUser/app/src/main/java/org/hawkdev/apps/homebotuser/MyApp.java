package org.hawkdev.apps.homebotuser;

import android.*;
import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.access.KandyConnectServiceNotificationListener;
import com.genband.kandy.api.access.KandyConnectionState;
import com.genband.kandy.api.access.KandyRegistrationState;
import com.genband.kandy.api.utils.KandyLog;

import org.json.JSONObject;

/**
 * Created by nomo on 12/18/16.
 */

public class MyApp extends Application {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
