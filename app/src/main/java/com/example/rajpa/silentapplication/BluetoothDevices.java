package com.example.rajpa.silentapplication;

import android.bluetooth.BluetoothDevice;

/**
 * Created by rajpa on 31/10/2017.
 */

public class BluetoothDevices
{
    private String name, macAddress;

    public BluetoothDevices(String name, String macAddress)
    {
        this.name = name;
        this.macAddress = macAddress;
    }
    public String getName()
    {
        return name;
    }
    public String getMacAddress()
    {
        return macAddress;
    }
}
