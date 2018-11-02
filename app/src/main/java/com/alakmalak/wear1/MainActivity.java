package com.alakmalak.wear1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.alakmalak.wear1.SharedPreference.SERVICE_HANDLER;

public class MainActivity extends WearableActivity {
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private final static String TAG = MainActivity.class.getSimpleName();

    private final static String[] permissions = new String[]{Manifest.permission.BODY_SENSORS,  Manifest.permission.RECORD_AUDIO,  Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS };

    TextView start_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If we already have all the permissions start immediately, otherwise request permissions
        if (permissionsGranted()) {
            init();
        } else {
            Log.d(TAG, "Requesting permissions");
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_PERMISSION);
        }
    }

    /**
     * Checks if all necessary permissions have been granted
     *
     * @return True if all necessary permissions have been granted, false otherwise
     */
    private boolean permissionsGranted() {
        Boolean result = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                result = false;
            }
        }
        return result;
    }

    public void init(){
        try {
            start_service = findViewById(R.id.start_service);

            if (SharedPreference.getPreference(getApplicationContext(),SERVICE_HANDLER,"n").equals("y")){
                start_service.setText("Stop Service");
            }else{
                start_service.setText("Start Service");
            }

            start_service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (start_service.getText().toString().equals("Start Service")){
                        SharedPreference.setPreference(getApplicationContext(),SERVICE_HANDLER,"y");
                        BackService.start_handler = true;
                        start_service.setText("Stop Service");
                        startService(new Intent(MainActivity.this, BackService.class));
                    }else{
                        SharedPreference.setPreference(getApplicationContext(),SERVICE_HANDLER,"n");
                        BackService.start_handler = false;
                        start_service.setText("Start Service");
                        stopService(new Intent(MainActivity.this, BackService.class));
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}