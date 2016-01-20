package com.example.drivingbehaviourapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	static Spinner spinner1;
	private Button btnSubmit;
	Button sendData;
	static String vehical;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_screen);
		btnSubmit = (Button) findViewById(R.id.submitButton);
		btnSubmit.setOnClickListener(this);

		sendData = (Button) findViewById(R.id.sendData);
		sendData.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		Spinner spn = (Spinner) findViewById(R.id.spinner1);

		if (arg0.getId() == R.id.submitButton) {

			if (spn.getSelectedItem().equals("Choose the type of ride")) {

				Toast.makeText(getApplicationContext(),
						"Please enter the type of ride", Toast.LENGTH_LONG)
						.show();

			} else {

				vehical = spn.getSelectedItem().toString();
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				if (!locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					Toast.makeText(this, "GPS is not Enabled in your devide",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent dataLogActivity = new Intent(this,
							DataLoggingActivity.class);
					startActivity(dataLogActivity);
				}

			}

		}

		if (arg0.getId() == R.id.sendData) {

			final ArrayList<String> filesSelected = new ArrayList<String>();
			CharSequence[] items = null;
			final String folderName = Environment.getExternalStorageDirectory()
					+ "/GPSTraces";
			File file = new File(folderName);
			final File fileList[] = file.listFiles();
			Log.d("Files", "Size: " + fileList.length);

			final ArrayList<String> temp = new ArrayList<String>();
			for (int i = 0; i < fileList.length; i++) {
				Log.d("Files", "FileName:" + fileList[i].getName());
				temp.add(fileList[i].getName().toString());

			}

			items = temp.toArray(new CharSequence[temp.size()]);
			// arraylist to keep the selected items
			final ArrayList seletedItems = new ArrayList();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select files to Email ");
			builder.setMultiChoiceItems(items, null,
					new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int indexSelected, boolean isChecked) {
							if (isChecked) {
								// If the user checked the item, add it to the
								// selected items
								seletedItems.add(indexSelected);
							} else if (seletedItems.contains(indexSelected)) {
								// Else, if the item is already in the array,
								// remove it
								seletedItems.remove(Integer
										.valueOf(indexSelected));
							}
						}
					})
					// Set the action buttons
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// Your code when user clicked on OK
									// You can write the code to save the
									// selected item here

									filesSelected.addAll(seletedItems);
									Uri u1 = null;
									Intent sendIntent = new Intent(
											Intent.ACTION_SEND_MULTIPLE);
									sendIntent.putExtra(Intent.EXTRA_SUBJECT,
											"Logged Data CSV");

									Log.d("Files", "Size: " + fileList.length);
									ArrayList<Uri> uris = new ArrayList<Uri>();

									Iterator iterator = seletedItems.iterator();
									while (iterator.hasNext()) {

										String name = temp
												.get((Integer) iterator.next());
										System.out.println(name);

										File fl = new File(folderName + "/"
												+ name);
										Log.i("Email------>>>>", fl.toString());
										u1 = Uri.fromFile(fl);
										uris.add(u1);
									}

									if(!uris.isEmpty()){
									sendIntent.putParcelableArrayListExtra(
											Intent.EXTRA_STREAM, uris);
									sendIntent.setType("text/html");
									
									startActivity(sendIntent);
									}

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {

								}
							});

			AlertDialog dialog = builder.create();// AlertDialog dialog; create
													// like this outside onClick
			dialog.show();

		}

	}
}
