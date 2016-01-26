package com.coolweather.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUitl;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener {

	
private LinearLayout weatherinfolayout;
private TextView citynametext;//������
private TextView publishtext;//����ʱ��
private TextView weatherdesptext ;//��������
private TextView temp1text;//�¶�1
private TextView temp2text;//�¶�2
private TextView currentdate;//��ǰ����
private  Button switchcity;//�л����а�ť
private Button refreshweather ;//��������
private TextView suggest ;//����
@Override
protected void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.weather_layout);
	weatherinfolayout=(LinearLayout)findViewById(R.id.weather_info_layout);
	citynametext=(TextView)findViewById(R.id.city_name);
	publishtext=(TextView)findViewById(R.id.publish_text);
	weatherdesptext=(TextView)findViewById(R.id.weather_desp);
	temp1text=(TextView)findViewById(R.id.temp1);
	temp2text=(TextView)findViewById(R.id.temp2);
	currentdate=(TextView)findViewById(R.id.current_date);
	switchcity =(Button)findViewById(R.id.switch_city);
	refreshweather=(Button)findViewById(R.id.refresh_weather);
	switchcity.setOnClickListener(this);
	refreshweather.setOnClickListener(this);
	suggest = (TextView)findViewById(R.id.suggest_view);
	String countyname=getIntent().getStringExtra("county_name");
	if(!TextUtils.isEmpty(countyname)){
		//�����ƾ�ȥ��ѯ����
		publishtext.setText("ͬ����");
		weatherinfolayout.setVisibility(View.INVISIBLE);
		citynametext.setVisibility(View.INVISIBLE);
		queryWeathername(countyname);
	}
	else{
		//����ֱ����ʾ��������
		showWeather();
		
	}
	
}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch(v.getId()) {
	case R.id.switch_city:
		Intent intent = new Intent(this,ChooseAreaActivity.class);
		intent.putExtra("from_weather_activity", true);
		startActivity(intent);
		finish();
		break;
	case R.id.refresh_weather:
		publishtext.setText("ͬ����");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		/*
		String weathercode=prefs.getString("weather_code", "");
		if(!TextUtils.isEmpty(weathercode)){
			queryWeatherInfo(weathercode);
		}
		*/
		String countyname=prefs.getString("cityname", "");
		if(!TextUtils.isEmpty(countyname)){
			queryWeathername(countyname);
		}
		
		break;
	   default:
		   break;
	}
}
//��ѯ�����ƶ�Ӧ����������
public void queryWeathername(String countyname){
	try {

		String address = "https://api.heweather.com/x3/weather?key=8e7c3a0b82b54b00b2a6d536de0e64ef&city="+URLEncoder.encode(new String ( countyname.toString().getBytes("UTF-8") ), "UTF-8") ;
		queryFromServer(address,"countyname");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
public void showWeather(){
	SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
	citynametext.setText(prefs.getString("cityname", ""));
	temp1text.setText(prefs.getString("min", "")+"��");
	temp2text.setText(prefs.getString("max", "")+"��");
	weatherdesptext.setText(prefs.getString("txt_d", ""));
	publishtext.setText(prefs.getString("loc", "")+"����");
	currentdate.setText(prefs.getString("current_date", ""));
//	suggest.setText(prefs.getString("suggest", ""));
	weatherinfolayout.setVisibility(View.VISIBLE);
	citynametext.setVisibility(View.VISIBLE);
}
//��ѯ�������Ŷ�Ӧ������
public void queryWeatherInfo(String weathercode){
	String address="https://api.heweather.com/x3/condition?search=allcond&key=8e7c3a0b82b54b00b2a6d536de0e64ef";
	queryFromServer(address,"weathercode");
	
}
//���ݴ����ַ������ȥ���������ѯ�������Ż�������Ϣ
public void queryFromServer(String address,final String type){
	HttpUitl.senHttpRequest(address, new HttpCallBackListener(){

		@Override
		public void onFinish(String response) {
			// TODO Auto-generated method stub
			if("countyname".equals(type)){
				if(!TextUtils.isEmpty(response)){
					//�ӷ��������������н�������������
				//	Log.d("WeatherActivity ", "queryFromServer  - onfinish-countyname");
					Utility.handleWeatherReponse(WeatherActivity.this, response);//����response�����汾��
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
						
						
					});
				}
				else if ("weathercode".equals(type)){
					//������������ص�������Ϣ
					
					Utility.handleWeatherReponse(WeatherActivity.this, response);//����response�����汾��
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
						
						
					});
					
					
				}
			}
		}

		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			publishtext.setText("ͬ��ʧ��");
		}
		
	});
}
}
