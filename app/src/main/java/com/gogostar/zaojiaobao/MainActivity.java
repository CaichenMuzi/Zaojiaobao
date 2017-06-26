package com.gogostar.zaojiaobao;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

	private Button btn_video, btn_story, btn_tongyao, btn_nianyao, btn_carton, btn_game,
			btn_picture, btn_zhaohu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
	}

	/**
	 * 加载页面布局
	 */
	private void initView() {
		btn_video = (Button) findViewById(R.id.btn_main_video);
		btn_story = (Button) findViewById(R.id.btn_main_story);
		btn_tongyao = (Button) findViewById(R.id.btn_main_tongyao);
		btn_nianyao = (Button) findViewById(R.id.btn_main_nianyao);
		btn_game = (Button) findViewById(R.id.btn_main_game);
		btn_carton = (Button) findViewById(R.id.btn_main_carton);
		btn_picture = (Button) findViewById(R.id.btn_main_picture);
		btn_zhaohu = (Button) findViewById(R.id.btn_main_zhaohu);

		btn_video.setOnClickListener(this);
		btn_story.setOnClickListener(this);
		btn_tongyao.setOnClickListener(this);
		btn_nianyao.setOnClickListener(this);
		btn_game.setOnClickListener(this);
		btn_carton.setOnClickListener(this);
//		btn_picture.setOnClickListener(this);
//		btn_zhaohu.setOnClickListener(this);
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_main_video:
				startActivity(0);
				break;
			case R.id.btn_main_story:
				startActivity(2);
				break;
			case R.id.btn_main_tongyao:
				startActivity(4);
				break;
			case R.id.btn_main_nianyao:
				startActivity(3);
				break;
			case R.id.btn_main_game:
				startActivity(5);
				break;
			case R.id.btn_main_carton:
				startActivity(1);
				break;
			case R.id.btn_main_picture:
				if (isInstalled(this, Common.PIC_PACKAGE_NAME)) {
					openApk(this, Common.PIC_PACKAGE_NAME);
				} else {
					doDownloadTask(Common.PIC_URL, Common.OutPath, Common.PIC_PACKAGE_NAME);
				}
				break;
			case R.id.btn_main_zhaohu:
				if (isInstalled(this, Common.ZH_PACKAGE_NAME)) {
					openApk(this, Common.ZH_PACKAGE_NAME);
				} else {
					doDownloadTask(Common.ZH_URL, Common.OutPath, Common.ZH_PACKAGE_NAME);
				}
				break;
		}
	}

	/**
	 * 打开Activity
	 *
	 * @param type
	 */
	private void startActivity(int type) {
		Intent intent = new Intent(MainActivity.this, EarlyInfoActivity.class);
		intent.putExtra("TYPE", type);
		startActivity(intent);
	}

	/**
	 * 异步加载
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
	 * 安装apk
	 *
	 * @param file
	 */
	public void installApp(File file) {
		if (file.exists()) {
			try {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.INSTALL_PACKAGE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android" +
						".package-archive");
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "未找到该应用", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 打开apk
	 *
	 * @param context
	 * @param packageName
	 */
	private void openApk(Context context, String packageName) {
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
	}

	/**
	 * 判断apk是否安装
	 *
	 * @param context
	 * @param packageName
	 * @return
	 */
	private boolean isInstalled(Context context, String packageName) {

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
}
