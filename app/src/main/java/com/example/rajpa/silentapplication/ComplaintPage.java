package com.example.rajpa.silentapplication;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

/*Fragment which is displayed when a user selects a device shows as the result of scanning devices.*/
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

    //public method that gets the list of bluetooth devices on call and the position in the list shown to user.
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
        Log.d("Fragment-error","Inside the onCreateView method of fragment");
        View v= inflater.inflate(R.layout.fragment_complaint_page, container, false);
        TextView nudgeDevice = (TextView) v.findViewById(R.id.nudgeDevice);
        TextView reportDevice = (TextView) v.findViewById(R.id.reportDevice);

        nudgeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on click listerner that starts an async task that updates the nudge column and
                //sets it 0 indicating that device has been nudged.
                NudgeDevice nudgeDevice = new NudgeDevice();
                nudgeDevice.execute();
            }
        });
        reportDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Fragment-error","Inside the onClick Listener");
                /*On Click listener that starts an async tasks that increments the Complaints column
                * in database indicating a user has complained about this device.*/
                ReportWorker reportWorker = new ReportWorker(v);
                reportWorker.execute();
            }
        });
        return v;
    }

    //Override method that attaches this fragment to main activity calling this fragment.
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


    /*This async task connects to the database using the httpurl connection and passes the deviceid i.e.
    * the bluetooth device address to the php file that compares it with the table and updates the
     * nudge column to 1 for that deviceId which indicates that the user has nudged that device.*/
    class NudgeDevice extends AsyncTask<Void, Void, String>
    {
        private ProgressBar progressBar;
        String website = "http://discoloured-pops.000webhostapp.com/NudgeDevice.php";
        @Override
        protected void onPreExecute() {
            Log.d("Nudge-checking","Entered the PreExecute");
            //associates the progress bar from the activity xml.
            //changes it to visible state indicating process is going in background for the user.
            progressBar = (ProgressBar) getActivity().findViewById(R.id.nudgeBar);
            progressBar.setVisibility(View.VISIBLE);
            Log.d("Nudge-checking","Complete the PreExecute");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Makes the progress bar dissapear indicating that the process is completed.
            Log.d("Nudge-checking","Started PostExecute the PreExecute");
            progressBar.setVisibility(View.INVISIBLE);
            //Checks if the database echo Success depending on the request gives the user feedback accordingly.
            if(s.equals("Success"))
            {
                Toast.makeText(getActivity(),"Successfully Nudged",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(),"Something Wrong with application",Toast.LENGTH_LONG).show();
            }
            Log.d("Nudge-checking","Completed PostExecute the PreExecute");

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
                Log.d("Nudge-checking","Started doInBackground the PreExecute");
                URL url = new URL(website); //converts the string to url
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //makes an httpurl connection from url
                httpURLConnection.setDoInput(true); //allows input for that url
                httpURLConnection.setDoOutput(true);//allows output for that url
                OutputStream outputStream  = httpURLConnection.getOutputStream(); //an output stream to write to the server
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                //This data is posted to server using buffer writer i.e. the device id is passed to server.
                String post_data = URLEncoder.encode("deviceId","UTF-8")+"=" +
                        URLEncoder.encode(listOfDevices.get(listPosition).getMacAddress(),"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                outputStream.close();
                bufferedWriter.close();
                //Input stream to get the echo from the server.
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line="", result="";
                while((line = bufferedReader.readLine())!=null)
                {
                    result += line;
                }
                inputStream.close();
                bufferedReader.close();
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


    /*This async task passes the deviceId to the server to increment the complaint column in table for that device.*/
    class ReportWorker extends AsyncTask<Void,Void,String>
    {
        View view;
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.reportBar);
        String website = "http://discoloured-pops.000webhostapp.com/ReportDevice.php";
        public ReportWorker(View v)
        {
            this.view = v;
        }
        @Override
        protected void onPreExecute() {
            Log.d("Fragment-error","Inside the preExecute Method of async");
            progressBar.setVisibility(View.VISIBLE); //shows the progress bar indicating process going in background to user.
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Fragment-error","Inside the onPostExectue method");
            //hides the progress bar indicating the process is finished.
            progressBar.setVisibility(View.INVISIBLE);
            //Depending upon the echo given from server feedback is given to the user.
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
                Log.d("Fragment-error","Inside doInBackground Method");
                URL url = new URL(website); //creates a url for the website.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //url opens an httpurlconnection
                httpURLConnection.setDoInput(true);//sets to allow input for that httpconnection
                httpURLConnection.setDoOutput(true);//sets to allow output for that http connection
                OutputStream outputStream = httpURLConnection.getOutputStream(); //output stream to write to the server
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                //data encoded using url encoder which will be writter to the server using buffer writer in the UTF-8 format
                String post_data = URLEncoder.encode("deviceId","UTF-8")+"=" +
                                    URLEncoder.encode(listOfDevices.get(listPosition).getMacAddress(),"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //Input stream to get the echo from the server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line="", result="";
                while((line = bufferedReader.readLine())!=null)
                {
                    Log.d("Checking-reporting", line);
                    result += line;
                }
                return result;

            }catch (MalformedURLException e)
            {

            }catch (IOException e)
            {

            }
            return null;
        }
    }


}
