package com.example.rajpa.silentapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
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
 * Created by rajpa on 30/10/2017.
 */
class BackgroundWorker extends AsyncTask<String, Void, String>
{
    View v;
    Activity ac;
    /*Database is created as a local host not on a webserver which is why the url is set to static
    * local host address.*/
    String login_url = "http://discoloured-pops.000webhostapp.com/login.php";
    public BackgroundWorker(View context)
    {
        v = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(v.getContext(),aVoid,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        //getting the operation type
        String type = params[0];
        if(type=="login")
        {
            String username = params[1];
            String password = params[2];
            Log.d("Checking-connection","Inside the connection");
            try
            {
                URL url = new URL(login_url); //basic url created from the string.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");//request method set as post to get value from php
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
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
                return result;
            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            }catch(IOException e)
            {
                //this catch block added for the HTTPURL connection
                e.printStackTrace();
            }

        }

        return null;
    }



}
