package com.example.rajpa.silentapplication;

import android.app.Service;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

/**
 * Created by rajpa on 31/10/2017.
 */

public class CheckInternetState {
    View v;
    Boolean isInternetConnected = false;
    public CheckInternetState(View parentView)
    {
        v = parentView;
        checkInternetConnection();
    }
    /*https://www.youtube.com/watch?v=3-ShLEjktq8
                * This code has been learned and implemented from this youtube video
                * This part checks if the Connectivity Manager is instantiated, if yes
                * It gets the status of the network using method getActiveNetworkInfo
                * The method brings with it the current state of the network.
                * It checks if the network is connected then the async task is performed,
                * database connection is performed then*/
    private void checkInternetConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) v.getContext().getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null)
            {
                isInternetConnected = true;
            }
        }
    }
    public Boolean getSate()
    {
        return isInternetConnected;
    }
}
