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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewDevices extends AppCompatActivity{
    Integer REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevices> listBluetoothDevices = new ArrayList<BluetoothDevices>();
    DevicesListAdapter adapter;

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
        /*----------------------------------------------------------------------------------------*/

        //setting the logo of the application on the action bar
        myBar.setLogo(R.drawable.silent);
        createBluetooth();//checks if bluetooth is turned on



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
        Boolean isActivityStarted;
        Boolean isActivityFinsihed;
        Integer devicesFound;
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Searching for Devices",Toast.LENGTH_LONG).show();
            Log.d("Bluetooth-tracking","Inside onPreExecute");
            isActivityStarted = false;
            isActivityFinsihed= false;
            pb.setMax(3);
            devicesFound = 0;
            if(mBluetoothAdapter.isDiscovering())
            {
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
            ListView myList = (ListView)findViewById(R.id.devicesList);
            DevicesListAdapter adapter = new DevicesListAdapter(getApplicationContext(), bluetoothDevices);
            myList.setAdapter(adapter);
            super.onPostExecute(bluetoothDevices);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pb.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected List<BluetoothDevices> doInBackground(Void... params) {
            Log.d("Bluetooth-tracking","Inside background override before starting discovery");
            mBluetoothAdapter.startDiscovery();
            Log.d("Bluetooth-tracking","Status of validation"+isActivityFinsihed);
            while(isActivityFinsihed == false)
            {
                if(isActivityStarted)
                {
                    publishProgress(listBluetoothDevices.size());
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
}
