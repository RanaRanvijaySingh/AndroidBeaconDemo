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

/**
 * @author Ranvijay This class demonstrate how to monitor the beacons present
 *         around you.
 */
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

	/**
	 * @return true if the mobile has the bluetooth and that is BTLE. false if
	 *         the mobile does not have the BTLE . It also checks if the mobile
	 *         has the bluetooth enabled if not then gives a dialog box to
	 *         enable the bluetooth.
	 */
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

	/**
	 * @param line
	 *            String value that is passed to the text view to be displayed
	 *            to the user. This function starts a new UI thread , so that
	 *            the main thread is not disturbed.
	 */
	private void logToDisplay(final String line) {
		runOnUiThread(new Runnable() {
			public void run() {
				TextView textViewMessage = (TextView) findViewById(R.id.textViewMessage);
				textViewMessage.setText(line);
			}
		});
	}
}
