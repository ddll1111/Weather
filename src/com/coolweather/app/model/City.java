package com.coolweather.app.model;

public class City {
private int city_id;
private String city_name;
private String city_code;
private int province_id;
public int getProvince_id() {
	return province_id;
}
public void setProvince_id(int province_id) {
	this.province_id = province_id;
}
public int getCity_id() {
	return city_id;
}
public void setCity_id(int city_id) {
	this.city_id = city_id;
}
public String getCity_name() {
	return city_name;
}
public void setCity_name(String city_name) {
	this.city_name = city_name;
}
public String getCity_code() {
	return city_code;
}
public void setCity_code(String city_code) {
	this.city_code = city_code;
}
}
