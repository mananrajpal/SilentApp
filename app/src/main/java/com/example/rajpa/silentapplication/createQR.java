package com.example.rajpa.silentapplication;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class createQR extends AppCompatActivity {
    static String androidId;
    BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    ImageView qrHolder;
    String phoneState;
    static Boolean createdOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initializeUI();
    }

    public String getDeviceId()
    {
        return androidId;
    }
    public Boolean getSignal()
    {
        return createdOnce;
    }

    private void initializeUI()
    {
        /*------------------------------------------------------------------------------------------*/
        //setting up objects with respective to their xml views.
        ActionBar myActionBar = getSupportActionBar();
        TextView idHolder = (TextView)findViewById(R.id.deviceId);
        /*----------------------------------------------------------------------------------------*/
        myActionBar.setLogo(R.drawable.silent); //setting up the logo on ActionBar
        androidId = getAndroidId();
        idHolder.setText(androidId);
        createQR();
        registerDevice();

        Log.d("Id-Testing", androidId);
    }

    private String getAndroidId()
    {
        String id;
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        //id = info.getMacAddress();
        id = mAdapter.getAddress();
        //Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    private void createQR()
    {
        //using an external library named zxing that takes in an string
        //string is converted to Bitmap of Barcode
        //that bitmap is set to an imageView
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try
        {
            BitMatrix bitMatrix = multiFormatWriter.encode(androidId, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrHolder = (ImageView)findViewById(R.id.qrImage);
            qrHolder.setImageBitmap(bitmap);
        }catch(WriterException e)
        {
            e.printStackTrace();
        }
    }

    private void registerDevice()
    {
        TrackPhoneState tr = new TrackPhoneState();
        phoneState = tr.getPhoneSate();
        /*-------------Getting current date and time-----*/
        Calendar cl = Calendar.getInstance();
        Log.d("Testing-date","Without format:"+cl.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String date = dateFormat.format(cl.getTime());
        String time = cl.get(cl.HOUR_OF_DAY)+":"+cl.get(cl.MINUTE);
        /*------------------------------------------------*/
        RegisterThread rT = new RegisterThread();
        rT.execute(androidId,phoneState, date, time);
        createdOnce = true;

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

    class RegisterThread extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Insert-checking",s );
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            //Afer the device is registered it starts checking for the nudges
            CheckNudge  checkNudge = new CheckNudge();
            StartAsyncTaskInParallel(checkNudge);
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            String deviceId = params[0];
            String deviceState = params[1];
            String date = params[2];
            String time = params[3];
            String registerUrl = "http://discoloured-pops.000webhostapp.com/register.php";
            Log.d("Testing-date",deviceId+" "+deviceState+" "+date+ " "+time);
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
                                    +URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(deviceState,"UTF-8")+"&"
                                    +URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(date,"UTF-8")+"&"
                                    +URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8");
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
                Log.d("Checking-connection",result);
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
                            URLEncoder.encode(mAdapter.getAddress(),"UTF-8");
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
                        URLEncoder.encode(mAdapter.getAddress(),"UTF-8");
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
