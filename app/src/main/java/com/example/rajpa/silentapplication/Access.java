package com.example.rajpa.silentapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Access extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); //setting the custom Toolbar
        setSupportActionBar(myToolbar);
        initializeUI();
    }

    private void initializeUI()
    {
      /*------------------------------------------------------------------------------------------*/
        //setting up objects with respective to their xml views.
        ActionBar myActionBar = getSupportActionBar();
        final TextView studentAccess = (TextView)findViewById(R.id.StudentAccess);
        TextView staffAccess = (TextView)findViewById(R.id.StaffAccess);
        /*----------------------------------------------------------------------------------------*/

        myActionBar.setLogo(R.drawable.silent); //setting up the logo on ActionBar

        askPermission();
        /*setting up onClickListener to respective textViews.*/
        studentAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent studentIntent = new Intent(getApplicationContext() ,StudentAccess.class);
                startActivity(studentIntent);
            }
        });
        staffAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Entering Staff Access", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void askPermission()
    {

        /*
        Code reference taken from https://developer.android.com/training/permissions/requesting.html
        This code has been added as android from 6.0 has changed the way how permissions are granted.
        Before 5.0 it was granted automatically while installing but from 6.0 user have to accept it
        while the application is launched for the first time.
        */
        /*----------------------------------------------------------------------------------------*/
        //this condition checks if the following application doesn't  have the permission to read phone state
        if(ContextCompat.checkSelfPermission(Access.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
        {
            //this conditions shows an explanation to user why is this application asking for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(Access.this, Manifest.permission.READ_PHONE_STATE))
            {
                ActivityCompat.requestPermissions(Access.this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
            else
            {
                //else ask the user to grant or deny the permission
                ActivityCompat.requestPermissions(Access.this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }
        else
        {
            //do nothing
        }

        /*------------------this condition checks for getting acces for outgoing calls------------------*/
        if(ContextCompat.checkSelfPermission(Access.this, Manifest.permission.PROCESS_OUTGOING_CALLS)
                !=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Access.this, Manifest.permission.PROCESS_OUTGOING_CALLS))
            {
                ActivityCompat.requestPermissions(Access.this, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},2);
            }
            else
            {
                ActivityCompat.requestPermissions(Access.this, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},2);
            }

        }
        else
        {
            //do nothing
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
                if(grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(Access.this,Manifest.permission.READ_PHONE_STATE)
                            ==PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(Access.this, "Permission Granted for incoming calls", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(Access.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(Access.this, Manifest.permission.PROCESS_OUTGOING_CALLS)
                            ==PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(Access.this, "Permission granted for outgoing calls", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }
}
