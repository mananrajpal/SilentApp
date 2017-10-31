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
        bluetoothDevices = deviceList;

    }
    static class Holder
    {
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
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.customrow, parent, false);
            holder = new Holder();
            holder.deviceName = (TextView)listItem.findViewById(R.id.onCallDevices);
            holder.constImage=(ImageView)listItem.findViewById(R.id.circleImage);
            listItem.setTag(holder);
        }
        else
        {
            holder = (Holder)listItem.getTag();
        }
        BluetoothDevices device = bluetoothDevices.get(position);
        holder.deviceName.setText(device.getName());
        Log.d("Bluetooth-tracking",device.getName());
        holder.constImage.setImageResource(R.drawable.circle);
        return listItem;
    }
}
