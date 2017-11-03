package com.example.rajpa.silentapplication;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
        final TextView challenge = (TextView) findViewById(R.id.Challenge);
        /*----------------------------------------------------------------------------------------*/

        myActionBar.setLogo(R.drawable.silent); //setting up the logo on ActionBar


        /*------------------------Making the device discoverable to other devices-----------------*/
        if(mBluetoothAdapter==null)
        {
            //checks if the adapter status is null i.e. the device does not have bluetooth support
            Toast.makeText(getApplicationContext(),"Your mobile does not support bluetooth",Toast.LENGTH_LONG).show();
        }
        else if(!mBluetoothAdapter.isEnabled())
        {
            //if the bluetooth is not enabled it enables in with the help of intent
            //action request enable
            Intent myIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(myIntent, REQUEST_ENABLE_BT);
        }
        //After making sure bluetooth is enabled device is made discoverable to other devices.
        //At the moment device is discoverable for unlimited time.
        //For future the device will be undiscoverabe when the user scans the QR again while
        //exiting the Silent Area
        Intent makeDeviceDiscoverable = new Intent(mBluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        makeDeviceDiscoverable.putExtra(mBluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
        startActivityForResult(makeDeviceDiscoverable,2);

        /*----------------------------------------------------------------------------------------*/

        /*Setting up the onClickListener for respective text fields.*/
        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInternetState checkInternetState = new CheckInternetState(view);
                if(checkInternetState.getSate()==true)
                {
                    CheckNudge checkNudge = new CheckNudge();
                    StartAsyncTaskInParallel(checkNudge);
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
                CheckInternetState checkInternetState = new CheckInternetState(view);
                if(checkInternetState.getSate()==true) {
                    Intent myIntent = new Intent(getApplicationContext(), ViewDevices.class);
                    startActivity(myIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Please connect to the Internet",Toast.LENGTH_LONG).show();
                }
            }
        });
        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInternetState checkInternetState = new CheckInternetState(view);
                if(checkInternetState.getSate()==true) {

                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Please connect to the Internet",Toast.LENGTH_LONG).show();
                }

            }
        });
        /*----------------------------------------------------------------------------------------*/
    }

    /*This is an executor method which allows async task to be executed in parallel as was my requirement
    * reference - https://stackoverflow.com/questions/18357641/is-it-possible-to-run-multiple-asynctask-in-same-time*/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void StartAsyncTaskInParallel(CheckNudge task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void StartResetNudgeInParallel(Resetnudge task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    /*This is an async task that checks if the nudge column value has been changed to 1.
    * When the user selects nudge the user, nudge column value gets changed from 0 to 1
    * Which makes this thread pick up that a nudge has been received from other user
    * about this device. The post execute method then vibrates the phone and displays a message
    * to end the call.*/
    class CheckNudge extends AsyncTask<Void, Void, String>
    {
        String website = "http://discoloured-pops.000webhostapp.com/CheckNudge.php";
        String result;
        Vibrator v;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Nudge-Checking","Inside the pre-execute");
            //vibrator object that helps us to vibrate the device for choice of time.
            v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
            result="0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //display a toast to user to end the call.
            Toast.makeText(getApplicationContext(),"Please End the Call, neighbour is getting disturbed", Toast.LENGTH_LONG).show();
            v.vibrate(1500); //vibrate the device for 1500 milli seconds.
            //Start another async task which should run in paraller with other async task in application
            Resetnudge resetnudge = new Resetnudge();
            StartResetNudgeInParallel(resetnudge);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Void... params) {
            //until there is no nudge
            while(result.equals("0"))
            {
                try
                {
                    /*This method uses the website string to convert to an url which then is
                    * used to open an httpurlconnection. BufferWriter is used to write on the
                    * output stream after which to read the echo from php BufferReader is used.*/
                    Log.d("Nudge-checking","Started doInBackground the PreExecute");
                    URL url = new URL(website);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream  = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("deviceId","UTF-8")+"=" +
                            URLEncoder.encode(mBluetoothAdapter.getAddress(),"UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    outputStream.close();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String line="";
                    while((line = bufferedReader.readLine())!=null)
                    {
                        Log.d("Nudge-checking",line);
                        result = line;
                    }
                    inputStream.close();
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                }catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if(result.equals(1))
            {
                return result;
            }

            return null;
        }
    }

    /*This async task is started by CheckNudge async task i.e. works on the logic that when a
    * users nudges this device i.e. the nudge value changes to 1, this async task is called
    * to reset the column back to 0 to users to nudge this device repetitively. After the value
     * is changed to 0 this async task starts the CheckNudge task again to allow the device
     * to be available to get the nudge again.*/
    class Resetnudge extends AsyncTask<Void, Void, String>
    {
        String website;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Nudge-checking","Inside the preexecute of ResetNudge");
            website = "http://discoloured-pops.000webhostapp.com/ResetNudge.php";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Nudge-checking","Inside the postexecute of ResetNudge");
            //starts the CheckNudge async task as parallel async tasks
            //Allowing the device to check again if any user nudged this device.
            CheckNudge checkNudge = new CheckNudge();
            StartAsyncTaskInParallel(checkNudge);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Void... params) {
            try
            {
                /*This method uses the website string to convert to an url which then is
                    * used to open an httpurlconnection. BufferWriter is used to write on the
                    * output stream after which to read the echo from php BufferReader is used.*/
                Log.d("Nudge-checking","Inside the doInBackground of ResetNudge");
                URL url = new URL(website);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("deviceId","UTF-8")+"=" +
                        URLEncoder.encode(mBluetoothAdapter.getAddress(),"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                outputStream.close();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "",line = "";
                while ((line= bufferedReader.readLine())!=null)
                {
                    Log.d("Nudge-checking","Reset-nudge result:"+line);
                    result = line;
                }
                return result;

            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
