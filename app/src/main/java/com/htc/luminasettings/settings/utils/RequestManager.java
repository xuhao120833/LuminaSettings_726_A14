package com.htc.luminasettings.settings.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.htc.luminasettings.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年7月4日 上午9:02:33 类说明
 */
public class RequestManager {

	private static final MediaType MEDIA_TYPE_JSON = MediaType
			.parse("application/json; charset=utf-8");// mdiatype这个需要和服务端保持一致
	private static final String TAG = RequestManager.class.getSimpleName();
	
	//private static String BASE_URL = "http://otaapi.hotackiot.com:8080";// 请求接口根地址
	//private static final String BASE_URL1 = "http://otaapi.hotackiot.com:8080";// 请求接口根地址
	
//	private static String BASE_URL = "http://otaapi.hotackiot.cp59.ott.cibntv.net:8080";// 请求接口根地址
	private static String BASE_URL = "http://ota.triplesai.com:8080";// 请求接口根地址
	private static final String BASE_URL1 = "http://ota.triplesai.com:8080";// 请求接口根地址
//	private static final String BASE_URL1 = "http://otaapi.hotackiot.cp59.ott.cibntv.net:8080";// 请求接口根地址
	
	private static final String BASE_URL2 = "http://otaapi.triplesai.com:8080";// 请求接口根地址
	private static final String BASE_URL3 = "http://139.199.190.220:8080";// 请求接口根地址
	private static volatile RequestManager mInstance;// 单利引用
	public static final int TYPE_GET = 0;// get请求
	public static final int TYPE_POST_JSON = 1;// post请求参数为json
	public static final int TYPE_POST_FORM = 2;// post请求参数为表单
	private OkHttpClient mOkHttpClient;// okHttpClient 实例
	private Handler okHttpHandler;// 全局处理子线程和M主线程通信
	
	private Handler progressHandler;

	/**
	 * 初始化RequestManager
	 */
	public RequestManager(Context context) {
		// 初始化OkHttpClient
		mOkHttpClient = new OkHttpClient().newBuilder()
				.connectTimeout(20, TimeUnit.SECONDS)// 设置超时时间
				.readTimeout(20, TimeUnit.SECONDS)// 设置读取超时时间
				.writeTimeout(20, TimeUnit.SECONDS)// 设置写入超时时间
				.build();
		// 初始化Handler
		okHttpHandler = new Handler(context.getMainLooper());
		progressHandler=new Handler(context.getMainLooper());
	}

	/**
	 * 获取单例引用
	 * 
	 * @return
	 */
	public static RequestManager getInstance(Context context) {
		RequestManager inst = mInstance;
		if (inst == null) {
			synchronized (RequestManager.class) {
				inst = mInstance;
				if (inst == null) {
					inst = new RequestManager(context.getApplicationContext());
					mInstance = inst;
				}
			}
		}
		return inst;
	}

	/**
	 * okHttp同步请求统一入口
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param requestType
	 *            请求类型
	 * @param paramsMap
	 *            请求参数
	 */
	public void requestSyn(String actionUrl, int requestType,
			HashMap<String, String> paramsMap) {
		switch (requestType) {
		case TYPE_GET:
			requestGetBySyn(actionUrl, paramsMap);
			break;
		case TYPE_POST_JSON:
			requestPostBySyn(actionUrl, paramsMap);
			break;
		case TYPE_POST_FORM:
			requestPostBySynWithForm(actionUrl, paramsMap);
			break;
		}
	}

	/**
	 * okHttp get同步请求
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param paramsMap
	 *            请求参数
	 */
	private void requestGetBySyn(String actionUrl,
			HashMap<String, String> paramsMap) {
		StringBuilder tempParams = new StringBuilder();
		try {
			// 处理参数
			int pos = 0;
			for (String key : paramsMap.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				// 对参数进行URLEncoder
				tempParams.append(String.format("%s=%s", key,
						URLEncoder.encode(paramsMap.get(key), "utf-8")));
				pos++;
			}
			// 补全请求地址
			String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl,
					tempParams.toString());
			// 创建一个请求
			Request request = addHeaders().url(requestUrl).build();
			// 创建一个Call
			final Call call = mOkHttpClient.newCall(request);
			// 执行请求
			final Response response = call.execute();
			response.body().string();
		} catch (Exception e) {
			LogUtils.d(TAG, e.toString());
		}
	}

	/**
	 * okHttp post同步请求
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param paramsMap
	 *            请求参数
	 */
	private void requestPostBySyn(String actionUrl,
			HashMap<String, String> paramsMap) {
		try {
			// 处理参数
			StringBuilder tempParams = new StringBuilder();
			int pos = 0;
			for (String key : paramsMap.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				tempParams.append(String.format("%s=%s", key,
						URLEncoder.encode(paramsMap.get(key), "utf-8")));
				pos++;
			}
			// 补全请求地址
			String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
			// 生成参数
			String params = tempParams.toString();
			// 创建一个请求实体对象 RequestBody
			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
			// 创建一个请求
			final Request request = addHeaders().url(requestUrl).post(body)
					.build();
			// 创建一个Call
			final Call call = mOkHttpClient.newCall(request);
			// 执行请求
			Response response = call.execute();
			// 请求执行成功
			if (response.isSuccessful()) {
				// 获取返回数据 可以是String，bytes ,byteStream
				LogUtils.d(TAG, "response ----->" + response.body().string());
			}
		} catch (Exception e) {
			LogUtils.d(TAG, e.toString());
		}
	}

	/**
	 * okHttp post同步请求表单提交
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param paramsMap
	 *            请求参数
	 */
	private void requestPostBySynWithForm(String actionUrl,
			HashMap<String, String> paramsMap) {
		try {
			// 创建一个FormBody.Builder
			FormBody.Builder builder = new FormBody.Builder();
			for (String key : paramsMap.keySet()) {
				// 追加表单信息
				builder.add(key, paramsMap.get(key));
			}
			// 生成表单实体对象
			RequestBody formBody = builder.build();
			// 补全请求地址
			String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
			// 创建一个请求
			final Request request = addHeaders().url(requestUrl).post(formBody)
					.build();
			// 创建一个Call
			final Call call = mOkHttpClient.newCall(request);
			// 执行请求
			Response response = call.execute();
			if (response.isSuccessful()) {
				LogUtils.d(TAG, "response ----->" + response.body().string());
			}
		} catch (Exception e) {
			LogUtils.d(TAG, e.toString());
		}
	}
	
	
	public void requestHttp(final String actionUrl, final int requestType,
			final HashMap<String, String> paramsMap, final ReqCallBack<String> callBack){
		
		BASE_URL=BASE_URL1;
		requestAsyn(actionUrl, requestType, paramsMap, new ReqCallBack<String>() {
			
			@Override
			public void onReqSuccess(String result) {
				//LogUtils.i(TAG, "BASE_URL 1"+BASE_URL);
				callBack.onReqSuccess(result);
			}
			
			@Override
			public void onReqFailed(String errorMsg) {
				if(!BASE_URL.equals(BASE_URL2)){					
					BASE_URL=BASE_URL2;
				}else{
					BASE_URL=BASE_URL3;
				}
//				LogUtils.i(TAG, "BASE_URL 2"+BASE_URL);
				requestAsyn(actionUrl, requestType, paramsMap, callBack);
			}
		});
	}

	/**
	 * okHttp异步请求统一入口
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param requestType
	 *            请求类型
	 * @param paramsMap
	 *            请求参数
	 * @param callBack
	 *            请求返回数据回调
	 * @param <T>
	 *            数据泛型
	 **/
	public <T> Call requestAsyn(String actionUrl, int requestType,
			HashMap<String, String> paramsMap, ReqCallBack<T> callBack) {
		Call call = null;
		switch (requestType) {
		case TYPE_GET:
			call = requestGetByAsyn(actionUrl, paramsMap, callBack);
			break;
		case TYPE_POST_JSON:
//			call = requestPostByAsyn(actionUrl, paramsMap, callBack);
			call = requestPostByAsynJSON(actionUrl, paramsMap, callBack);
			
			break;
		case TYPE_POST_FORM:
			call = requestPostByAsynWithForm(actionUrl, paramsMap, callBack);
			break;
		}
		return call;
	}

	/**
	 * okHttp get异步请求
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param paramsMap
	 *            请求参数
	 * @param callBack
	 *            请求返回数据回调
	 * @param <T>
	 *            数据泛型
	 * @return
	 */
	private <T> Call requestGetByAsyn(String actionUrl,
			HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
		StringBuilder tempParams = new StringBuilder();
		try {
			int pos = 0;
			for (String key : paramsMap.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				tempParams.append(String.format("%s=%s", key,
						URLEncoder.encode(paramsMap.get(key), "utf-8")));
				pos++;
			}
			String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl,
					tempParams.toString());
			final Request request = addHeaders().url(requestUrl).build();
			final Call call = mOkHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					failedCallBack("访问失败", callBack);
					LogUtils.d(TAG, e.toString());
				}

				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					if (response.isSuccessful()) {
						String string = response.body().string();
						LogUtils.d(TAG, "response ----->" + string);
						successCallBack((T) string, callBack);
					} else {
						failedCallBack("服务器错误", callBack);
					}
				}
			});
			return call;
		} catch (Exception e) {
			LogUtils.d(TAG, e.toString());
		}
		return null;
	}

	/**
	 * okHttp post异步请求
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param paramsMap
	 *            请求参数
	 * @param callBack
	 *            请求返回数据回调
	 * @param <T>
	 *            数据泛型
	 * @return
	 */
	private <T> Call requestPostByAsyn(String actionUrl,
			HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
		try {
			StringBuilder tempParams = new StringBuilder();
			int pos = 0;
			for (String key : paramsMap.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				tempParams.append(String.format("%s=%s", key,
						URLEncoder.encode(paramsMap.get(key), "utf-8")));
				pos++;
			}
			String params = tempParams.toString();
			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
			String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
			
			LogUtils.d(TAG, requestUrl+"params:  "+params);
			
			final Request request = addHeaders().url(requestUrl).post(body)
					.build();
			final Call call = mOkHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					failedCallBack("访问失败", callBack);
					LogUtils.d(TAG, e.toString());
				}

				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					if (response.isSuccessful()) {
						String string = response.body().string();
						LogUtils.d(TAG, "response ----->" + string);
						successCallBack((T) string, callBack);
					} else {
						failedCallBack("服务器错误", callBack);
					}
				}
			});
			return call;
		} catch (Exception e) {
			LogUtils.d(TAG, e.toString());
		}
		return null;
	}
	
	
	private <T> Call requestPostByAsynJSON(String actionUrl,
			HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
		try {
			
			boolean isConn=false;
			
			JsonObject jsonObject=new JsonObject();
			for (String key : paramsMap.keySet()) {
				
				if(key.equals(Constant.key_check_number)){
					isConn=true;
				}
				
				if(key.equals(Constant.key_upgradestate)){
					jsonObject.addProperty(key, Integer.parseInt(paramsMap.get(key)));
				}else{
					jsonObject.addProperty(key, paramsMap.get(key));
				}
			}
			
			String params = jsonObject.toString();
			
			LogUtils.d(TAG, params);
			
			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
			String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
			
			LogUtils.d(TAG, requestUrl);
			
			final Request request = addHeadersCurrent(paramsMap,isConn).url(requestUrl).post(body)
					.build();
			final Call call = mOkHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					LogUtils.d("test"," onFailure  ---->");
					failedCallBack("访问失败 net err", callBack);
					//LogUtils.d(TAG, e.toString());
				}

				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					LogUtils.d("test"," onResponse  ---->");
					if (response.isSuccessful()) {
						String string = response.body().string();
						//LogUtils.d(TAG, "response ----->" + string);
						successCallBack((T) string, callBack);
					} else {
						failedCallBack("服务器错误 err", callBack);
					}
				}
			});
			return call;
		} catch (Exception e) {
			//LogUtils.d(TAG, e.toString());
			failedCallBack("访问失败 e"+ e.getMessage(), callBack);
		}
		return null;
	}
	
	
	public <T> Call requestPostByAsynObj(String actionUrl,
			HashMap<String, String> paramsMap,String dataKey,JsonObject dataJsonObj, JsonArray dataJsonArray,final ReqCallBack<T> callBack) {
		try {
			
			boolean isConn=false;
			
			JsonObject jsonObject=new JsonObject();
			for (String key : paramsMap.keySet()) {
				
				if(key.equals(Constant.key_check_number)){
					isConn=true;
				}
				
				jsonObject.addProperty(key, paramsMap.get(key));
			}
			
			if(dataJsonObj!=null){
				jsonObject.add(dataKey, dataJsonObj);
			}
			
			if(dataJsonArray!=null){
				jsonObject.add(dataKey, dataJsonArray);
			}
			
			
			String params = jsonObject.toString();
			
			LogUtils.d(TAG, "params: "+params);
			
			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
			String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
			
			
			final Request request = addHeadersCurrent(paramsMap,isConn).url(requestUrl).post(body)
					.build();
			final Call call = mOkHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					failedCallBack("访问失败", callBack);
					LogUtils.d(TAG, e.toString());
				}

				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					if (response.isSuccessful()) {
						String string = response.body().string();
						LogUtils.d(TAG, "response ----->" + string);
						successCallBack((T) string, callBack);
					} else {
						failedCallBack("服务器错误", callBack);
					}
				}
			});
			return call;
		} catch (Exception e) {
			LogUtils.d(TAG, e.toString());
			failedCallBack("访问失败"+ e.getMessage(), callBack);
		}
		return null;
	}
	

	/**
	 * okHttp post异步请求表单提交
	 * 
	 * @param actionUrl
	 *            接口地址
	 * @param paramsMap
	 *            请求参数
	 * @param callBack
	 *            请求返回数据回调
	 * @param <T>
	 *            数据泛型
	 * @return
	 */
	private <T> Call requestPostByAsynWithForm(String actionUrl,
			HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
		try {
			FormBody.Builder builder = new FormBody.Builder();
			for (String key : paramsMap.keySet()) {
				builder.add(key, paramsMap.get(key));
			}
			RequestBody formBody = builder.build();
			String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
			final Request request = addHeaders().url(requestUrl).post(formBody)
					.build();
			final Call call = mOkHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					failedCallBack("访问失败", callBack);
					LogUtils.d(TAG, e.toString());
				}

				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					if (response.isSuccessful()) {
						String string = response.body().string();
						LogUtils.d(TAG, "response ----->" + string);
						successCallBack((T) string, callBack);
					} else {
						failedCallBack("服务器错误", callBack);
					}
				}
			});
			return call;
		} catch (Exception e) {
			LogUtils.d(TAG, e.toString());
		}
		return null;
	}

	/**
     * 统一为请求添加头信息
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "3.2.0");
        return builder;
    }
	
	
	private Request.Builder addHeadersCurrent(Map<String, String> params,boolean isConn) {
		Request.Builder builder = new Request.Builder();
		builder.addHeader(Constant.key_appid, "1");
		builder.addHeader(Constant.key_timestamp, System.currentTimeMillis()+"");
		builder.addHeader(Constant.key_sign, SignatureUtil.getSign(params));
		if(isConn){
			builder.addHeader(Constant.key_conn, "close");
		}
		return builder;
	}
	

	/**
	 * 统一同意处理成功信息
	 * 
	 * @param result
	 * @param callBack
	 * @param <T>
	 */
	private <T> void successCallBack(final T result,
			final ReqCallBack<T> callBack) {
		okHttpHandler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onReqSuccess(result);
				}
			}
		});
	}

	/**
	 * 统一处理失败信息
	 * 
	 * @param errorMsg
	 * 
	 * @param callBack
	 * @param <T>
	 */
	private <T> void failedCallBack(final String errorMsg,
			final ReqCallBack<T> callBack) {
		okHttpHandler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onReqFailed(errorMsg);
				}
			}
		});
	}

	/**
	 * 下载文件
	 * 
	 * @param fileUrl
	 *            文件url
	 * @param destFileDir
	 *            存储目标目录
	 */
	public <T> void downLoadFile(String fileUrl,String fileName, final String destFileDir,
			final ReqCallBack<T> callBack) {
		
		File fileDir=new File(destFileDir);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		
		final File file = new File(destFileDir, fileName);
		if (file.exists()) {
			file.delete();
		}
		final Request request = new Request.Builder().url(fileUrl).build();
		final Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				LogUtils.d(TAG, e.toString());
				failedCallBack("下载失败", callBack);
			}

			@Override
			public void onResponse(Call call, Response response)
					throws IOException {
				InputStream is = null;
				byte[] buf = new byte[2048];
				int len = 0;
				FileOutputStream fos = null;
				try {
					long total = response.body().contentLength();
					LogUtils.d(TAG, "total------>" + total);
					long current = 0;
					is = response.body().byteStream();
					fos = new FileOutputStream(file);
					while ((len = is.read(buf)) != -1) {
						current += len;
						fos.write(buf, 0, len);
						LogUtils.d(TAG, "current------>" + current);
					}
					fos.flush();
					successCallBack((T) file, callBack);
				} catch (IOException e) {
					LogUtils.d(TAG, e.toString());
					failedCallBack("下载失败", callBack);
				} finally {
					try {
						if (is != null) {
							is.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						LogUtils.d(TAG, e.toString());
						failedCallBack("下载失败", callBack);
					}
				}
			}
		});
	}
	
	private long current_=0;
	private boolean isDownload=false;
	
	/**
	 * 下载文件
	 * 
	 * @param fileUrl
	 *            文件url
	 * @param destFileDir
	 *            存储目标目录
	 */
	public <T> void downLoadFileProgress(String fileUrl,String fileName, final String destFileDir,
			final ReqProgressCallBack<T> callBack) {
		
		isDownload=true;
		
		File fileDir=new File(destFileDir);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		
		final File file = new File(destFileDir, fileName);
		if (file.exists()) {
			file.delete();
		}
		final Request request = new Request.Builder().url(fileUrl).build();
		final Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				LogUtils.d(TAG, e.toString());
				isDownload=false;
				failedProgressCallBack("下载失败", callBack);
			}

			@Override
			public void onResponse(Call call, Response response)
					throws IOException {
				InputStream is = null;
				byte[] buf = new byte[2048];
				int len = 0;
				FileOutputStream fos = null;
				try {
					final long total = response.body().contentLength();
					//LogUtils.d(TAG, "total------>" + total);
					long current = 0;
					
					current_=0;
					
					progressHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							progressCallBack(total, current_, callBack);
							if(total>0&&total==current_){
								progressHandler.removeCallbacks(this);
							}else{
								progressHandler.postDelayed(this, 1000);
							}
							
							if(!isDownload){
								progressHandler.removeCallbacks(this);
							}
							
						}
					}, 0);
					
					is = response.body().byteStream();
					fos = new FileOutputStream(file);
					while ((len = is.read(buf)) != -1) {
						current += len;
						current_=current;
						fos.write(buf, 0, len);
						//LogUtils.d(TAG, "current------>" + current);
//						progressCallBack(total, current, callBack);
					}
					fos.flush();
					//添加可执行权限
					try {
						String command = "chmod 777 "
								+ file.getAbsolutePath();
						Runtime runtime = Runtime.getRuntime();
						Process proc = runtime.exec(command);
					} catch (IOException e) {
						e.printStackTrace();
					}
					successProgressCallBack((T) file, callBack);
				} catch (IOException e) {
					LogUtils.d(TAG, e.toString());
					isDownload=false;
					failedProgressCallBack("下载失败", callBack);
				} finally {
					try {
						if (is != null) {
							is.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						LogUtils.d(TAG, e.toString());
						isDownload=false;
						failedProgressCallBack("下载失败", callBack);
					}
				}
			}
		});
	}

	
	/**
	 * 统一同意处理成功信息
	 * 
	 * @param result
	 * @param callBack
	 * @param <T>
	 */
	private <T> void successProgressCallBack(final T result,
			final ReqProgressCallBack<T> callBack) {
		okHttpHandler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onReqSuccess(result);
				}
			}
		});
	}

	/**
	 * 统一处理失败信息
	 * 
	 * @param errorMsg
	 * 
	 * @param callBack
	 * @param <T>
	 */
	private <T> void failedProgressCallBack(final String errorMsg,
			final ReqProgressCallBack<T> callBack) {
		okHttpHandler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onReqFailed(errorMsg);
				}
			}
		});
	}
	
	
	/**
	 * 统一处理失败信息
	 * 
	 *
	 * 
	 * @param callBack
	 * @param <T>
	 */
	private <T> void progressCallBack(final long total , final long current,
			final ReqProgressCallBack<T> callBack) {
		okHttpHandler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onReqProgress(total , current);
				}
			}
		});
	}
	
}
