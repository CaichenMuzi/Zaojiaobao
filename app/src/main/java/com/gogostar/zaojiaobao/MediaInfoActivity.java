package com.gogostar.zaojiaobao;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/12.
 */

public class MediaInfoActivity extends BaseActivity {

	private MyUtil myUtil = new MyUtil();

	private GridView mGridView;
	private GridViewAdapter adapter;
	private List<SubjectInfo> list;

	private ImageView img_back;

	private int version, month, type;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					list = (List<SubjectInfo>) msg.obj;
					initGridView(list);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediainfo);

		version = getIntent().getIntExtra("Version", -1);
		month = getIntent().getIntExtra("Month", -1);
		type = getIntent().getIntExtra("Type", -1);

		if (type == 0 || type == 1 || type == 2 || type == 3 || type == 4) {
			new Thread(runnable).start();
		} else if (type == 5) {
			new Thread(runnable1).start();
		}

		img_back = (ImageView) findViewById(R.id.img_mediaInfo_back);
		img_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 加载GridView
	 *
	 * @param list
	 */
	private void initGridView(final List<SubjectInfo> list) {
		mGridView = (GridView) findViewById(R.id.gv_media_early);
		adapter = new GridViewAdapter(this, list);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SubjectInfo subjectInfo = list.get(position);
				if (type == 0) {
					Intent intent = new Intent(MediaInfoActivity.this, Mp4Activity.class);
					intent.putExtra("SubInfo", (Serializable) subjectInfo);
					startActivity(intent);
				} else if (type == 2 || type == 3 || type == 4) {
					Intent intent = new Intent(MediaInfoActivity.this, Mp3Activity.class);
					intent.putExtra("SubList", (Serializable) list);
					intent.putExtra("index", position);
					startActivity(intent);
				} else if (type == 5) {
					int index = subjectInfo.getFile_url().lastIndexOf(".");
					String fileName = subjectInfo.getFile_url().substring(index);
					File file = new File(Common.OutPath + File.separator + version + File
							.separator + month + File.separator + subjectInfo.getName() +
							fileName);
					if (!file.exists()) {
						doDownloadTask(Common.HEAD_URL + subjectInfo.getFile_url(), Common.OutPath
								+ File.separator + version + File.separator + month, subjectInfo
								.getName());
					} else {
						openApp(file);
					}
				}
			}
		});
	}

	/**
	 * 异步处理
	 *
	 * @param url
	 * @param out
	 * @param fileName
	 */
	private void doDownloadTask(String url, String out, String fileName) {
		DownloadTask task = new DownloadTask(url, out, this, fileName);
		task.execute();
	}

	/**
	 * 打开apk
	 *
	 * @param file
	 */
	public void openApp(File file) {
		PackageManager pm = MediaInfoActivity.this.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager
				.GET_ACTIVITIES);
		ApplicationInfo appInfo = null;
		if (info != null) {
			appInfo = info.applicationInfo;
			String packageName = appInfo.packageName;
			myUtil.openApk(MediaInfoActivity.this, packageName, file.getAbsolutePath());
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			List<SubjectInfo> list = getMediaSub();
			mHandler.obtainMessage(1, list).sendToTarget();
		}
	};

	/**
	 * 获取Media列表
	 *
	 * @return
	 */
	private List<SubjectInfo> getMediaSub() {
		List<SubjectInfo> list = new ArrayList<SubjectInfo>();
		try {
			JSONObject mediaJson = myUtil.sendByHttp(Common.GET, Common.MEDIA_URL + "?version=" +
					version + "&month=" + month + "&type=" + type, null);
			if (mediaJson != null) {
				String result = mediaJson.getString(Common.Result);
				if (result.equals(Common.True)) {
					JSONArray contentJson = mediaJson.getJSONArray(Common.Content);
					int length = contentJson.length();
					for (int i = 0; i < length; i++) {
						JSONObject jsonObject = contentJson.getJSONObject(i);
						int id = jsonObject.getInt(Common.ID);
						int zID = jsonObject.getInt(Common.ZaojiaobaoID);
						String name = jsonObject.getString(Common.Name);
						String fileUrl = jsonObject.getString(Common.FileUrl);
						String iconUrl = jsonObject.getString(Common.IconUrl);

//						int index = iconUrl.lastIndexOf("/");
//						String fileName = iconUrl.substring(index + 1);
//						myUtil.downloadFile(iconUrl, OutPath + File.separator + version +
//								File.separator + month, fileName);

						SubjectInfo subjectInfo = new SubjectInfo();
						subjectInfo.setId(id);
						subjectInfo.setCategory_id(zID);
						subjectInfo.setName(name);
						subjectInfo.setFile_url(fileUrl);
						subjectInfo.setIcon_url(Common.HEAD_URL + iconUrl);
						list.add(subjectInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	Runnable runnable1 = new Runnable() {
		@Override
		public void run() {
			List<SubjectInfo> list = getAppSubList();
			mHandler.obtainMessage(1, list).sendToTarget();
		}
	};

	private List<SubjectInfo> getAppSubList() {
		List<SubjectInfo> list = new ArrayList<SubjectInfo>();
		try {
			/**
			 * 获取apk列表信息
			 */
			JSONObject apkJson = myUtil.sendByHttp(Common.GET, Common.APP_URL + "?version=" +
					version + "&month=" + month, null);
			if (apkJson != null) {
				String result = apkJson.getString(Common.Result);
				if (result.equals(Common.True)) {
					JSONArray contentJson = apkJson.getJSONArray(Common.Content);
					int length = contentJson.length();
					for (int i = 0; i < length; i++) {
						JSONObject jsonObject1 = contentJson.getJSONObject(i);
						int id = jsonObject1.getInt(Common.ID);
						int zID = jsonObject1.getInt(Common.ZaojiaobaoID);
						String name = jsonObject1.getString(Common.Name);
						String introduction = jsonObject1.getString(Common.Introduction);
						String apkUrl = jsonObject1.getString(Common.ApkUrl);
						String iconUrl = jsonObject1.getString(Common.IconUrl);

//						int index = iconUrl.lastIndexOf("/");
//						String fileName = iconUrl.substring(index + 1);
//						myUtil.downloadFile(iconUrl, Common.OutPath, fileName);

						SubjectInfo subjectInfo = new SubjectInfo();
						subjectInfo.setId(id);
						subjectInfo.setCategory_id(zID);
						subjectInfo.setName(name);
						subjectInfo.setIntroduction(introduction);
						subjectInfo.setFile_url(apkUrl);
						subjectInfo.setType(5);
						subjectInfo.setIcon_url(Common.HEAD_URL + iconUrl);
						list.add(subjectInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
