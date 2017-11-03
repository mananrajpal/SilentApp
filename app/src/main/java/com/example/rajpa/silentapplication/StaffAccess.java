package com.example.rajpa.silentapplication;

import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class StaffAccess extends AppCompatActivity implements StaffLogin.loginListener {

    //Interface overridden by the this activity
    @Override
    public void loginStaff(Boolean validated) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_access);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        initializeUI();
    }

    //Inflating Main Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mn = getMenuInflater();
        mn.inflate(R.menu.staffmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Override method to listen the user clicks on the menu populated on the device
    /*This section will be developed in the future allowing the staff to scan the qr generated on students device
    * and reset the complains back to 0.*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.scanQr:
                Toast.makeText(getApplicationContext(), "Please get the QR ready to be scanned.", Toast.LENGTH_LONG).show();
                break;
            case R.id.resetComplaints:
                Toast.makeText(getApplicationContext(), "Please get the Device-id ready for input.", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeUI()
    {
        //Starts the fragment transaction to populate on the activity view.
        ActionBar myBar = getSupportActionBar();
        myBar.setLogo(R.drawable.silent);
        StaffLogin fragment1= new StaffLogin();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.staffFragment,fragment1);
        fragmentTransaction.commit();

    }


}
