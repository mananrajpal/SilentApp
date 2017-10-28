package com.example.rajpa.silentapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.oned.MultiFormatOneDReader;

public class createQR extends AppCompatActivity {
    String androidId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initializeUI();
    }

    private void initializeUI()
    {
        /*------------------------------------------------------------------------------------------*/
        //setting up objects with respective to their xml views.
        ActionBar myActionBar = getSupportActionBar();
        /*----------------------------------------------------------------------------------------*/
        myActionBar.setLogo(R.drawable.silent); //setting up the logo on ActionBar
        androidId = getAndroidId();
        createQR();

        Log.d("Id-Testing", androidId);
    }

    private String getAndroidId()
    {
        String id;
        id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    private void createQR()
    {

    }

}
