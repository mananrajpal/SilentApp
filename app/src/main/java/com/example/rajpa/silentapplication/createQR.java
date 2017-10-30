package com.example.rajpa.silentapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
    String androidId;
    ImageView qrHolder;
    String phoneState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initializeUI();
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
        id = info.getMacAddress();
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

}
