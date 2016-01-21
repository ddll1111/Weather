package com.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CollWeatherOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
//���ݿ���
	public static final String DB_NAME="cool_weather";
//���ݿ�汾
	public static final int VERSION=1;
private static CoolWeatherDB coolweatherdb;
private SQLiteDatabase db;

//���췽��˽�л�
private CoolWeatherDB(Context context){
	CollWeatherOpenHelper dbhelper = new CollWeatherOpenHelper(context,DB_NAME,null,VERSION);
	db =dbhelper.getWritableDatabase();
}
//��ȡCoolWeatherDB,ʹ��ͬ����������֤Ψһcoolweatherdb
public synchronized static CoolWeatherDB getInstance(Context context){
	if(coolweatherdb ==null){
		coolweatherdb=new CoolWeatherDB(context);
	}
		return coolweatherdb;
}
//��Provinceʵ�����浽���ݿ�
	public void saveProvince(Province province){
		if(province!=null){
			String in = "insert into province(province_name,province_code) values(" 
				+province.getProvince_name() +"," + province.getProvince_code() + ")";
			db.execSQL(in);
		}
	}
//�����ݿ��ȡȫ������ʡ����Ϣ
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor =db.rawQuery("SELECT * FROM PROVINCE ", null);
		if(cursor.moveToFirst()){
		do{	
			Province province = new Province();
			province.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
			province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
			province.setProvince_code(cursor.getString(cursor.getColumnIndex("province_code")));
		   list.add(province);
		}
		while(cursor.moveToNext());
	}
		return list;
	}
//��cityʵ�����浽���ݿ�
	public void saveCity(City city){
		if(city!=null){
			String sql = "insert into city(city_name,city_code,province_id) values("
					+ city.getCity_name() +","+city.getCity_code() + "," + city.getProvince_id() +")";
			db.execSQL(sql);
		}
		
	}
//�����ݿ���ĳʡ�����г���
	public List<City> loadCity(int province_id){
		List<City> list = new ArrayList<City>();
	
		Cursor cursor = db.rawQuery("select * from city where province_Id =?", new String[] {province_id+""});
		if(cursor.moveToFirst()){
		do{
			City city = new City();
		    city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
		    city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
		    city.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
		    list.add(city);
		}
		while(cursor.moveToNext());
		}
		return list;
	}
//��Countyʵ�����浽���ݿ�
	public void saveCounty(County county){
		if(county!=null){
			String sql = "insert into county(county_name,county_code,city_id) values("
					+county.getCounty_name() +"," + county.getCounty_code() + "," + county.getCity_id() + ")";
			db.execSQL(sql);
		}
	}
//��ȡ�������������б�
	public List<County> loadCounty(int city_id){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.rawQuery("select * from county where city_id=?",new String[] {city_id+""});
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setCounty_name(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCounty_code(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			}
			while(cursor.moveToNext());
		}
		return list;
		
	}
}
