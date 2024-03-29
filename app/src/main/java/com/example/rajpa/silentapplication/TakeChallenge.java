package com.example.rajpa.silentapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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

import com.google.zxing.aztec.encoder.Encoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class TakeChallenge extends Fragment
{
    String question = "", readLine="";
    Boolean isCorrect = false;
    public TakeChallenge() {
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
        View v = inflater.inflate(R.layout.fragment_take_challenge, container, false);
        try
        {
            FileInputStream file = getActivity().openFileInput("question.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
            while((readLine = bufferedReader.readLine())!=null)
            {
                question = readLine;
                Log.d("Store-question","Question read:"+ question);
            }
            TextView textQuestion = (TextView) v.findViewById(R.id.loadQuestion);
            textQuestion.setText(question);
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        Button submitButton = (Button) v.findViewById(R.id.submitAnswer);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText answerField = (EditText)getActivity().findViewById(R.id.answer);
                String answer = answerField.getText().toString();
                if(TextUtils.isEmpty(answer))
                {
                    answerField.setError("Please write answer first");
                    return;
                }
                else
                {
                    new GetAnswer().execute();
                }
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    class GetAnswer extends AsyncTask<Void, Void, String>
    {
        String website;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            website = "http://discoloured-pops.000webhostapp.com/GetAnswer.php";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Store-question", "Answer:" +s);
            EditText answerField = (EditText)getActivity().findViewById(R.id.answer);
            String customAnswer = answerField.getText().toString();
            if(customAnswer.equals(s))
            {
                new UpdateScore().execute();
                Toast.makeText(getActivity(), "Congratulation you are one step closer to the award.",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), "Wrong Answer, try again next time",Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            try
            {
                URL url = new URL(website);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "", answer = "";
                while((line = bufferedReader.readLine())!=null)
                {
                    answer = line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return answer;
            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            }catch(IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    class UpdateScore extends AsyncTask<Void, Void, String>
    {
        String website, deviceId;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            website = "http://discoloured-pops.000webhostapp.com/UpdateScore.php";
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            deviceId = bluetoothAdapter.getAddress();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Store-question",s);
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



}
