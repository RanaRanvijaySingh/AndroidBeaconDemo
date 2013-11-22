AndroidBeaconDemo
=================
This application shows how to use the android phone as a monitor for the beacon devices.
<br/>For this we are using the Radius Networks API.
<br/>
<br/>This application monitors the distance between the beacon and the monitoring device(your application).
<br/>
<br/>First you have to get the APIs that will be needed in this application.
<br/>You can download it from this link : 
<br/><br/>http://developer.radiusnetworks.com/ibeacon/android/download.html
<br/>
<br/>After downloading 
<br/>
These are the steps that you need to follow :<br/>
Step 1: Give the permission in the manifest file and declare few services.<br/>
Step 2: Create a simple layout.<br/>
Step 3: Write the main class.<br/>
____________________________________________________________________________________________________________________________________________
Step 1: Give the permission in the manifest file and declare few services.<br/><br/>
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.webonise.beacondemo"
    android:versionCode="1"
    android:versionName="1.0" >

    <uses-sdk
        android:minSdkVersion="18"
        android:targetSdkVersion="19" />

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme" >
        <activity
            android:name="com.webonise.beacondemo.MainActivity"
            android:label="@string/app_name" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name="com.radiusnetworks.ibeacon.service.IBeaconService"
            android:enabled="true"
            android:exported="true"
            android:isolatedProcess="false"
            android:label="iBeacon" >
        </service>
        <service
            android:name="com.radiusnetworks.ibeacon.IBeaconIntentProcessor"
            android:enabled="true" >
            <meta-data
                android:name="background"
                android:value="true" />

            <intent-filter android:priority="1" >
                <action android:name="com.radiusnetworks.ibeaconreference.DID_RANGING" />
                <action android:name="com.radiusnetworks.ibeaconreference.DID_MONITORING" />
            </intent-filter>
        </service>
    </application>

</manifest>


____________________________________________________________________________________________________________________________________________
Step 2: Create a simple layout.<br/><br/>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity" >

    <TextView
        android:id="@+id/textViewMessage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:text="@string/hello_world"
        android:textSize="20sp" />

</LinearLayout>


____________________________________________________________________________________________________________________________________________
Step 3: Write the main class.<br/><br/>
package com.webonise.beacondemo;

import java.util.Collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.TextView;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

public class MainActivity extends Activity implements IBeaconConsumer {

	private IBeaconManager iBeaconManager = IBeaconManager
			.getInstanceForApplication(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (isBluetooth())
			iBeaconManager.bind(this);
	}

	private boolean isBluetooth() {
		try {
			if (!IBeaconManager.getInstanceForApplication(this)
					.checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						this);
				builder.setTitle("Bluetooth is disabled");
				builder.setMessage("Please enable bluetooth and restart application");
				builder.setPositiveButton("OK", null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
						System.exit(0);
					}
				});
				builder.show();
			} else
				return true;
		} catch (Exception e) {
			if (!IBeaconManager.getInstanceForApplication(this)
					.checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						this);
				builder.setTitle("Bluetooth LE not avaliable");
				builder.setMessage("Sorry, this device does not support Bluetooth LE");
				builder.setPositiveButton("OK", null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
						System.exit(0);
					}
				});
				builder.show();
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		iBeaconManager.unBind(this);
	}

	@Override
	public void onIBeaconServiceConnect() {
		iBeaconManager.setRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons,
					Region region) {
				if (iBeacons.size() > 0) {
					logToDisplay("Beacon is "
							+ iBeacons.iterator().next().getAccuracy()
							+ " meters away.");
				}
			}
		});
		try {
			iBeaconManager.startRangingBeaconsInRegion(new Region(
					"myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {
		}
	}

	private void logToDisplay(final String line) {
		runOnUiThread(new Runnable() {
			public void run() {
				TextView textViewMessage = (TextView) findViewById(R.id.textViewMessage);
				textViewMessage.setText(line);
			}
		});
	}
}



