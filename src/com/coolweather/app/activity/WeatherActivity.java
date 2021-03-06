package com.coolweather.app.activity;



import in.srain.cube.mints.base.MintsBaseActivity;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.R.drawable;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUitl;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends MintsBaseActivity implements OnClickListener {

	
private LinearLayout weatherinfolayout;
private TextView citynametext;//城市名
private TextView publishtext;//发布时间
private TextView weatherdesptext ;//天气描述
private TextView temp1text;//温度1
private TextView temp2text;//温度2
private TextView currentdate;//当前日期
private  Button switchcity;//切换城市按钮
private Button refreshweather ;//更新天气
private Button getlocation;//获取地址
private Button pullfresh; //下拉刷新
private TextView suggest ;//建议
private ImageView weatherimg;
private PtrClassicFrameLayout ptrFrame;
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
	weatherimg = (ImageView)findViewById(R.id.weather_img);
	switchcity =(Button)findViewById(R.id.switch_city);
	refreshweather=(Button)findViewById(R.id.refresh_weather);
	getlocation=(Button)findViewById(R.id.getlocation);
	pullfresh=(Button)findViewById(R.id.pullrefresh);
	switchcity.setOnClickListener(this);
	refreshweather.setOnClickListener(this);
	getlocation.setOnClickListener(this);
	pullfresh.setOnClickListener(this);
	pullfresh.setVisibility(View.GONE);
	refreshweather.setVisibility(View.GONE);
//	suggest = (TextView)findViewById(R.id.suggest_view);
	final String countyname=getIntent().getStringExtra("county_name");
	 
	//使用pulltofresh
	ptrFrame =(PtrClassicFrameLayout)findViewById(R.id.fragment_rotate_header_with_view_group_frame);
	ptrFrame.setLastUpdateTimeRelateObject(this);
    ptrFrame.setPtrHandler(new PtrDefaultHandler() {
        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
            frame.postDelayed(new Runnable() {
                @Override
                public void run() {
                	
                    ptrFrame.refreshComplete();
                    
                    if(!TextUtils.isEmpty(countyname)){
                		//有名称就去查询天气
                		publishtext.setText("同步中");
                		weatherinfolayout.setVisibility(View.INVISIBLE);
                		citynametext.setVisibility(View.INVISIBLE);
                		queryWeathername(countyname);
                	}
                	else{
                		//否则直接显示本地天气
                		showWeather();
                		
                	}
                	
                }
            }, 1500);
        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
            return true;
        }
    });
    ptrFrame.setVisibility(View.VISIBLE);
    
    
    /*
     * 关闭重进程序后用于加载已有天气数据
     */
	if(!TextUtils.isEmpty(countyname)){
		//有名称就去查询天气
		publishtext.setText("同步中");
		weatherinfolayout.setVisibility(View.INVISIBLE);
		citynametext.setVisibility(View.INVISIBLE);
		queryWeathername(countyname);
	}
	else{
		//否则直接显示本地天气
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
		publishtext.setText("同步中");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		/*
		String weathercode=prefs.getString("weather_code", "");
		if(!TextUtils.isEmpty(weathercode)){
			queryWeatherInfo(weathercode);
		}
		*/
		String countyname=prefs.getString("cityname", "");
		if(!TextUtils.isEmpty(countyname)){
			 if(countyname.equals("上城")||countyname.equals("下城")||countyname.equals("江干")||countyname.equals("拱墅")||countyname.equals("西湖")||countyname.equals("滨江"))
			  {
				 countyname = "杭州";
			  }
			queryWeathername(countyname);
		}
		
		break;
	case R.id.getlocation:
		Intent i = new Intent(WeatherActivity.this,GetLocationActivity.class);
		startActivity(i);
	//	finish();
		break;

	   default:
		   break;
	}
}
//查询县名称对应的天气代号
public void queryWeathername(String countyname){
	
		 if(countyname.equals("上城")||countyname.equals("下城")||countyname.equals("江干")||countyname.equals("拱墅")||countyname.equals("西湖")||countyname.equals("滨江"))
		  {
			 countyname = "杭州";
		  }
		String address;
		try {
			address = "https://api.heweather.com/x3/weather?key=8e7c3a0b82b54b00b2a6d536de0e64ef&city="+URLEncoder.encode(new String ( countyname.toString().getBytes("UTF-8") ), "UTF-8");
			queryFromServer(address,"countyname");
			Log.d("queryweather", countyname);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
}
public void showWeather(){
	SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
 if(prefs!=null) {
	String cityname =prefs.getString("cityname", "");
	 if(cityname=="上城"||cityname=="下城"||cityname=="江干"||cityname=="拱墅"||cityname=="西湖"||cityname=="滨江")
	  {
		 cityname = "杭州";
	  }
	 else if (cityname.equals("海曙")||cityname.equals("江东")||cityname.equals("江北")){
		 cityname = "宁波";
	 }
	citynametext.setText(cityname);
	temp1text.setText(prefs.getString("min", "")+"℃");
	temp2text.setText(prefs.getString("max", "")+"℃");
	weatherdesptext.setText(prefs.getString("txt_d", ""));
	publishtext.setText(prefs.getString("loc", "")+"发布");
	currentdate.setText(prefs.getString("current_date", ""));
//	suggest.setText(prefs.getString("suggest", ""));
	weatherinfolayout.setVisibility(View.VISIBLE);
	citynametext.setVisibility(View.VISIBLE);
	
	//动态加载drawable中图片
	String code_n = prefs.getString("code_d", "");
	Class<drawable> cls = R.drawable.class;
    try {
        Integer value = cls.getDeclaredField("w"+code_n).getInt(null);
           Log.v("value",value.toString());
           weatherimg.setImageResource(value);
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

 }
	Intent i = new Intent (this,AutoUpdateService.class);
	startService(i);
}
//获取网络图片
/*
public Bitmap getHttpBitmap(String url){
	 URL myFileURL;  
     Bitmap bitmap=null;  
     try{  
         myFileURL = new URL(url);  
         //获得连接  
         HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();  
         //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制  
         conn.setConnectTimeout(6000);  
         //连接设置获得数据流  
         conn.setDoInput(true);  
         //不使用缓存  
         conn.setUseCaches(false);  
         //这句可有可无，没有影响  
         //conn.connect();  
         //得到数据流  
         InputStream is = conn.getInputStream();  
         //解析得到图片  
         bitmap = BitmapFactory.decodeStream(is);  
         //关闭数据流  
         is.close();  
     }catch(Exception e){  
         e.printStackTrace();  
     }  
       
     return bitmap;  
       
 }  
*/

//查询天气代号对应的天气
public void queryWeatherInfo(String weathercode){
	String address="https://api.heweather.com/x3/condition?search=allcond&key=8e7c3a0b82b54b00b2a6d536de0e64ef";
	queryFromServer(address,"weathercode");
	
}
//根据传入地址和类型去向服务器查询天气代号或天气信息
public void queryFromServer(String address,final String type){
	HttpUitl.senHttpRequest(address, new HttpCallBackListener(){

		@Override
		public void onFinish(String response) {
			// TODO Auto-generated method stub
			if("countyname".equals(type)){
				if(!TextUtils.isEmpty(response)){
					//从服务器返回数据中解析出天气代号
				//	Log.d("WeatherActivity ", "queryFromServer  - onfinish-countyname");
					Utility.handleWeatherReponse(WeatherActivity.this, response);//解析response并保存本地
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
						
						
					});
				}
				else if ("weathercode".equals(type)){
					//处理服务器返回的天气信息
					
					Utility.handleWeatherReponse(WeatherActivity.this, response);//解析response并保存本地
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
			publishtext.setText("同步失败");
		}
		
	});
}


}
