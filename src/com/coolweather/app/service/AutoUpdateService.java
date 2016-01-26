package com.coolweather.app.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUitl;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
@Override
public int onStartCommand(Intent intent,int flags,int startId){
	new Thread(new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateWeather();
		}
		
	}).start();
	AlarmManager manager =(AlarmManager)getSystemService(ALARM_SERVICE);
	int anhour = 8*60*60*1000;//8–° ±∫¡√Î ˝
	long triggerattime = SystemClock.elapsedRealtime()+anhour;
	Intent i = new Intent(this,AutoUpdateReceiver.class);
	PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
	manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerattime, pi);
	
	
	return super.onStartCommand(intent, flags, startId);
}
public void updateWeather(){
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	String countyname = prefs.getString("cityname", "");
	String address;
	try {
		address = "https://api.heweather.com/x3/weather?key=8e7c3a0b82b54b00b2a6d536de0e64ef&city="+URLEncoder.encode(new String ( countyname.toString().getBytes("UTF-8") ), "UTF-8");
		 HttpUitl.senHttpRequest(address, new HttpCallBackListener(){

				@Override
				public void onFinish(String response) {
					// TODO Auto-generated method stub
					Utility.handleWeatherReponse(AutoUpdateService.this, response);
				}

				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					e.printStackTrace();
				}
		    	
		    });
	
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
   
	
}
}
