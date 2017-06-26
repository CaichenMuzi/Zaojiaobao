package com.gogostar.zaojiaobao;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/12.
 * <p>
 * ActivityCollector类用于管理所有的活动
 */

public class ActivityCollector {

	// 活动列表
	public static List<Activity> activities = new ArrayList<Activity>();

	// 将活动添加到列表里
	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	// 将活动从列表中移除
	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}

	// 关闭所有活动
	public static void finishAll() {
		for (Activity activity : activities) {
			// 如果活动没有关闭，关闭活动
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}
}
