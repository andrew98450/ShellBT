package com.agentwaj.shellbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter bluetoothAdapter;
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Obtain access to the Bluetooth radio of the device
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// Close app if Bluetooth is not supported
		if (bluetoothAdapter == null)
			exit("Bluetooth not supported on this device.");
		
		// Request Bluetooth be enabled if it isn't already
		if (!bluetoothAdapter.isEnabled())
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
		
		// Holds the names of found devices
		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		// Called whenever a device is found
		receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					listAdapter.add(device.getName());
				}
			}
		};
		
		// Set adapter of ListView to the list of devices
		((ListView) findViewById(R.id.lvDevices)).setAdapter(listAdapter);
		
		// Filter for when a device is found
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		
		// Begin scanning for devices
		bluetoothAdapter.startDiscovery();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// Register the receiver to be called when a device is found
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		// Unregister receiver when Activity leaves foreground
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT)
			if (resultCode == RESULT_OK) {
				// If Bluetooth is successfully enabled
				registerReceiver(receiver, filter);
				bluetoothAdapter.startDiscovery();
			}
			else
				exit("Enabling Bluetooth failed.");
	}
	
	private void exit(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		finish();
	}
}
