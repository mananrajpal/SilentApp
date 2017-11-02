package com.example.rajpa.silentapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.aztec.encoder.Encoder;

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
import java.util.List;


public class ComplaintPage extends Fragment {

    List<BluetoothDevices> listOfDevices;
    complaintListener mListener;
    Integer listPosition;
    public interface complaintListener
    {
        public void getStatus(String s);
    }
    public ComplaintPage() {
        // Required empty public constructor
    }

    public void setDevies(List<BluetoothDevices> devices, Integer position)
    {
        listOfDevices = devices;
        listPosition = position;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_complaint_page, container, false);
        TextView nudgeDevice = (TextView) v.findViewById(R.id.nudgeDevice);
        TextView reportDevice = (TextView) v.findViewById(R.id.reportDevice);

        nudgeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        reportDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportWorker reportWorker = new ReportWorker(v);
            }
        });
        return v;
    }


    @Override
    public void onAttach(Activity context) {
        try
        {
            mListener=(complaintListener)context;
        }catch (ClassCastException e)
        {

        }
        super.onAttach(context);

    }

    class ReportWorker extends AsyncTask<Void,Void,String>
    {
        View view;
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.reportBar);
        String website = "http://discoloured-pops.000webhostapp.com/ReportDevice.php";
        public ReportWorker(View v)
        {
            this.view = v;
        }
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            if(s.equals("Success"))
            {
                Toast.makeText(getActivity(),"Device Reported Successfully",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(),"Error Reporting Device",Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... params) {
            try
            {
                URL url = new URL(website);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data = URLEncoder.encode("deviceId","UTF-8")+"=" +
                                    URLEncoder.encode(listOfDevices.get(listPosition).getMacAddress(),"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line="", result="";
                while((line = bufferedReader.readLine())!=null)
                {
                    Log.d("Checking-reporting", line);
                    result += line;
                }

            }catch (MalformedURLException e)
            {

            }catch (IOException e)
            {

            }
            return null;
        }
    }


}
