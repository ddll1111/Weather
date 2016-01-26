package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
//解析和处理服务器返回的省级信息
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolweatherdb,String response){
	 if(!TextUtils.isEmpty(response)){
	 try {
		JSONObject jsonobject=new JSONObject(response);
		JSONArray resultarray = jsonobject.getJSONArray("result");//获取result节点下的位置信息
		 Log.d("Utility", "resultarray.length()"+resultarray.length());
		 JSONArray jarray = resultarray.getJSONArray(0);
		 Log.d("Utility", "jarray.length()"+jarray.length());
		if(jarray.length()>0){
			for(int i =0;i<jarray.length();i++){
				JSONObject subobject = (JSONObject)jarray.get(i);
				Province province =new Province();
				province.setProvince_name(subobject.getString("name"));
				 Log.d("Utility", "PROVINCE NAME"+subobject.getString("name"));
				province.setProvince_code(subobject.getString("id"));
				 Log.d("Utility", "PROVINCE id"+subobject.getString("id"));
				coolweatherdb.saveProvince(province);
				 Log.d("Utility", "PROVINCE OK");
			}
			return true;
		}
	
		
		
		
	   } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	 }
		 return false;
		
		
	}
	//解析和处理服务器返回的市级信息
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolweatherdb,String response,int provinceid){
		if(response!=null){
			try {
				JSONObject jsonobject = new JSONObject(response);
				JSONArray jsonarray=jsonobject.getJSONArray("result");//获取result节点下的位置信息
				 JSONArray jarray = jsonarray.getJSONArray(0);
				if(jarray.length()>0){
					for(int i =0;i<jarray.length();i++){
						JSONObject subobject = jarray.getJSONObject(i);
						City city = new City();
						city.setCity_name(subobject.getString("name"));
						city.setCity_code(subobject.getString("id"));
						city.setProvince_id(provinceid);
						coolweatherdb.saveCity(city);
					}
					return true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
		
	}
	//解析和处理服务器返回的县级信息
	public synchronized static boolean handleCountyResponse(CoolWeatherDB  coolweatherdb,String response,int citycode){
		if(response!=null){
			try {
				JSONObject jsonobject = new JSONObject(response);
				JSONArray jsonarray = jsonobject.getJSONArray("result");
				 JSONArray jarray = jsonarray.getJSONArray(0);
				 Log.d("Utility county", "jarray.length()"+jarray.length());
				if(jsonarray.length()>0){
					for(int i =0;i<jarray.length();i++){
						JSONObject subobject = jarray.getJSONObject(i);
						County county = new County();
						String cname = subobject.getString("fullname");
						if(cname.endsWith("市")||cname.endsWith("县")||cname.endsWith("区")){ 
							cname=cname.substring(0, cname.length()-1);//对县名称做处理，用户查询天气数据时匹配
						}
						county.setCounty_name(cname);
						county.setCounty_code(subobject.getString("id"));
						county.setCity_id(citycode);
						coolweatherdb.saveCounty(county);
					
					}
					return true;
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return false;
	}
/*
 * 解析服务器返回Json，并将解析出的数据存储到本地
 */
	
	public static void handleWeatherReponse(Context context,String response){
		try{
			Log.d("Utility-handleweatherreponse", "ok");
			JSONObject jsonobject = new JSONObject(response);
		  JSONArray jsonarray = jsonobject.getJSONArray("HeWeather data service 3.0");
		  JSONObject subobject = jsonarray.getJSONObject(0);//基础数据
		  JSONObject basic = subobject.getJSONObject("basic");
		  String cityname=basic.getString("city");//城市 ok
		  Log.d("Utility-handleweatherreponse",cityname);
		  JSONObject update = basic.getJSONObject("update");
		  String loc =update.getString("loc");//更新时间 ok
		  Log.d("Utility-handleweatherreponse",loc);
		  JSONArray daily_forecast = subobject.getJSONArray("daily_forecast");
		  JSONObject day1 = daily_forecast.getJSONObject(0);
		  JSONObject cond =day1.getJSONObject("cond");
		  Log.d("Utility-handleweatherreponse",day1+"");
		  String txt_d =cond.getString("txt_d"); //天气信息1
		  Log.d("Utility-handleweatherreponse",txt_d);
		  String txt_n= cond.getString("txt_n");//天气信息2
		  String code_d =cond.getString("code_d");//天气代码
		  JSONObject tmp =day1.getJSONObject("tmp");
		  String max =tmp.getString("max");//最高气温
		  String min =tmp.getString("min");//最低气温
		  JSONObject suggestion = subobject.getJSONObject("suggestion");
		  JSONObject comf = suggestion.getJSONObject("comf");
		  String suggest = comf.getString("txt") ;//建议
		  saveWeatherInfo(context,cityname,txt_d,txt_n,code_d,max,min,loc,suggest);
			
		}
		catch(Exception e){e.printStackTrace();}
		
	}
	
	/*
	 * 将服务器返回的所有天气信息存储到SharedPreferences
	 */
	public static void saveWeatherInfo(Context context,String cityname,
			String txt_d,String txt_n,String code_d,String max,String min,String loc,String suggest){
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		   SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();				   
		   editor.putBoolean("city_select", true);
		   editor.putString("cityname", cityname);
		   editor.putString("txt_d", txt_d);
		   editor.putString("txt_n", txt_n);
		   editor.putString("code_d", code_d);
		   editor.putString("max", max);
		   editor.putString("min", min);
		   editor.putString("loc", loc);
		   editor.putString("current_date", sdf.format(new Date()));
		   editor.putString("suggest", suggest);
		   editor.commit();
				   }
/*
 * 解析处理服务器返回的天气代码
 * */
	public static void handleWeatherInfoResponse(Context context,String response){
		
	}
 
	
}
