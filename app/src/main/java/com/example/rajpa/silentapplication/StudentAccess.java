package com.example.rajpa.silentapplication;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
                    new DownloadQuestion().execute();
                    Intent challenge = new Intent(getApplicationContext(), Challenge.class);
                    startActivity(challenge);

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

    protected void storeQuestion(String question)
    {
        String filename= "question.txt";
        File file;
        file = new File(getApplication().getFilesDir(), filename);
        try
        {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(question.getBytes());
            Log.d("Store-question","Written to File: "+ question );
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**/
    class DownloadQuestion extends AsyncTask<Void, Void, String>
    {
        String website, result;
        @Override
        protected void onPreExecute()
        {
            website = "http://discoloured-pops.000webhostapp.com/GetQuestion.php";
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            storeQuestion(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Void... params) {
            try
            {
                URL url = new URL(website);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line="",result="";
                while((line = bufferedReader.readLine())!=null)
                {
                    result = line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
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
