package com.example.rajpa.silentapplication;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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


public class GetScore extends Fragment {




    public GetScore() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_get_score, container, false);
        GetMyScore getMyScore = new GetMyScore(v);
        GetMaxScore getMaxScore = new GetMaxScore(v);
        StartViewScoreInParallel(getMyScore);
        StartMaxScoreInParallel(getMaxScore);
        return v;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void StartViewScoreInParallel(GetMyScore task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void StartMaxScoreInParallel(GetMaxScore task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    class GetMyScore extends AsyncTask<Void, Void, String>
    {
        String website, deviceId;
        ProgressBar progressBar;
        View fragmentView;
        public GetMyScore(View v)
        {
            fragmentView = v;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            website = "http://discoloured-pops.000webhostapp.com/GetMyScore.php";
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            deviceId = bluetoothAdapter.getAddress();
            progressBar =(ProgressBar) fragmentView.findViewById(R.id.scoreProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Store-question","My score:"+ s);
            TextView scoreText = (TextView)getActivity().findViewById(R.id.yourScore);
            scoreText.setText(s);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try
            {
                URL url = new URL(website);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("deviceId", "UTF-8")+ "="+ URLEncoder.encode(deviceId,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line="", result = "";
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
    class GetMaxScore extends AsyncTask<Void, Void, String>
    {
        String website;
        ProgressBar progressBar;
        View fragmentView;
        public GetMaxScore(View v)
        {
            fragmentView = v;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            website = "http://discoloured-pops.000webhostapp.com/GetMaxScore.php";
            progressBar =(ProgressBar) fragmentView.findViewById(R.id.maxScoreProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Store-question","My score:"+ s);
            TextView scoreText = (TextView)getActivity().findViewById(R.id.maxScore);
            scoreText.setText(s);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try
            {
                URL url = new URL(website);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line="", result = "";
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
