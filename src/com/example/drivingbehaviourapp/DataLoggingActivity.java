package com.example.drivingbehaviourapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class DataLoggingActivity extends ActionBarActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener,
		SensorEventListener {

	private LocationManager locationManager;
	DBHelper gpsDb = new DBHelper(this);
	ImageView start;
	TextView title;
	Button overspeed;
	Button braking;
	Button lane;
	Button acceln;
	static Boolean brakFlag = false;
	static Boolean laneFlag = false;
	//Button logData;
	Button aggTurn;
	Button otherEvent;
	static Boolean speedFlag = false;
	static Boolean accelFlag = false;
	static Boolean turnFlag = false;
	static String remarksField = "None";
	static float accelX;
	static float accelY;
	static float accelZ;
	static float pressureVal;
	Timer timer = new Timer();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_log_screen);
		gpsDb.deletDB();
		start = (ImageView) findViewById(R.id.startStop);
		title = (TextView) findViewById(R.id.latLon);
		overspeed = (Button) findViewById(R.id.overspeed);
		//logData = (Button) findViewById(R.id.submit);
		braking = (Button) findViewById(R.id.brake);
		lane = (Button) findViewById(R.id.lane);
		acceln = (Button) findViewById(R.id.accleration);
		aggTurn = (Button) findViewById(R.id.turn);
		otherEvent = (Button) findViewById(R.id.other);

		int delay = 1000; // delay for 1 sec.
		int period = 1000; // repeat every 10 sec.

		Toast.makeText(getApplicationContext(), "Logging has started.......",
				Toast.LENGTH_LONG).show();

		overspeed.setOnClickListener(this);
		//logData.setOnClickListener(this);
		braking.setOnClickListener(this);
		lane.setOnClickListener(this);
		acceln.setOnClickListener(this);
		aggTurn.setOnClickListener(this);
		otherEvent.setOnClickListener(this);

		SensorManager snsMgr = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		Sensor acc = snsMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor prss = snsMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
		snsMgr.registerListener(this, acc, 1);
		snsMgr.registerListener(this, prss, 1);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10, 10, new LocationListener() {

					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderEnabled(String arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLocationChanged(Location arg0) {
						// TODO Auto-generated method stub

						Location location2 = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						String lat;
						String Lon;
						if (location2 != null) {

							start.setImageResource(R.drawable.greebutton);
							lat = String.valueOf(location2.getLatitude());
							Lon = String.valueOf(location2.getLongitude());

						} else {
							lat = "No val";
							Lon = "No val";

						}

						title.setText("Latitude : " + lat + "\n Longitude :"
								+ Lon);

					}
				});

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {

				StoreHouse tempStore = new StoreHouse();
				Date mydate = new Date();
				tempStore.setTimeStmnp(mydate.toString());
				tempStore.setAcX(String.valueOf(accelX));
				tempStore.setAcY(String.valueOf(accelY));
				tempStore.setAcZ(String.valueOf(accelZ));
				tempStore.setPressure(String.valueOf(pressureVal));

				Location location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					tempStore.setLat(String.valueOf(location.getLatitude()));
					tempStore.setLon(String.valueOf(location.getLongitude()));
					tempStore.setBarr(location.getBearing());
					tempStore.setGpsSpeed(String.valueOf(location.getSpeed()));

				}

				if (speedFlag == true) {
					tempStore.setSpeed("Over");
				}
				if (brakFlag == true) {
					tempStore.setBraking("Yes");
				}
				if (laneFlag == true) {
					tempStore.setLane("Change");
				}
				if (accelFlag == true) {
					tempStore.setAccel("Yes");
				}
				if (turnFlag == true) {
					tempStore.setTurn("Yes");
				}
				if (!remarksField.equals("None")) {
					tempStore.setRemark(remarksField);
				}
				gpsDb.insertGps(tempStore);
				remarksField = "None";

			}
		}, delay, period);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		if (R.id.overspeed == arg0.getId()) {

			speedFlag = true;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.seekbarlayout,
					(ViewGroup) findViewById(R.id.Rel1));
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setView(layout);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// Toast.makeText(getApplicationContext(),
							// "You clicked on OK", Toast.LENGTH_SHORT)
							// .show();
							speedFlag = false;
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBar1);
			sb.setMax(100);
			sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// Do something here with new value
					remarksField = String.valueOf(seekBar.getProgress());
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

					Toast.makeText(getApplicationContext(),
							"Rated :" + arg0.getProgress(), Toast.LENGTH_SHORT)
							.show();
					remarksField = String.valueOf(arg0.getProgress());

				}
			});

		}

//		if (R.id.submit == arg0.getId()) {
//
//			Log.i("Stop Logging", "Stopped Logging");
//			timer.cancel();
//			Button coloChange = (Button) arg0;
//			// Toast.makeText(getApplicationContext(), "Writting to file",
//			// Toast.LENGTH_LONG).show();
//			coloChange.setBackgroundColor(Color.GREEN);
//			writeToCSV();
//			Toast.makeText(getApplicationContext(), "Data Logged",
//					Toast.LENGTH_LONG).show();
//			coloChange.setBackgroundColor(Color.RED);
//			finish();
//
//		}

		if (R.id.brake == arg0.getId()) {

			brakFlag = true;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.seekbarlayout,
					(ViewGroup) findViewById(R.id.Rel1));
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setView(layout);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// Toast.makeText(getApplicationContext(),
							// "You clicked on OK", Toast.LENGTH_SHORT)
							// .show();
							brakFlag = false;
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBar1);
			// sb.setLabelFor(0);
			sb.setMax(100);
			// sb.setLabelFor(100);
			sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// Do something here with new value
					remarksField = String.valueOf(seekBar.getProgress());
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

					Toast.makeText(getApplicationContext(),
							"Rated :" + arg0.getProgress(), Toast.LENGTH_SHORT)
							.show();

					remarksField = String.valueOf(arg0.getProgress());
				}
			});

		}

		if (R.id.lane == arg0.getId()) {

			laneFlag = true;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.seekbarlayout,
					(ViewGroup) findViewById(R.id.Rel1));
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setView(layout);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// Toast.makeText(getApplicationContext(),
							// "You clicked on OK", Toast.LENGTH_SHORT)
							// .show();
							laneFlag = false;
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBar1);
			sb.setMax(100);
			sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// Do something here with new value
					remarksField = String.valueOf(seekBar.getProgress());
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

					Toast.makeText(getApplicationContext(),
							"Rated :" + arg0.getProgress(), Toast.LENGTH_SHORT)
							.show();
					remarksField = String.valueOf(arg0.getProgress());

				}
			});

		}

		if (R.id.accleration == arg0.getId()) {

			accelFlag = true;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.seekbarlayout,
					(ViewGroup) findViewById(R.id.Rel1));
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setView(layout);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// Toast.makeText(getApplicationContext(),
							// "You clicked on OK", Toast.LENGTH_SHORT)
							// .show();
							accelFlag = false;
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBar1);
			sb.setMax(100);
			sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// Do something here with new value
					remarksField = String.valueOf(seekBar.getProgress());
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

					Toast.makeText(getApplicationContext(),
							"Rated :" + arg0.getProgress(), Toast.LENGTH_SHORT)
							.show();

					remarksField = String.valueOf(arg0.getProgress());
				}
			});

		}

		if (R.id.turn == arg0.getId()) {

			turnFlag = true;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.seekbarlayout,
					(ViewGroup) findViewById(R.id.Rel1));
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setView(layout);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// Toast.makeText(getApplicationContext(),
							// "You clicked on OK", Toast.LENGTH_SHORT)
							// .show();
							turnFlag = false;
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBar1);
			sb.setMax(100);
			sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// Do something here with new value
					remarksField = String.valueOf(seekBar.getProgress());
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

					Toast.makeText(getApplicationContext(),
							"Rated :" + arg0.getProgress(), Toast.LENGTH_SHORT)
							.show();

					remarksField = String.valueOf(arg0.getProgress());
				}
			});

		}

		if (R.id.other == arg0.getId()) {

			LayoutInflater li = LayoutInflater.from(this);
			View promptsView = li.inflate(R.layout.other_event_layout, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.editTextDialogUserInput);

			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// get user input and set it to result
									// edit text
									remarksField = userInput.getText()
											.toString();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

		}

	}

	void writeToCSV() {

		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/GPSTraces");

		boolean var = false;
		if (!folder.exists())
			var = folder.mkdir();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(
				Calendar.getInstance().getTime());
		final String filename = folder.toString() + "/" + MainActivity.vehical
				+ mydate + "Log.csv";

		try {
			FileWriter fw = new FileWriter(filename);

			DBHelper gpsDb = new DBHelper(this);

			Cursor rs = gpsDb.getData();
			System.out.println(rs.getCount());
			System.out.println(rs);
			// rs.moveToFirst();
			if (rs.moveToFirst()) {
				do {
					String lat = rs.getString(0);
					String lon = rs.getString(1);
					String barr = rs.getString(2);
					String time = rs.getString(3);
					String brake = rs.getString(4);
					String speed = rs.getString(5);
					String lane = rs.getString(6);
					String gpsSpeed = rs.getString(7);
					String acc = rs.getString(8);
					// String acc = rs.getString(9);
					String tn = rs.getString(9);
					String rm = rs.getString(10);
					String x = rs.getString(11);
					String y = rs.getString(12);
					String z = rs.getString(13);
					String pp = rs.getString(14);

					fw.append(lat);
					fw.append(',');
					fw.append(lon);
					fw.append(',');
					fw.append(barr);
					fw.append(',');
					fw.append(time);
					fw.append(',');
					fw.append(brake);
					fw.append(',');
					fw.append(speed);
					fw.append(',');
					fw.append(lane);
					fw.append(',');
					fw.append(gpsSpeed);
					fw.append(',');
					fw.append(acc);
					fw.append(',');
					fw.append(tn);
					fw.append(',');
					fw.append(rm);
					fw.append(',');
					fw.append(x);
					fw.append(',');
					fw.append(y);
					fw.append(',');
					fw.append(z);
					fw.append(',');
					fw.append(pp);
					fw.append("\n");
				} while (rs.moveToNext());
			}

			// fw.close();
			// }
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub

		Sensor sensor = arg0.sensor;
		float[] values = arg0.values;

		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			accelX = values[0];
			accelY = values[1];
			accelZ = values[2];
		}
		
		if (sensor.getType() == Sensor.TYPE_PRESSURE) {

			pressureVal = values[0];
			
		}


	}

	@Override
	public void onBackPressed() {

		Log.i("Stop Logging", "Stopped Logging");
		timer.cancel();
		writeToCSV();
		Toast.makeText(getApplicationContext(), "Data Logged",
				Toast.LENGTH_LONG).show();
		finish();

	}

}
