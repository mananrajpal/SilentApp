package com.example.rajpa.silentapplication;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class StudentAccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_access);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); //setting the custom Toolbar
        setSupportActionBar(myToolbar);
        initializeUI();
    }

    private void initializeUI()
    {
        /*------------------------------------------------------------------------------------------*/
        //setting up objects with respective to their xml views.
        ActionBar myActionBar = getSupportActionBar();
        final TextView createQR = (TextView) findViewById(R.id.CreateQr);
        TextView scanDevices = (TextView) findViewById(R.id.ScanDevices);
        TextView challenge = (TextView) findViewById(R.id.Challenge);
        /*----------------------------------------------------------------------------------------*/

        myActionBar.setLogo(R.drawable.silent); //setting up the logo on ActionBar

        /*Setting up the onClickListener for respective text fields.*/
        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(),createQR.class);
                startActivity(myIntent);
            }
        });

        scanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent (getApplicationContext(), ViewDevices.class);
                startActivity(myIntent);
            }
        });
        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        /*----------------------------------------------------------------------------------------*/
    }
}
