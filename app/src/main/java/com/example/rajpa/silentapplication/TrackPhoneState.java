package com.example.rajpa.silentapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
                createQR qrCreator = new createQR();
                if(qrCreator.getSignal()==true)
                {
                    String deviceId= qrCreator.getDeviceId();
                    UpdateState updateState = new UpdateState(context);
                    Log.d("Check-update","Inside the if condition"+phoneSate);
                    updateState.execute(deviceId, phoneSate);
                }
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                //checks if user accepted the incoming call
                Toast.makeText(context, "Received Phone", Toast.LENGTH_LONG).show();
                phoneSate= "On Call";
                createQR qrCreator = new createQR();
                if(qrCreator.getSignal()==true)
                {
                    String deviceId= qrCreator.getDeviceId();
                    UpdateState updateState = new UpdateState(context);
                    Log.d("Check-update","Inside the if condition"+phoneSate);
                    updateState.execute(deviceId, phoneSate);
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    class UpdateState extends AsyncTask<String, Void, String>
    {
        Context cr;
        public UpdateState(Context context)
        {
            cr = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Check-update",s);
            Toast.makeText(cr.getApplicationContext(), s, Toast.LENGTH_LONG).show();
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            String deviceId = params[0];
            String phoneState = params[1];
            String registerUrl = "http://discoloured-pops.000webhostapp.com/UpdateState.php";
            try
            {
                URL url = new URL(registerUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data= URLEncoder.encode("deviceId","UTF-8")+"="+URLEncoder.encode(deviceId,"UTF-8")+"&"
                        +URLEncoder.encode("state","UTF-8")+"="+URLEncoder.encode(phoneState,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result="", line="";
                while((line = bufferedReader.readLine()) != null)
                {
                    result +=  line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.d("Check-update","Finished reading");
                return result;

            }catch(MalformedURLException e)
            {

            }
            catch(IOException e)
            {

            }
            return null;
        }
    }
}
