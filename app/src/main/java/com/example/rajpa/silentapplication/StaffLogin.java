package com.example.rajpa.silentapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class StaffLogin extends Fragment
{
    Boolean isInternetConnected = false;
    Boolean validated = false;
    loginListener mListener;
    public interface loginListener
    {
        public void loginStaff(Boolean validated);
    }
    public StaffLogin() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try
        {
            mListener = (loginListener) context;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement ToolbarListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_staff_login, container, false);
        Button checkCredentials = (Button) v.findViewById(R.id.submitLogin);


        /*----------------------Starting the threading of login with onClickListener--------------*/
        checkCredentials.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckInternetState stateChecker = new CheckInternetState(v);
                if(stateChecker.getSate()==true)
                {
                    /*-------------------------getting the associated views from xml file---------*/
                    EditText nameEntered = (EditText) getActivity().findViewById(R.id.userName);
                    EditText passEntered = (EditText) getActivity().findViewById(R.id.userPass);
                    /*------------------------Converting the input to string----------------------*/
                    String userName = nameEntered.getText().toString();
                    String userPass = passEntered.getText().toString();
                    String type = "login"; //Operation type is login
                    /*----------------------------------------------------------------------------*/
                    /*--------------------Validating data using TextUtils Library-----------------*/
                    if(TextUtils.isEmpty(userName))
                    {
                        nameEntered.setError("Enter name");
                        return;
                    }
                    if(TextUtils.isEmpty(userPass))
                    {
                        passEntered.setError("Enter password");
                        return;
                    }
                    else
                    {
                        //performing the async threading of getting records from database
                        BackgroundWorker br = new BackgroundWorker();
                        br.execute(type, userName,userPass);
                    }

                }
                else
                {
                    Toast.makeText(getActivity(),"Please connect to Internet",Toast.LENGTH_LONG).show();
                }
            }

        });
        return v;
    }





    class BackgroundWorker extends AsyncTask<String, Void, String>
    {


    /*Database is created as a local host not on a webserver which is why the url is set to static
    * local host address.*/



        String login_url = "http://discoloured-pops.000webhostapp.com/login.php";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            String access = aVoid.substring(0,7);
            Log.d("Checking-connection","Inside the post executed");
            if(access.equals("Welcome"))
            {
                Log.d("Result-string","status");
            }
            Toast.makeText(getActivity(),aVoid,Toast.LENGTH_LONG).show();
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
                /*This code has been learned and implemented from https://www.youtube.com/watch?v=eldh8l8yPew
                * This code converts the string url to actual url
                * HTTP connection is opened using the url created,
                * Output, Input is set to true on that http connection,
                * An output stream is created instantiated with the http outputstream
                * In order to write to output stream a buffered writer is created
                * As the php taken in UTF-8 type string the data is encoded in UTF-8,
                * PHP variables takes the value from the url so variables are attached to the post data
                * All the connections are then closed except the httpURLConnection,
                * this connection is used again to read the echo's from the php file
                * An InputStream is instantiated with httpURLInputStream.
                * That input stream is read using the BufferReader and the result is then returned,
                * which is then read in the post execute override method*/
                try
                {
                    URL url = new URL(login_url); //basic url created from the string.
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");//request method set as post to get value from php
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    Log.d("Checking-connection","After the output stream");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.d("Checking-connection","After the buffered writer");
                    String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                            +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    Log.d("Checking-connection","After the input stream");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    Log.d("Checking-connection","After the bufferred reader");
                    String result="", line="";
                    while((line = bufferedReader.readLine()) != null)
                    {
                        result +=  line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    Log.d("Checking-connection",result);
                    return result;
                }catch (MalformedURLException e)
                {
                    Toast.makeText(getContext(), "Problem with creating URL",Toast.LENGTH_LONG).show();
                }catch(IOException e)
                {
                    //this catch block added for the HTTPURL connection
                    Toast.makeText(getContext(), "Problem with Connecting to Internet",Toast.LENGTH_LONG).show();
                }

            }
            return null;
        }



    }


}
