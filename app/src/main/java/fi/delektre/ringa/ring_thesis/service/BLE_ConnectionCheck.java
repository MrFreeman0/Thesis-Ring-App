package fi.delektre.ringa.ring_thesis.service;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BLE_ConnectionCheck extends BroadcastReceiver {
    Context activityContext;
    int duration = Toast.LENGTH_SHORT;
    public BLE_ConnectionCheck(Context activityContext){
        this.activityContext = activityContext;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_DISCONNECTED:
                    Toast toast1 = Toast.makeText(activityContext, "Device Disconnected!", duration);
                    toast1.show();
                    break;
                case BluetoothAdapter.STATE_CONNECTED:
                    Toast toast2 = Toast.makeText(activityContext, "Device Connected!", duration);
                    toast2.show();
                    break;
                }
            }
        }
    }

