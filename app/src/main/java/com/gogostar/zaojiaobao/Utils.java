package com.gogostar.zaojiaobao;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 * <p>
 * 自定义工具类
 */

class MyUtil {

	/**
	 * POST请求操作
	 *
	 * @param jsonObject JSON数据
	 * @param path       接口地址
	 * @return JSONObject
	 */
	public JSONObject sendByHttp(String type, String path, JSONObject jsonObject) {
		JSONObject resultJson = null;
		URL url = null;
		try {
			// 根据地址创建URL对象
			url = new URL(path);
//			}
			// 根据URL对象打开链接
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			// 设置请求的方式
			urlConnection.setRequestMethod(type);
			// 设置请求的超时时间
			urlConnection.setReadTimeout(5000);
			urlConnection.setConnectTimeout(5000);
			// 设置请求的头
			urlConnection.setRequestProperty("Connection", "keep-alive");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; " +
					"rv:27.0) Gecko/20100101 Firefox/27.0");

			if (type.equals("POST")) {

				// 传递的数据
				String data = jsonObject.toString();
				urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes()
						.length));

				// 发送POST请求必须设置允许输出
				urlConnection.setDoInput(true);
				// 发送POST请求必须设置允许输入
				urlConnection.setDoOutput(true);

				// 获取输出流
				OutputStream os = urlConnection.getOutputStream();
				os.write(data.getBytes());
				os.flush();
				Log.d("sendPost", os.toString());
			}
			if (urlConnection.getResponseCode() == 200) {
				// 获取响应的输入流对象
				InputStream is = urlConnection.getInputStream();
				// 创建字节输出流对象
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 定义读取的长度
				int len = 0;
				// 定义缓存区
				byte buffer[] = new byte[1024];
				// 按照缓存区的大小，循环读取
				while ((len = is.read(buffer)) != -1) {
					// 根据读取的长度写入到os对象中
					baos.write(buffer, 0, len);
				}
				// 释放资源
				is.close();
				baos.close();
				// 返回字符串
				String result = new String(baos.toByteArray());
				String string = getString(result);
				resultJson = new JSONObject(string);

			} else {
				System.out.println("链接失败......");
				System.out.println(String.valueOf(urlConnection.getResponseCode()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	/**
	 * 从网络下载文件
	 *
	 * @param urlPath
	 * @param outPath
	 * @param fileName
	 */
	public void downloadFile(final String urlPath, final String outPath, final String fileName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(outPath);
					if (!file.exists()) {
						file.mkdirs();
					}
					URL url = new URL(Common.HEAD_URL + urlPath);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(5000);
					conn.setConnectTimeout(5000);
					conn.setRequestProperty("Charset", "UTF-8");
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						FileOutputStream fileOutputStream = null;
						if (is != null) {
							fileOutputStream = new FileOutputStream(new File(outPath, fileName));
							byte[] buf = new byte[1024];
							int ch;
							while ((ch = is.read(buf)) != -1) {
								fileOutputStream.write(buf, 0, ch);
							}
						}
						if (fileOutputStream != null) {
							fileOutputStream.flush();
							fileOutputStream.close();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 根据包名判断apk是否安装
	 *
	 * @param context
	 * @param packageName
	 * @return
	 */
	public boolean isInstalled(Context context, String packageName) {

		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();

		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				String pn = packageInfos.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}

	/**
	 * 打开指定包名的apk
	 *
	 * @param context
	 * @param packageName
	 * @param apkPath
	 */
	public void openApk(Context context, String packageName, String apkPath) {
		if (isInstalled(context, packageName)) {
			PackageManager pm = context.getPackageManager();

			PackageInfo pi = null;
			try {
				pi = pm.getPackageInfo(packageName, 0);
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setPackage(pi.packageName);

				List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

				ResolveInfo ri = apps.iterator().next();
				if (ri != null) {
					String className = ri.activityInfo.name;
					intent.addCategory(Intent.CATEGORY_LAUNCHER);

					ComponentName cn = new ComponentName(packageName, className);

					intent.setComponent(cn);
					context.startActivity(intent);
				}
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			File file = new File(apkPath);
			if (file.exists()) {
				try {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.INSTALL_PACKAGE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android" +
							".package-archive");
					context.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(context, "未找到该应用", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 从服务器获取图片Bitmap
	 *
	 * @param path 图片URL
	 * @return Bitmap
	 */
	public Bitmap getBitmap(String path) {
		Bitmap bitmap1 = null;
		URL url = null;
		try {
			// 链接服务器
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			// 获取返回的输入流并转换成Bitmap
			InputStream in = new BufferedInputStream(conn.getInputStream());
			bitmap1 = BitmapFactory.decodeStream(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap1;
	}

	/**
	 * 去除字符串中多余的符号
	 *
	 * @param string 返回的JSON字符串
	 * @return String
	 */
	public String getString(String string) {
		return string.replace("\\", "").replace("\"[", "[").replace("]\"",
				"]").replace("\"{", "{").replace("}\"", "}");
	}

	/**
	 * 将字符串MD5加密
	 *
	 * @param str 加密的字符串
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String getMD5(String str) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		// 将字符串转换成加密的字节数组
		byte[] bs = md5.digest(str.getBytes());
		StringBuilder sb = new StringBuilder(40);
		// 拼接字节数组
		for (byte x : bs) {
			if ((x & 0xff) >> 4 == 0) {
				sb.append("0").append(Integer.toHexString(x & 0xff));
			} else {
				sb.append(Integer.toHexString(x & 0xff));
			}
		}
		return sb.toString();
	}

	/**
	 * 定义图片放大消失的动画
	 *
	 * @param imageView 需要添加动画的Image
	 * @return AnimationSet
	 */
	public AnimationSet ImgAnimation(ImageView imageView) {
		AnimationSet animationSet = new AnimationSet(false);
		// 以View中心点作为缩放中心，水平方向和垂直方向都扩大为原来的1.5倍
		float fromXScale = 1.0f;
		float toScaleX = 1.5f;
		float fromYScale = 1.0f;
		float toScaleY = 1.5f;
		float pivotX = imageView.getWidth() / 2;
		float pivotY = imageView.getHeight() / 2;
		Animation animation = new ScaleAnimation(fromXScale, toScaleX, fromYScale,
				toScaleY, pivotX, pivotY);
		// 1.0表示完全不透明，0.0表示完全透明
		float fromAlpha = 1.0f;
		float toAlpha = 0.0f;
		// 1.0 => 0.0表示View从完全不透明渐变到完全透明
		// 设置动画集
		Animation animation1 = new AlphaAnimation(fromAlpha, toAlpha);
		animationSet.addAnimation(animation);
		animationSet.addAnimation(animation1);
		// 设置动画持续时间
		animationSet.setDuration(2000);
		// 动画执行完毕后是否停在结束时的角度上
		animationSet.setFillAfter(true);
		return animationSet;
	}

	/**
	 * 定义图片缩小消失的动画
	 *
	 * @param imageView 需要添加动画的Image
	 * @return AnimationSet
	 */
	public AnimationSet getAnimation(ImageView imageView) {
		AnimationSet animationSet = new AnimationSet(false);
		// 以View中心点作为缩放中心，水平方向和垂直方向都缩小
		float fromXScale = 1.0f;
		float toScaleX = 0.0f;
		float fromYScale = 1.0f;
		float toScaleY = 0.0f;
		float pivotX = imageView.getWidth() / 2;
		float pivotY = imageView.getHeight() / 2;
		Animation animation = new ScaleAnimation(fromXScale, toScaleX, fromYScale,
				toScaleY, pivotX, pivotY);
		// 1.0表示完全不透明，0.0表示完全透明
		float fromAlpha = 1.0f;
		float toAlpha = 0.0f;
		// 1.0 => 0.0表示View从完全不透明渐变到完全透明
		// 设置动画集
		Animation animation1 = new AlphaAnimation(fromAlpha, toAlpha);
		animationSet.addAnimation(animation);
		animationSet.addAnimation(animation1);
		// 设置动画持续时间
		animationSet.setDuration(2000);
		// 动画执行完毕后是否停在结束时的角度上
		animationSet.setFillAfter(true);
		return animationSet;
	}

	// 定义静态变量
	private static MediaPlayer mediaPlayer = new MediaPlayer();
	private static SoundPool soundPool;
	private static int soundId;

	// 封装MediaPlayer
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	/**
	 * 播放音频
	 *
	 * @param path 音频地址
	 */
	public void initMediaPlayer(String path) {
		try {
			mediaPlayer.reset();// 重置mediaPlayer
			mediaPlayer.setDataSource(path); // 指定音频文件的路径
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止播放音频
	 */
	public void stopMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();// 释放
			mediaPlayer = null;
		}
	}

	/**
	 * 定义播放按钮音效方法
	 *
	 * @param context 当前页面
	 * @param musicId 音效id
	 */
	public void playSound(Context context, int musicId) {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		soundId = soundPool.load(context, musicId, 1);
		// 设置监听器，在加载音乐文件完成时触发该事件
		soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1);
			}
		});
	}

	/**
	 * 播放音效
	 *
	 * @param context 当前页面
	 * @param musicId 音效id
	 */
	public void playMusic(Context context, int musicId) {
		mediaPlayer.reset();
		mediaPlayer = MediaPlayer.create(context, musicId);
		mediaPlayer.start();
	}

	/**
	 * 从txt文件中读取文本
	 *
	 * @param file txt文件
	 * @return
	 */
	public String readTxtFile(File file) {
		String lineTxt = null;
		try {
			String enconding = "UTF-8";
			if (file.isFile() && file.exists()) {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file),
						enconding);
				BufferedReader bufferedReader = new BufferedReader(reader);
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt);
					return lineTxt.replaceAll("[\\p{Punct}]", " ");
				}
				reader.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return lineTxt;
	}

	/**
	 * 从xml中读取文本
	 *
	 * @param xmlData xml文件
	 * @return
	 */
	public String parseXMLWithPull(String xmlData) {
		String total_score = "";
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlData));
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG: {
						if ("total_score".equals(nodeName)) {
							total_score = xmlPullParser.getAttributeValue(0);
							return total_score;
						}
					}
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total_score;
	}
}

/**
 *
 */
class MyBitmapUtils {
	private NetCacheUtils mNetCacheUtils;
	private LocalCacheUtils mLocalCacheUtils;
	private MemoryCacheUtils mMemoryCacheUtils;

	public MyBitmapUtils() {
		mMemoryCacheUtils = new MemoryCacheUtils();
		mLocalCacheUtils = new LocalCacheUtils();
		mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
	}

	/**
	 * @param imageView 要展示加载图片的ImageView
	 * @param url       加载图片的链接
	 */
	public void display(ImageView imageView, String url) {

		// 设置默认图片
//		imageView.setImageResource(resId);

		Bitmap bitmap = null;
		// 先从内存加载，如果内存中有值
		bitmap = mMemoryCacheUtils.getMemoryCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			return;
		}

		// 先从本地加载，判断是否有本地缓存
		bitmap = mLocalCacheUtils.getLocalCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			mMemoryCacheUtils.setMemoryCache(url, bitmap);
			return;
		}

		// 从网络加载
		mNetCacheUtils.getBitmapFromNet(imageView, url);
	}
}

/***
 * 网络缓存工具
 */
class NetCacheUtils {

	private LocalCacheUtils mLocalCacheUtil;
	private MemoryCacheUtils mMemoryCacheUtils;

	public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
		super();
		this.mLocalCacheUtil = localCacheUtils;
		this.mMemoryCacheUtils = memoryCacheUtils;
	}

	// 从网络加载图片
	public void getBitmapFromNet(ImageView imageView, String url) {
		// imageView,url 两个参数会封装为数组传给doInBackground
		new BitmapTask().execute(imageView, url);
	}

	/**
	 * 异步处理
	 */
	class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {

		private ImageView imageView;
		private String url;

		/**
		 * 1.预加载 ，运行在主线程
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		/**
		 * 2.正在加载(核心方法)，运行在子线程
		 */
		@Override
		protected Bitmap doInBackground(Object... params) {
			imageView = (ImageView) params[0];
			url = (String) params[1];

			imageView.setTag(url);// 打标记

			Bitmap bitmap = download(url);
			return bitmap;
		}

		/**
		 * 3.进度更新（如果下载文件），运行在主线程
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * 4.加载结束，运行在主线程
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				String url = (String) imageView.getTag();
				if (url != null && url.equals(this.url)) {
					// 从网络加载图片
					imageView.setImageBitmap(bitmap);
					// 写本地内存
					mLocalCacheUtil.setLocalCache(url, bitmap);
					// 写本地缓存
					mMemoryCacheUtils.setMemoryCache(url, bitmap);
				}
			}
		}
	}

	/**
	 * 根据url下载图片
	 *
	 * @param url
	 * @return
	 */
	public Bitmap download(String url) {
		HttpURLConnection conn;
		try {
			URL mUrl = new URL(url);
			conn = (HttpURLConnection) mUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			int code = conn.getResponseCode();

			if (code == 200) {
				// 成功后获取流，进行处理
				InputStream inputStream = conn.getInputStream();
				// 根据流来获取对应的数据，这里是图片，所以直接根据流得到bitmap对象
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

/**
 * 本地缓存工具类
 */
class LocalCacheUtils {
	private static final String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/gogostar/BobdogEarly/Resources/image";

	// 写本地缓存
	public void setLocalCache(String url, Bitmap bitmap) {
		File dir = new File(LOCAL_CACHE_PATH);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}

		try {
			String fileName = MD5Encoder.encode(url);// 采用MD5加密文件名
			File cacheFile = new File(dir, fileName);
			// 参1：图片格式；参2：压缩比例0-100；参3：输出流
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(cacheFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读本地缓存
	public Bitmap getLocalCache(String url) {
		try {
			File cacheFile = new File(LOCAL_CACHE_PATH, MD5Encoder.encode(url));
			if (cacheFile.exists()) {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

/**
 * 内存缓存工具类
 * 第一次优化，利用软引用解决可能的OOM异常
 */
class MemoryCacheUtils {

	private HashMap<String, SoftReference<Bitmap>> hash;

	// 写内存缓存
	public void setMemoryCache(String url, Bitmap bitmap) {
		if (hash == null) {
			hash = new HashMap<String, SoftReference<Bitmap>>();
		}
		// 使用软引用把Bitmap包装起来
		SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);
		hash.put(url, soft);
	}

	// 读内存缓存
	public Bitmap getMemoryCache(String url) {
		if (hash != null && hash.containsKey(url)) {
			SoftReference<Bitmap> softReference = hash.get(url);
			Bitmap bitmap = softReference.get();
			return bitmap;
		}
		return null;
	}
}
