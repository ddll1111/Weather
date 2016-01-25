package com.coolweather.app.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
						county.setCounty_name(subobject.getString("fullname"));
						 Log.d("Utility county", "County name"+subobject.getString("fullname"));
						county.setCounty_code(subobject.getString("id"));
						Log.d("Utility county", "County  code"+subobject.getString("id"));
						Log.d("Utility county", "city id "+citycode);
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
	
	
}
