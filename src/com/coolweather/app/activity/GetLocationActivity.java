package com.coolweather.app.activity;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.coolweather.app.R;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
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
	locationtext=(TextView)findViewById(R.id.location_textview);
	
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
	//	Log.d("Weatheractivity ", "Latitude:"+location.getLatitude()+"Longitude:"+location.getLongitude());
	}

	locationManager.requestLocationUpdates(provider, 5000, 1, locationlistener);
	
	
}
LocationListener locationlistener = new LocationListener(){

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	if(location !=null) {
		getLocation();
	}

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
	try{
		StringBuilder url= new StringBuilder();
		
        url.append("http://apis.map.qq.com/ws/geocoder/v1/?key=6X3BZ-XULCW-C7ZRN-RKQJ2-HJ5WT-MEBZX&get_poi=1&location="+location.getLatitude()+","+location.getLongitude());
  //      Toast.makeText(MainActivity.this, url.toString(), Toast.LENGTH_SHORT).show();
    	locationtext.setText(location.getLatitude()+","+location.getLongitude());
    	Log.d("address", url.toString());
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget= new HttpGet(url.toString());
     //   httpget.addHeader("Accept-Language","zh-CN");
        HttpResponse response = httpclient.execute(httpget);
        if(response.getStatusLine().getStatusCode()==200){
        	HttpEntity entity=response.getEntity();
        	String responses = EntityUtils.toString(entity, "utf-8");
        	JSONObject jsonobject  = new JSONObject(responses);
        	Log.d("GetLocationActivity", responses);
        	JSONObject result=jsonobject.getJSONObject("result");
        	JSONObject formatted_addresses=result.getJSONObject("formatted_addresses");
        	String recommend =formatted_addresses.getString("recommend");
        	JSONObject address_component =result.getJSONObject("address_component");
        	String nation = address_component.getString("nation");
        	String province = address_component.getString("province");
        	String city = address_component.getString("city");
        	String district = address_component.getString("district");
        	String street = address_component.getString("street");
        	locationtext.setText(recommend);
        	
        }
		
	}
	
	catch(Exception e)
	{e.printStackTrace();
	}
	
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
