package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUitl;
import com.coolweather.app.util.Utility;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
public static final int LEVEL_PROVINCE=0;
public static final int LEVEL_CITY=1;
public static final int LEVEL_COUNTY=2;

private Boolean isfromweatheractivity;

private ProgressDialog progressDialog;
private TextView titletext;
private ListView listview;
private ArrayAdapter<String> adapter;
private CoolWeatherDB coolweatherdb;
private List<String> datalist = new ArrayList<String>();

private List<Province> provincelist;//ʡ�б�
private List<City> citylist;//�����б�
private List<County> countylist;//���б�
private Province selectprovince;//ѡ��ʡ
private City selectcity;//ѡ�г���
//private County selectcounty;//ѡ����
private int currentlevel ;
@Override
protected void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
	isfromweatheractivity=getIntent().getBooleanExtra("from_weather_activity", false);
	if(prefs.getBoolean("city_select", false)&&!isfromweatheractivity ){//��ѡ����У����Ҳ��Ǵ�WeatherActivity��ת���Ż���ת��WeatherActivity
		Intent intent = new Intent(this,WeatherActivity.class);
		startActivity(intent);
		finish();
		return;
	}
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.choose_area);
	listview =(ListView)findViewById(R.id.list_view);
	titletext=(TextView)findViewById(R.id.title_text);
	adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
	listview.setAdapter(adapter);
	coolweatherdb=CoolWeatherDB.getInstance(this);
	listview.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(currentlevel==LEVEL_PROVINCE){
				selectprovince=provincelist.get(position);
				queryCity();
			}
			else if (currentlevel==LEVEL_CITY){
				selectcity=citylist.get(position);
			    queryCounty();
			}
			else if (currentlevel==LEVEL_COUNTY){
				String countyname=countylist.get(position).getCounty_name();
				Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
				 if(countyname.equals("�ϳ�")||countyname.equals("�³�")||countyname.equals("����")||countyname.equals("����")||countyname.equals("����")||countyname.equals("����"))
				  {
					 countyname = "����";
				  }
				 Log.d("chooseAreaActivity", countyname);
				intent.putExtra("county_name", countyname);
				startActivity(intent);
				finish();
			}
		}
		
		
	});
	queryProvince();//����ʡ������
	
	
}
//��ѯ����ʡ�����ȴ����ݿ��ѯ��û�оʹӷ�������ѯ
private void queryProvince(){
	provincelist=coolweatherdb.loadProvince();
	if(provincelist.size()>0){
		datalist.clear();
	
	for(Province province :provincelist){
		datalist.add(province.getProvince_name());//��ʡ����Ϣ���ص�LIstview
	}
	adapter.notifyDataSetChanged();//ˢ��Adapter
	listview.setSelection(0);//��λ����һ��λ��
	titletext.setText("�й�");
	currentlevel=LEVEL_PROVINCE;
	}
	
	else {
		queryFromServer(null,"province");
	}
}
//��ѯѡ��ʡ�����е��У��ȴ����ݿ⣬��û��������ݿ�
private void queryCity( ){
	citylist=coolweatherdb.loadCity(selectprovince.getProvince_id());

	if(citylist.size()>0){
		datalist.clear();
		for(City city:citylist){
			datalist.add(city.getCity_name());
		}
		adapter.notifyDataSetChanged();
		listview.setSelection(0);
		titletext.setText(selectprovince.getProvince_name());
		currentlevel=LEVEL_CITY;
		
	}
	else {
		queryFromServer(selectprovince.getProvince_code(),"city");
		
	}
	
	
}
//��ѯѡ�е��أ����ȴ����ݿ⣬û����ӷ�����
private void queryCounty(){
	countylist=coolweatherdb.loadCounty(Integer.parseInt(selectcity.getCity_code()));

	Log.d( "queryCounty cityid", selectcity.getCity_code());
	if(countylist.size()>0){
		datalist.clear();
		for(County county:countylist){
			datalist.add(county.getCounty_name());
		}
		adapter.notifyDataSetChanged();//ˢ��
		listview.setSelection(0);//���ý���λ��
		titletext.setText(selectcity.getCity_name());
		currentlevel=LEVEL_COUNTY;
	}
	else{
		queryFromServer(selectcity.getCity_code()+"","county");
	}
	
}
//���ݴ�����ź����ʹӷ������ϲ�ѯ��������
public void queryFromServer(final String code,final String type){
	String address;
	if(!TextUtils.isEmpty(code)){
		address = "http://apis.map.qq.com/ws/district/v1/getchildren?key=6X3BZ-XULCW-C7ZRN-RKQJ2-HJ5WT-MEBZX&id="+code;
	}
	else {
		address = "http://apis.map.qq.com/ws/district/v1/getchildren?key=6X3BZ-XULCW-C7ZRN-RKQJ2-HJ5WT-MEBZX";
		Log.d("ChooseAreaActivity", "get address");
	}
	showProgressDialog();
	HttpUitl.senHttpRequest(address, new HttpCallBackListener(){
		@Override
		public void onFinish(String response) {
			// TODO Auto-generated method stub
			boolean result=false;
			if("province".equals(type)){
				result =Utility.handleProvinceResponse(coolweatherdb, response);
	//			Log.d("onfinish", "province"+result);
			}
			else if ("city".equals(type)){
				result=Utility.handleCityResponse(coolweatherdb, response, selectprovince.getProvince_id());
			}
			else if ("county".equals(type)){
				result=Utility.handleCountyResponse(coolweatherdb, response, Integer.parseInt(selectcity.getCity_code()));
				Log.d("onFinish county", "result"+result);
			}
			if(result){
				//ͨ��runOnUiThread�����ص����̴߳����߼�
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
				//		Log.d("ChooseAreaActivity", "in ui thread");
		                
						closeProgressDialog();
						Log.d("run", "close pregress");
						
						if("province".equals(type)){
							queryProvince();
						}
						else if ("city".equals(type)){
							queryCity();
						}
						else if ("county".equals(type)){
							queryCounty();
						}
						
					}
					
				});
			}
		
		}

		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			//ͨ��runonuithread�ص����߳�
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					closeProgressDialog();
					Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
							Toast.LENGTH_SHORT).show();
				}
				
			});
		}
		
	});
}
//��ʾ������
public void showProgressDialog(){
	if(progressDialog==null){
		progressDialog=new ProgressDialog(this);
		progressDialog.setMessage("���ڼ���....");
		progressDialog.setCanceledOnTouchOutside(false);
	}
	progressDialog.show();
}

//�رս�����
public void closeProgressDialog(){
	if(progressDialog!=null){
		progressDialog.dismiss();
	}
}
//����Back���������ݵ�ǰ�����жϣ���ʱ�����У��أ�ʡ�б���ֱ���˳�
@Override
public void onBackPressed(){
/*
	if(currentlevel==LEVEL_PROVINCE){
		finish();
	}
	
	else if (currentlevel==LEVEL_CITY){
		queryProvince();
	}
	else if (currentlevel==LEVEL_COUNTY){
		queryCity();
	}
	*/
	if(currentlevel==LEVEL_COUNTY){
		queryCity();
	}
	else {
		if(isfromweatheractivity){
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
		}
		finish();
	}
		
}

 
}

