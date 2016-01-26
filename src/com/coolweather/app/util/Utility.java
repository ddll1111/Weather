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
//�����ʹ�����������ص�ʡ����Ϣ
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolweatherdb,String response){
	 if(!TextUtils.isEmpty(response)){
	 try {
		JSONObject jsonobject=new JSONObject(response);
		JSONArray resultarray = jsonobject.getJSONArray("result");//��ȡresult�ڵ��µ�λ����Ϣ
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
	//�����ʹ�����������ص��м���Ϣ
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolweatherdb,String response,int provinceid){
		if(response!=null){
			try {
				JSONObject jsonobject = new JSONObject(response);
				JSONArray jsonarray=jsonobject.getJSONArray("result");//��ȡresult�ڵ��µ�λ����Ϣ
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
	//�����ʹ�����������ص��ؼ���Ϣ
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
						if(cname.endsWith("��")||cname.endsWith("��")||cname.endsWith("��")){ 
							cname=cname.substring(0, cname.length()-1);//���������������û���ѯ��������ʱƥ��
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
 * ��������������Json�����������������ݴ洢������
 */
	
	public static void handleWeatherReponse(Context context,String response){
		try{
			Log.d("Utility-handleweatherreponse", "ok");
			JSONObject jsonobject = new JSONObject(response);
		  JSONArray jsonarray = jsonobject.getJSONArray("HeWeather data service 3.0");
		  JSONObject subobject = jsonarray.getJSONObject(0);//��������
		  JSONObject basic = subobject.getJSONObject("basic");
		  String cityname=basic.getString("city");//���� ok
		  Log.d("Utility-handleweatherreponse",cityname);
		  JSONObject update = basic.getJSONObject("update");
		  String loc =update.getString("loc");//����ʱ�� ok
		  Log.d("Utility-handleweatherreponse",loc);
		  JSONArray daily_forecast = subobject.getJSONArray("daily_forecast");
		  JSONObject day1 = daily_forecast.getJSONObject(0);
		  JSONObject cond =day1.getJSONObject("cond");
		  Log.d("Utility-handleweatherreponse",day1+"");
		  String txt_d =cond.getString("txt_d"); //������Ϣ1
		  Log.d("Utility-handleweatherreponse",txt_d);
		  String txt_n= cond.getString("txt_n");//������Ϣ2
		  String code_d =cond.getString("code_d");//��������
		  JSONObject tmp =day1.getJSONObject("tmp");
		  String max =tmp.getString("max");//�������
		  String min =tmp.getString("min");//�������
		  JSONObject suggestion = subobject.getJSONObject("suggestion");
		  JSONObject comf = suggestion.getJSONObject("comf");
		  String suggest = comf.getString("txt") ;//����
		  saveWeatherInfo(context,cityname,txt_d,txt_n,code_d,max,min,loc,suggest);
			
		}
		catch(Exception e){e.printStackTrace();}
		
	}
	
	/*
	 * �����������ص�����������Ϣ�洢��SharedPreferences
	 */
	public static void saveWeatherInfo(Context context,String cityname,
			String txt_d,String txt_n,String code_d,String max,String min,String loc,String suggest){
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
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
 * ����������������ص���������
 * */
	public static void handleWeatherInfoResponse(Context context,String response){
		
	}
 
	
}
