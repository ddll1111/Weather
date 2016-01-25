package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUitl {

	public static void senHttpRequest(final String address,final HttpCallBackListener listener){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try{
					URL url = new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");//设置请求方式
					connection.setConnectTimeout(8000);//设置连接超时
					connection.setReadTimeout(8000);//设置访问超时
					
					InputStream in =connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					if(listener!=null){
						listener.onFinish(response.toString());
					}
					
				}
				catch(Exception e){
					if(listener!=null){
					listener.onError(e);
				}
					}
				finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
			
			
		}).start();
	}
}
