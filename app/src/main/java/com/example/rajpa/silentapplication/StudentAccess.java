package com.example.rajpa.silentapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StudentAccess extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Integer REQUEST_ENABLE_BT=1;
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
        if(mBluetoothAdapter==null)
        {
            Toast.makeText(getApplicationContext(),"Your mobile does not support bluetooth",Toast.LENGTH_LONG).show();
        }
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent myIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(myIntent, REQUEST_ENABLE_BT);
        }
        Intent makeDeviceDiscoverable = new Intent(mBluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        makeDeviceDiscoverable.putExtra(mBluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
        startActivityForResult(makeDeviceDiscoverable,2);
        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInternetState checkInternetState = new CheckInternetState(view);
                if(checkInternetState.getSate()==true)
                {
                    Intent myIntent = new Intent(getApplicationContext(),createQR.class);
                    startActivity(myIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Please connect to the Internet",Toast.LENGTH_LONG).show();
                }
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
