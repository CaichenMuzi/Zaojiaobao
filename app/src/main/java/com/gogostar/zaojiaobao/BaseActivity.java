package com.gogostar.zaojiaobao;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.File;

/**
 * Created by Administrator on 2017/3/3.
 */

public abstract class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加活动
		ActivityCollector.addActivity(this);

		// 添加权限
		if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission
					.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		}

		hideBottomUIMenu();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
			grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager
						.PERMISSION_GRANTED) {
					File file = new File(Environment.getExternalStorageDirectory() +
							"/gogostar/");
					if (!file.exists()) {
						file.mkdirs();
					}
				} else {
					finish();
				}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 移除活动
		ActivityCollector.removeActivity(this);
	}

	/**
	 * 隐藏虚拟按键，并且全屏
	 */
	protected void hideBottomUIMenu() {
		//隐藏虚拟按键，并且全屏
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}
}
