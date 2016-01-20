package com.example.drivingbehaviourapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "DrivingBehaviourWithPressure";
	public static final String GPSTABLENAME = "gps";
	public static final String GPSLAT = "lat";
	public static final String GPSLON = "lon";
	public static final String BARR = "barometer";
	public static final String TIMESTAMP = "time";
	public static final String BRAKE = "brake";
	public static final String SPEED = "speed";
	public static final String LANE = "lane";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

		// SQLiteDatabase db = this.getWritableDatabase();

		arg0.execSQL("create table gps (lat text , lon text,heading text,time text, gpsspeed text,speed text,brake text,lane text, acceleralion text, turn text,remarks text,acX text,acY text, acZ text, pressure text)");

	}

	public void deletDB() {

		SQLiteDatabase db = this.getWritableDatabase();

		// db.execSQL("delete from gps");
		db.delete("gps", null, null);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		// SQLiteDatabase db = this.getWritableDatabase();
		arg0.execSQL("DROP TABLE IF EXISTS gps");
		onCreate(arg0);

	}

	public boolean insertGps(StoreHouse temp) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		StoreHouse store = temp;
		contentValues.put("lat", store.getLat());
		contentValues.put("lon", store.getLon());
		contentValues.put("heading", store.getBarr());
		contentValues.put("time", store.getTimeStmnp());
		contentValues.put("gpsspeed", store.getGpsSpeed());
		contentValues.put("speed", store.getSpeed());
		contentValues.put("brake", store.getBraking());
		contentValues.put("lane", store.getLane());
		contentValues.put("acceleralion", store.getAccel());
		contentValues.put("turn", store.getTurn());
		contentValues.put("remarks", store.getRemark());
		contentValues.put("acX", store.getAcX());
		contentValues.put("acY", store.getAcY());
		contentValues.put("acZ", store.getAcZ());
		contentValues.put("pressure", store.getPressure());
		String log = store.getLat() + store.getLon() + store.getBarr() + store.getTimeStmnp() + store.getGpsSpeed() + store.getSpeed() + store.getBraking() + store.getLane()+ store.getAccel() + store.getTurn()+ store.getRemark();
		Log.i("Message", log);
		db.insert("gps", null, contentValues);
		return true;
	}

	public Cursor getData() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from gps", null);
		return res;
	}

}
