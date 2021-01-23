package com.example.myapplication.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.Adapter.DeviceAdapter;
import com.example.myapplication.R;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluetoothConnect extends Activity {

    private BluetoothAdapter bluetoothAdapter;

    private static final long SCAN_PERION = 10000;  //扫描周期：10秒
    private Handler handler;
    private boolean isScanning = false;

    boolean device_found = false;
    private String Address = "80:EA:CA:03:02:01";
    public static Map<String, Integer> deviceRssiValues;
    public List<BluetoothDevice> deviceList;
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        handler = new Handler();

        Log.d("Connect", "on create");

        // 检测设备是否支持BLE
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth Low Energy not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备是否支持蓝牙
        if(bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth Low Energy not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateList();

        Button cancel_button = findViewById(R.id.cancel_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isScanning == false)
                    scanLeDevice(true);
                else
                    finish();
            }
        });
    }

    private void scanLeDevice(final boolean enable) {
        Log.d("Connect", "on scan");
        if(enable) {
            // 扫描十秒后停止
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    if(!device_found){
                        Toast.makeText(BluetoothConnect.this, "Device not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }, SCAN_PERION);

            isScanning = true;
            Log.d("Connect","Scanning");
            bluetoothAdapter.startLeScan(leScanCallback);

        } else {
            isScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private void populateList() {
        deviceList = new ArrayList<BluetoothDevice>();
        deviceAdapter = new DeviceAdapter(this, deviceList);
        deviceRssiValues = new HashMap<String, Integer>();

        ListView newDevicesListView = findViewById(R.id.device_listview);
        newDevicesListView.setAdapter(deviceAdapter);
        newDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = deviceList.get(position);
                bluetoothAdapter.stopLeScan(leScanCallback);

                Bundle bundle = new Bundle();
                bundle.putString(BluetoothDevice.EXTRA_DEVICE, deviceList.get(position).getAddress());

                Intent result = new Intent();
                result.putExtras(bundle);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        scanLeDevice(true);

    }

    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    Log.d("Connect", "Call back");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addDevice(device, rssi);
                                }
                            });
                        }
                    });
                }
            };

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean device_found = false;

        for(BluetoothDevice device_item : deviceList) {
            if(device_item.getAddress().equals(device.getAddress())) {
                device_found = true;
                break;
            }
        }

        deviceRssiValues.put(device.getAddress(), rssi);

        if(!device_found) {
            deviceList.add(device);
            deviceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

}
