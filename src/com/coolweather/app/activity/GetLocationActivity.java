package com.coolweather.app.activity;

import java.util.List;

import com.coolweather.app.R;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



public class GetLocationActivity extends  Activity{
	private LocationManager locationManager;
	private String provider;
	private TextView locationtext;
	private Location location;
@Override
public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	setContentView(R.layout.getlocation_layout);
	
	locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
	List<String> providerList =locationManager.getProviders(true);
	if(providerList.contains(LocationManager.GPS_PROVIDER)){
		provider=LocationManager.GPS_PROVIDER;
	}
	else if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
		provider =LocationManager.NETWORK_PROVIDER;
	}
	else{
		Toast.makeText(this, "No location provider to user", Toast.LENGTH_SHORT).show();
		return;
	}
	 location =locationManager.getLastKnownLocation(provider);
	if(location!=null){
		getLocation();
		Log.d("Weatheractivity ", "Latitude:"+location.getLatitude()+"Longitude:"+location.getLongitude());
	}
	locationManager.requestLocationUpdates(provider, 5000, 1, locationlistener);
}
LocationListener locationlistener = new LocationListener(){

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		getLocation();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	
};


//获得本地地址
public void getLocation(){
	locationtext.setText("Latitude:"+location.getLatitude()+"Longitude:"+location.getLongitude());
	
}
@Override 
protected void onDestroy(){
	super.onDestroy();
	if(locationManager!=null){
		//关闭程序时将监听器移除
		locationManager.removeUpdates(locationlistener);
	}
}

}
