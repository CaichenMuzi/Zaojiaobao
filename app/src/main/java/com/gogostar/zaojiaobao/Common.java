package com.gogostar.zaojiaobao;

import android.os.Environment;

/**
 * Created by Administrator on 2017/5/31.
 */

public class Common {

	//	public static final String HEAD_URL = "http://192.168.0.2:4567/www/";
	public static final String HEAD_URL = "http://www.vitacastle.com:4578/www/";
	//	public static final String CATEGORY_URL = "http://www.vitacastle
	// .com:4578/api/GetCategoryList";
	//	public static final String GROWTH_APP_URL = "http://www.vitacastle
	// .com:4578/api/GetGrowthApp";
	public static final String MEDIA_URL = "http://www.vitacastle.com:4578/api/GetMediaList";
	public static final String APP_URL = "http://www.vitacastle.com:4578/api/GetZaoApp";

	public static final String OutPath = Environment.getExternalStorageDirectory().toString() +
			"/gogostar/BobdogEarly/Resources";

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String Result = "Result";
	public static final String True = "True";
	public static final String Content = "Content";
	public static final String ID = "ID";
	public static final String CategoryID = "CategoryID";
	public static final String ZaojiaobaoID = "ZaojiaobaoID";
	public static final String Name = "Name";
	public static final String Introduction = "Introduction";
	public static final String ApkUrl = "ApkUrl";
	public static final String IconUrl = "IconUrl";
	public static final String FileUrl = "FileUrl";

	public static final String PIC_PACKAGE_NAME = "com.gogostar.enstory";
	public static final String PIC_URL = "http://www.vitacastle" +
			".com:4578/www/resources/apk/gogopicture.apk";
	public static final String ZH_PACKAGE_NAME = "com.gogostar.gogoparent";
	public static final String ZH_URL = "http://www.vitacastle" +
			".com:4578/www/resources/apk/GoGoParent.apk";
}
