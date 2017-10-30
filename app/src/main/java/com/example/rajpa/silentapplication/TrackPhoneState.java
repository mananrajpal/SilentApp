package com.example.rajpa.silentapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by rajpa on 29/10/2017.
 */

public class TrackPhoneState extends BroadcastReceiver {
    String state;
    static String phoneSate = "Idle";
    public TrackPhoneState()
    {

    }
    public String getPhoneSate()
    {
        return phoneSate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //we override the onReceive method to allow us to use the curret state of the phone
        try
        {
            //gets the current state of the phone
            state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state==null)
            {
                //checks if the state is null i.e. an outgoing call is being made
                Log.d("Testing-call","Outgoing call");
                phoneSate = "Outgoing";
                Toast.makeText(context,"Outgoing Call",Toast.LENGTH_LONG).show();
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                //checks if the state is ringing i.e. an incoming call
                Log.d("Testing-call", "Incoming call");
                phoneSate="Incoming";
                Toast.makeText(context,"Incoming Call",Toast.LENGTH_LONG).show();
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                //checks if the phone is idle
                Toast.makeText(context, "Phone on idle",Toast.LENGTH_LONG).show();
                phoneSate="Idle";
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                //checks if user accepted the incoming call
                Toast.makeText(context, "Received Phone", Toast.LENGTH_LONG).show();
                phoneSate= "On Call";
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
