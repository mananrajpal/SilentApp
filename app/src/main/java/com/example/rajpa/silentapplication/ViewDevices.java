package com.example.rajpa.silentapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewDevices extends AppCompatActivity implements ComplaintPage.complaintListener{
    Integer REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevices> listBluetoothDevices = new ArrayList<BluetoothDevices>();
    List<BluetoothDevices>devicesOnCall = new ArrayList<BluetoothDevices>();

    DevicesListAdapter adapter;

    @Override
    public void getStatus(String s) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_devices);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        initializeUI();
    }

    private void initializeUI()
    {
        /*------------------------Associating objects with their views----------------------------*/
        ActionBar myBar = getSupportActionBar();
        ListView listView = (ListView)findViewById(R.id.devicesList);
        /*----------------------------------------------------------------------------------------*/

        //setting the logo of the application on the action bar
        myBar.setLogo(R.drawable.silent);
        createBluetooth();//checks if bluetooth is turned on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ComplaintPage cl = new ComplaintPage();
                cl.setDevies(devicesOnCall, position);
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.complaintFragment, cl);
                fr.commit();
            }
        });
    }
    private void createBluetooth()
    {

        /*Code reference - https://developer.android.com/guide/topics/connectivity/bluetooth.html
        * Created a bluetooth adapter and checks if it is not enabled creates an intent to turned
        * the bluetooth on.*/
        if(mBluetoothAdapter==null)
        {
            Toast.makeText(getApplicationContext(),"Your mobile does not support bluetooth",Toast.LENGTH_LONG).show();
        }
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent myIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(myIntent, REQUEST_ENABLE_BT);
        }
        if(mBluetoothAdapter.isEnabled())
        {
            ExploreDevices ex = new ExploreDevices();
            ex.execute();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
        {
            ExploreDevices ex = new ExploreDevices();
            ex.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Turn on Bluetooth to scan nearby devices.",Toast.LENGTH_LONG).show();
        }
    }

    class ExploreDevices extends AsyncTask<Void, Integer, List<BluetoothDevices>>
    {
        Boolean isActivityStarted; //boolean value that detects if the scanning started.
        Boolean isActivityFinsihed;//condition to keep the background part keep running until listener reports Scanning finished
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //initiates all the values to default.
            Toast.makeText(getApplicationContext(),"Searching for Devices",Toast.LENGTH_LONG).show();
            Log.d("Bluetooth-tracking","Inside onPreExecute");
            isActivityStarted = false;
            isActivityFinsihed= false;
            if(mBluetoothAdapter.isDiscovering())
            {
                /*This condition checks if the bluetooth device was already in discovery mode,
                * if yes it disables it first which is a good practice and then turns it on again.
                * Three intents are created one to get status if scanning started, second to get
                * status if the scanning is finished and last to check how many devices were found.*/
                Log.d("Bluetooth-tracking","Inside if condition of was Discovering");
                //making sure bluetooth discovery is cancelled before starting to discover again.
                mBluetoothAdapter.cancelDiscovery();
                IntentFilter startAction = new IntentFilter(mBluetoothAdapter.ACTION_DISCOVERY_STARTED);
                IntentFilter finishAction = new IntentFilter(mBluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                IntentFilter deviceFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(actionStarted, startAction);
                registerReceiver(actionFinished, finishAction);
                registerReceiver(deviceFoundAction, deviceFound);
            }
            else
            {
                Log.d("Bluetooth-tracking","Inside the else condition of discovering");
                IntentFilter startAction = new IntentFilter(mBluetoothAdapter.ACTION_DISCOVERY_STARTED);
                IntentFilter finishAction = new IntentFilter(mBluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                IntentFilter deviceFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(actionStarted, startAction);
                registerReceiver(actionFinished, finishAction);
                registerReceiver(deviceFoundAction, deviceFound);
            }
        }

        @Override
        protected void onPostExecute(List<BluetoothDevices> bluetoothDevices) {
            Log.d("Bluetooth-tracking","Inside the Post Execute");
            Log.d("Bluetooth-tracking","Size:"+ bluetoothDevices.size());
            new GetValidatedList(bluetoothDevices).execute();
            super.onPostExecute(bluetoothDevices);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
           // pb.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected List<BluetoothDevices> doInBackground(Void... params) {
            Log.d("Bluetooth-tracking","Inside background override before starting discovery");
            //startDiscovery is an async method that put the device is discoverable mode and looks
            //for other devices to get their name and MAC address.
            mBluetoothAdapter.startDiscovery();
            Log.d("Bluetooth-tracking","Status of validation"+isActivityFinsihed);
            /*startDiscovery async method depends on its listeners to get appropriate status of the
            * scanning process but threads don't considers those callbacks so had to implement a
            * while loop with logic such that it will keep the thread occupied until the listener
            * reports that the scanning is complete i.e. devices list has been updated which then
            * takes it to the post method.*/
            while(isActivityFinsihed == false)
            {
                if(isActivityStarted)
                {
                   // publishProgress(listBluetoothDevices.size());
                }
            }
            if(isActivityFinsihed)
            {
                Log.d("Bluetooth-tracking","Inside the background if activity finished");
                return listBluetoothDevices;
            }
            return null;
        }

        /*----------------Creating the BroadcastReceiver to get the appropriate-------------------*/

        /*This broadcast receiver checks if the scanning is started on device.*/
        private BroadcastReceiver actionStarted = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(mBluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
                {
                    Log.d("Bluetooth-tracking","Search Started in interface");
                    isActivityStarted = true;
                }
            }
        };

        /*This broadcast receiver checks if the scanning is finished on device.*/
        private BroadcastReceiver actionFinished = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(mBluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                    isActivityFinsihed = true;
                    Log.d("Bluetooth-tracking","Search finished in interface");
                }
            }
        };

        /*This broadcast receiver checks if the scanning has found a device.*/
        private BroadcastReceiver deviceFoundAction = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    Log.d("Bluetooth-tracking","Device Found in interface");
                    BluetoothDevice db = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("Bluetooth-tracking",db.getName() +":"+ db.getAddress());
                    listBluetoothDevices.add(new BluetoothDevices(db.getName(),db.getAddress()));
                }
            }
        };

        /*---------------------------------------------------------------------------------------*/
    }




    class GetValidatedList extends AsyncTask<String, Void, String>
    {
        List<BluetoothDevices>myDevices;
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);

        public GetValidatedList(List<BluetoothDevices> br)
        {
            myDevices = br;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ListView myList = (ListView)findViewById(R.id.devicesList);//gets the associated listview to be populated
            pb.setVisibility(View.INVISIBLE);//sets the progress bar to invisible once the list is populated.
            DevicesListAdapter adapter = new DevicesListAdapter(getApplicationContext(), devicesOnCall);
            //sets the adapter to the defined list view.
            myList.setAdapter(adapter);
            Log.d("Bluetooth-tracking", s);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            String website = "http://discoloured-pops.000webhostapp.com/GetDeviceOnCall.php";
            try
            {
                URL url = new URL(website);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String result="", line="";
                while((line = bufferedReader.readLine())!=null)
                {
                    Log.d("Mac-comparison",line);
                    result += line;
                    for(int i=0; i< myDevices.size();i++)
                    {
                        Log.d("Mac-comparison",myDevices.get(i).getMacAddress());
                        if(myDevices.get(i).getMacAddress().equals(line))
                        {
                            Log.d("Bluetooth-tracking","Inside the checking condition");
                            Log.d("Bluetooth-tracking",myDevices.get(i).getName()+myDevices.get(i).getMacAddress());
                            devicesOnCall.add(new BluetoothDevices(myDevices.get(i).getName(),myDevices.get(i).getMacAddress()));
                        }
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            }catch (MalformedURLException e)
            {

            }
            catch(IOException e)
            {

            }

            return null;
        }
    }
}
