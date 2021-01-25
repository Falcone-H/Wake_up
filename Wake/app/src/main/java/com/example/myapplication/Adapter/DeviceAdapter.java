package com.example.myapplication.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.Activity.BluetoothConnect;
import com.example.myapplication.R;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    Context context;
    List<BluetoothDevice> devices;
    LayoutInflater inflater;

    public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup viewGroup;

        if (convertView != null) {
            viewGroup = (ViewGroup) convertView;
        } else {
            viewGroup = (ViewGroup) inflater.inflate(R.layout.device_item, null);
        }

        BluetoothDevice bluetoothDevice = devices.get(position);
        final TextView address_textview = viewGroup.findViewById(R.id.address_textview);
        final TextView name_textview = viewGroup.findViewById(R.id.name_textview);
        final TextView rssi_textview = viewGroup.findViewById(R.id.rssi_textview);

        rssi_textview.setVisibility(View.VISIBLE);

        byte rssival = (byte) BluetoothConnect.deviceRssiValues.get(bluetoothDevice.getAddress()).intValue();
        if (rssival != 0) {
            rssi_textview.setText("Rssi = " + String.valueOf(rssival));

            name_textview.setText(bluetoothDevice.getName());
            address_textview.setText(bluetoothDevice.getAddress());
        }

        if(name_textview.getText().equals("")) {
            name_textview.setText("未知设备");
        }
        return viewGroup;
    }
}
