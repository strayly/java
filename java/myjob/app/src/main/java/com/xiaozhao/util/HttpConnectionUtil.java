package com.xiaozhao.util;

import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Map;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
//import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;


//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import android.os.Handler;
import android.util.Log;

public class HttpConnectionUtil
{
	//private static int TIME_OUT_DELAY = 30000;
	public static final String TAG = "HTTP_INFO";
	private int statusCode = -1;

	public int getStatusCode() {
		return statusCode;
	}

	public int checkStatus(String requestUrl) {
		int result = -1;
		try {
			// 新建一个URL对象
			URL url = new URL(requestUrl);
			// 打开一个HttpURLConnection连接
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// 设置连接主机超时时间
			urlConn.setConnectTimeout(500);
			// 开始连接
			urlConn.connect();
			// 判断请求是否成功
			result = urlConn.getResponseCode();
			// 关闭连接
			urlConn.disconnect();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return result;
	}
	public String requestGetJson(String baseUrl,Map<String, String> paramsMap) {
		String result = "";
		try {
			//String baseUrl = "https://xxx.com/getUsers?";
			StringBuilder tempParams = new StringBuilder();
			int pos = 0;
			for (String key : paramsMap.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				if(paramsMap.get(key)!=null)tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key),"utf-8")));
				pos++;
			}
			String requestUrl = baseUrl +"?"+ tempParams.toString();
			Log.e(TAG, "URL:" + requestUrl);
			// 新建一个URL对象
			URL url = new URL(requestUrl);
			// 打开一个HttpURLConnection连接
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// 设置连接主机超时时间
			urlConn.setConnectTimeout(2 * 1000);
			//设置从主机读取数据超时
			urlConn.setReadTimeout(2 * 1000);
			// 设置是否使用缓存  默认是true
			urlConn.setUseCaches(true);
			// 设置为Post请求
			urlConn.setRequestMethod("GET");
			//urlConn设置请求头信息
			//设置请求中的媒体类型信息。
			urlConn.setRequestProperty("Content-Type", "application/json");
			//设置客户端与服务连接类型
			//Log.e(TAG, "URL status:aaa");
			urlConn.addRequestProperty("Connection", "Keep-Alive");
			// 开始连接
			urlConn.connect();
			// 判断请求是否成功
			//Log.e(TAG, "URL status:" + urlConn.getResponseCode());
			this.statusCode = urlConn.getResponseCode();
			if (this.statusCode == 200) {
				// 获取返回的数据
				result = streamToString(urlConn.getInputStream());
				//Log.e(TAG, "Get方式请求成功，result--->" + result);
				//System.out.println("aaaa:"+result);
			} else {
				Log.e(TAG, "Get方式请求失败");
				//System.out.println("xx:");
			}
			// 关闭连接
			urlConn.disconnect();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return result;
	}
	public String requestGet(String baseUrl,Map<String, String> paramsMap) {
		String result = "";
		try {
			//String baseUrl = "https://xxx.com/getUsers?";
			StringBuilder tempParams = new StringBuilder();
			int pos = 0;
			String requestUrl = baseUrl;
			if (paramsMap != null){
				for (String key : paramsMap.keySet()) {
					if (pos > 0) {
						tempParams.append("&");
					}
					tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
					pos++;
				}
				requestUrl = requestUrl+"?"+ tempParams.toString();
			}

			Log.e(TAG, "URL:" + requestUrl);
			// 新建一个URL对象
			URL url = new URL(requestUrl);
			// 打开一个HttpURLConnection连接
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// 设置连接主机超时时间
			urlConn.setConnectTimeout(2 * 1000);
			//设置从主机读取数据超时
			urlConn.setReadTimeout(2 * 1000);
			// 设置是否使用缓存  默认是true
			urlConn.setUseCaches(true);
			// 设置为Post请求
			urlConn.setRequestMethod("GET");
			//urlConn设置请求头信息
			//设置请求中的媒体类型信息。
			//urlConn.setRequestProperty("Content-Type", "application/json");
			//设置客户端与服务连接类型
			//Log.e(TAG, "URL status:aaa");
			urlConn.addRequestProperty("Connection", "Keep-Alive");
			// 开始连接
			urlConn.connect();
			// 判断请求是否成功
			//Log.e(TAG, "URL status:" + urlConn.getResponseCode());
			this.statusCode = urlConn.getResponseCode();
			if (this.statusCode == 200) {
				// 获取返回的数据
				result = streamToString(urlConn.getInputStream());
				//Log.e(TAG, "Get方式请求成功，result--->" + result);
			} else {
				Log.e(TAG, "Get方式请求失败");
			}
			// 关闭连接
			urlConn.disconnect();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return result;
	}

	public String requestPost(String baseUrl,Map<String, String> getMap,Map<String, String> postMap) {
		String result = "";
		try {
			//String baseUrl = "https://xxx.com/getUsers";
			//get 参数
			StringBuilder tempParams = new StringBuilder();
			int pos = 0;
			for (String key : getMap.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				tempParams.append(String.format("%s=%s", key, URLEncoder.encode(getMap.get(key),"utf-8")));
				pos++;
			}
			String requestUrl = baseUrl +"?"+ tempParams.toString();
			//post 参数
			tempParams = new StringBuilder();
			pos = 0;
			for (String key : postMap.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				tempParams.append(String.format("%s=%s", key,  URLEncoder.encode(postMap.get(key),"utf-8")));
				pos++;
			}

			String params =tempParams.toString();
			// 请求的参数转换为byte数组
			byte[] postData = params.getBytes();
			// 新建一个URL对象
			URL url = new URL(requestUrl);
			Log.e(TAG, "URL:" + requestUrl);
			Log.e(TAG, "postdata:" + params);
			// 打开一个HttpURLConnection连接
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// 设置连接超时时间
			urlConn.setConnectTimeout(2 * 1000);
			//设置从主机读取数据超时
			urlConn.setReadTimeout(2 * 1000);
			// Post请求必须设置允许输出 默认false
			urlConn.setDoOutput(true);
			//设置请求允许输入 默认是true
			urlConn.setDoInput(true);
			// Post请求不能使用缓存
			urlConn.setUseCaches(false);
			// 设置为Post请求
			urlConn.setRequestMethod("POST");
			//设置本次连接是否自动处理重定向
			urlConn.setInstanceFollowRedirects(true);
			// 配置请求Content-Type
			//urlConn.setRequestProperty("Content-Type", "application/json");
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// 开始连接
			urlConn.connect();
			// 发送请求参数
			DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
			dos.write(postData);
			dos.flush();
			dos.close();
			// 判断请求是否成功
			this.statusCode = urlConn.getResponseCode();

			if (this.statusCode == 200) {
				// 获取返回的数据
				result = streamToString(urlConn.getInputStream());
				Log.e(TAG, "Post方式请求成功，result--->" + result);
			} else {
				Log.e(TAG, "Post方式请求失败");
			}
			// 关闭连接
			urlConn.disconnect();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return result;
	}

	/**
	 * 将输入流转换成字符串
	 *
	 * @param is 从网络获取的输入流
	 * @return
	 */
	public String streamToString(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.close();
			is.close();
			byte[] byteArray = baos.toByteArray();
			return new String(byteArray);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return null;
		}
	}

	public void downloadFile(String fileUrl){
		try {
			// 新建一个URL对象
			URL url = new URL(fileUrl);
			// 打开一个HttpURLConnection连接
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// 设置连接主机超时时间
			urlConn.setConnectTimeout(5 * 1000);
			//设置从主机读取数据超时
			urlConn.setReadTimeout(5 * 1000);
			// 设置是否使用缓存  默认是true
			urlConn.setUseCaches(true);
			// 设置为Post请求
			urlConn.setRequestMethod("GET");
			//urlConn设置请求头信息
			//设置请求中的媒体类型信息。
			urlConn.setRequestProperty("Content-Type", "application/json");
			//urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//设置客户端与服务连接类型
			urlConn.addRequestProperty("Connection", "Keep-Alive");
			// 开始连接
			urlConn.connect();
			// 判断请求是否成功
			this.statusCode = urlConn.getResponseCode();
			if (this.statusCode == 200) {
				String filePath="";
				File  descFile = new File(filePath);
				FileOutputStream fos = new FileOutputStream(descFile);;
				byte[] buffer = new byte[1024];
				int len;
				InputStream inputStream = urlConn.getInputStream();
				while ((len = inputStream.read(buffer)) != -1) {
					// 写到本地
					fos.write(buffer, 0, len);
				}
			} else {
				Log.e(TAG, "文件下载失败");
			}
			// 关闭连接
			urlConn.disconnect();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	public void upLoadFile(String filePath, Map<String, String> paramsMap) {
		try {
			String baseUrl = "https://xxx.com/uploadFile";
			File file = new File(filePath);
			//新建url对象
			URL url = new URL(baseUrl);
			//通过HttpURLConnection对象,向网络地址发送请求
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			//设置该连接允许读取
			urlConn.setDoOutput(true);
			//设置该连接允许写入
			urlConn.setDoInput(true);
			//设置不能适用缓存
			urlConn.setUseCaches(false);
			//设置连接超时时间
			urlConn.setConnectTimeout(5 * 1000);   //设置连接超时时间
			//设置读取超时时间
			urlConn.setReadTimeout(5 * 1000);   //读取超时
			//设置连接方法post
			urlConn.setRequestMethod("POST");
			//设置维持长连接
			urlConn.setRequestProperty("connection", "Keep-Alive");
			//设置文件字符集
			urlConn.setRequestProperty("Accept-Charset", "UTF-8");
			//设置文件类型
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "*****");
			String name = file.getName();
			DataOutputStream requestStream = new DataOutputStream(urlConn.getOutputStream());
			requestStream.writeBytes("--" + "*****" + "\r\n");
			//发送文件参数信息
			StringBuilder tempParams = new StringBuilder();
			tempParams.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + name + "\"; ");
			int pos = 0;
			int size=paramsMap.size();
			for (String key : paramsMap.keySet()) {
				tempParams.append( String.format("%s=\"%s\"", key, paramsMap.get(key), "utf-8"));
				if (pos < size-1) {
					tempParams.append("; ");
				}
				pos++;
			}
			tempParams.append("\r\n");
			tempParams.append("Content-Type: application/octet-stream\r\n");
			tempParams.append("\r\n");
			String params = tempParams.toString();
			requestStream.writeBytes(params);
			//发送文件数据
			FileInputStream fileInput = new FileInputStream(file);
			int bytesRead;
			byte[] buffer = new byte[1024];
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			while ((bytesRead = in.read(buffer)) != -1) {
				requestStream.write(buffer, 0, bytesRead);
			}
			requestStream.writeBytes("\r\n");
			requestStream.flush();
			requestStream.writeBytes("--" + "*****" + "--" + "\r\n");
			requestStream.flush();
			fileInput.close();
			this.statusCode = urlConn.getResponseCode();
			if (this.statusCode == 200) {
				// 获取返回的数据
				String result = streamToString(urlConn.getInputStream());
				Log.e(TAG, "上传成功，result--->" + result);
			} else {
				Log.e(TAG, "上传失败");
			}
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}

}
