package com.example.rajpa.silentapplication;

import android.bluetooth.BluetoothDevice;

/**
 * Created by rajpa on 31/10/2017.
 */


/*This is a class created to store the details of bluetooth devices and to get their name and mac address stored
* when the devices around were read using bluetooth discovering.*/

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
