package com.example.rajpa.silentapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.Image;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rajpa on 31/10/2017.
 */

public class DevicesListAdapter extends ArrayAdapter<BluetoothDevices>
{
    List<BluetoothDevices> bluetoothDevices;
    public DevicesListAdapter(@NonNull Context context, List<BluetoothDevices> deviceList) {
        super(context,0, deviceList);
        bluetoothDevices = deviceList; //takes in the deviceList passed from async task in ViewDevices class

    }
    static class Holder
    {
        //using a holder class which takes does not does the find by id operations making the views much more recyclable
        //and makes the code faster.
        TextView deviceName;
        ImageView constImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        Holder holder;
        if(listItem == null)
        {
            //this condition checks if there is no spare view to populate the list item and creates a new one
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.customrow, parent, false);
            holder = new Holder();
            holder.deviceName = (TextView)listItem.findViewById(R.id.onCallDevices);
            holder.constImage=(ImageView)listItem.findViewById(R.id.circleImage);
            listItem.setTag(holder);
        }
        else
        {
            //else it uses the associated list item i.e. the non used on to populate the list item.
            holder = (Holder)listItem.getTag();
        }
        //takes in the current bluetooth device in the list and updates on the view.
        BluetoothDevices device = bluetoothDevices.get(position);
        holder.deviceName.setText(device.getName());
        Log.d("Bluetooth-tracking",device.getName());
        holder.constImage.setImageResource(R.drawable.circle);
        return listItem;
    }
}
