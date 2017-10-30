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
import android.widget.Toast;

public class StaffAccess extends AppCompatActivity implements StaffLogin.loginListener {


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

    private void initializeUI()
    {
        ActionBar myBar = getSupportActionBar();
        myBar.setLogo(R.drawable.silent);
        StaffLogin fragment1= new StaffLogin();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.staffFragment,fragment1);
        fragmentTransaction.commit();

    }


}
