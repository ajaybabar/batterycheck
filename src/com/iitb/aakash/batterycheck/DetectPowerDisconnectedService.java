package com.iitb.aakash.batterycheck;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DetectPowerDisconnectedService extends Service {

	private Timer _timer = null;
	private SQLiteAdapter mySQLiteAdapter;
	SimpleCursorAdapter cursorAdapter;
	Cursor cursor;
	Calendar today;
	static int level;
	DetectPowerConnectedService countingid;
	//BroadcastReceiver batteryLevelReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent)

		{
			context.unregisterReceiver(this);

			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
					0);
			level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

			String curTime = String.format("%02d:%02d:%02d",
					today.get(Calendar.HOUR), today.get(Calendar.MINUTE),
					today.get(Calendar.SECOND));
			String curDate = String.format("%02d/%02d/%02d",
					today.get(Calendar.DATE), today.get(Calendar.MONTH)+1,
					today.get(Calendar.YEAR));

			if (plugged == 0) {
		
				int count_id=cursor.getCount();
				/* Toast.makeText(getApplicationContext(),
				 "stored count "+count_id, Toast.LENGTH_SHORT).show();
				*/
				mySQLiteAdapter.update_byID(count_id, curTime, ""
						+ level, curDate);

				
				updateList();
				cursor.close();
				mySQLiteAdapter.close();


			}

		}
	};

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mySQLiteAdapter = new SQLiteAdapter(this);
		countingid = new DetectPowerConnectedService();

	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(this.batteryLevelReceiver);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		today = Calendar.getInstance();
		mySQLiteAdapter.openToWrite();
		cursor = mySQLiteAdapter.queueAll();

		//get_batterpercentage();
		this.registerReceiver(batteryLevelReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		// Show toast notification
	//	showToastNotification();

		// Show status bar notification
	//	showStatusBarNotification();
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateList() {
		cursor.requery();
	}

	// Show status bar notification
	/*private void showStatusBarNotification() {
		_timer = new Timer();
		_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("message_title", "AC power is disconnected");
				bundle.putString("message_text", "Click here to fire intent");
				bundle.putString("ticker_text", "AC power is disconnected");
				
				 * message.obj = bundle; toastHandler.sendMessage(message);
				 

				 Handler required only for toast notifications 

				// Get reference to notification manager
				NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				// Instantiate the notification
				Notification notification = new Notification(
						R.drawable.ic_launcher, // The resource id of the icon
												// to put in the status bar.
						bundle.getString("ticker_text"), // The text that flows
															// by in the status
															// bar when the
															// notification
															// first activates.
						System.currentTimeMillis()); // The time to show in the
														// time field. In the
														// System.currentTimeMillis
														// timebase.

				// Set the expanded message and the intent to fire when the user
				// clicks the expanded message
				Intent notificationIntent = new Intent(getApplicationContext(), // Application
																				// context.
						BatteryCheck.class); // Activity to open.
				PendingIntent contentIntent = PendingIntent.getActivity(
						getApplicationContext(), 0, notificationIntent, 0);
				notification.setLatestEventInfo(getApplicationContext(),
						bundle.getString("message_title"),
						bundle.getString("message_text"), contentIntent);

				// Pass notification to notification manager
				notificationManager.notify(2, // Unique ID of notification
						notification); // Notification
			}
		}, 500);
	}
*/
	// Show toast notification
	/*private void showToastNotification() {

		_timer = new Timer();
		_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("message", "AC power is disconnected");
				message.obj = bundle;
				toastHandler.sendMessage(message);
			}
		}, 5000);

	}*/

	private final Handler toastHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = (Bundle) msg.obj;
			Toast.makeText(getApplicationContext(),
					bundle.getString("message"), Toast.LENGTH_SHORT).show();
		}
	};


}
