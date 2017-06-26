package com.gogostar.zaojiaobao;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

/**
 * Created by Administrator on 2017/6/12.
 */

public class EarlyInfoActivity extends BaseActivity implements View.OnClickListener {

	// 版本类型
	private int type;

	private Button btn_new, btn_sunny, btn_happy;
	private TableLayout layout1, layout2, layout3;
	private ImageView img_back;
	private LinearLayout root_linear;

	// 新奇版月份按钮
	private int[] newBtn = new int[]{R.id.btn_item1_Jan, R.id.btn_item1_Feb, R.id.btn_item1_Mar,
			R.id.btn_item1_Apr, R.id.btn_item1_May, R.id.btn_item1_June, R.id.btn_item1_July,
			R.id.btn_item1_Aug, R.id.btn_item1_Sep, R.id.btn_item1_Oct, R.id.btn_item1_Nov,
			R.id.btn_item1_Dec};
	// 阳光版月份按钮
	private int[] sunnyBtn = new int[]{R.id.btn_item2_Jan, R.id.btn_item2_Feb, R.id.btn_item2_Mar,
			R.id.btn_item2_Apr, R.id.btn_item2_May, R.id.btn_item2_June, R.id.btn_item2_July, R.id
			.btn_item2_Aug, R.id.btn_item2_Sep, R.id.btn_item2_Oct, R.id.btn_item2_Nov, R.id
			.btn_item2_Dec};
	// 快乐版月份按钮
	private int[] happyBtn = new int[]{R.id.btn_item3_Jan, R.id.btn_item3_Feb, R.id.btn_item3_Mar,
			R.id.btn_item3_Apr, R.id.vp_item3_May, R.id.btn_item3_June, R.id.btn_item3_July, R.id
			.btn_item3_Aug, R.id.btn_item3_Sep, R.id.btn_item3_Oct, R.id.btn_item3_Nov, R.id
			.btn_item3_Dec};

	// 新奇版 锁
	private int[] suoImg1 = new int[]{R.id.img_item1_suo1, R.id.img_item1_suo2, R.id
			.img_item1_suo3, R.id.img_item1_suo4, R.id.img_item1_suo5, R.id.img_item1_suo6, R.id
			.img_item1_suo7, R.id.img_item1_suo8, R.id.img_item1_suo9, R.id.img_item1_suo10, R.id
			.img_item1_suo11, R.id.img_item1_suo12};
	// 阳光版 锁
	private int[] suoImg2 = new int[]{R.id.img_item2_suo1, R.id.img_item2_suo2, R.id
			.img_item2_suo3, R.id.img_item2_suo4, R.id.img_item2_suo5, R.id.img_item2_suo6, R.id
			.img_item2_suo7, R.id.img_item2_suo8, R.id.img_item2_suo9, R.id.img_item2_suo10, R.id
			.img_item2_suo11, R.id.img_item2_suo12};
	// 快乐版 锁
	private int[] suoImg3 = new int[]{R.id.img_item3_suo1, R.id.img_item3_suo2, R.id
			.img_item3_suo3, R.id.img_item3_suo4, R.id.img_item3_suo5, R.id.img_item3_suo6, R.id
			.img_item3_suo7, R.id.img_item3_suo8, R.id.img_item3_suo9, R.id.img_item3_suo10, R.id
			.img_item3_suo11, R.id.img_item3_suo12};

	// 月份图片
	private int[] imgs = new int[]{R.drawable.jan, R.drawable.feb, R.drawable.mar, R
			.drawable.apr, R.drawable.may, R.drawable.june, R.drawable.july, R.drawable
			.aug, R.drawable.sep, R.drawable.oct, R.drawable.nov, R.drawable.dec};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_earlyinfo);

		type = getIntent().getIntExtra("TYPE", -1);
	}

	/**
	 * 加载页面布局
	 */
	private void initView() {
		root_linear = (LinearLayout) findViewById(R.id.root_linear);
		btn_new = (Button) findViewById(R.id.btn_earlyInfo_new);
		btn_sunny = (Button) findViewById(R.id.btn_earlyInfo_sunny);
		btn_happy = (Button) findViewById(R.id.btn_earlyInfo_happy);
		img_back = (ImageView) findViewById(R.id.btn_earlyInfo_back);

		layout1 = (TableLayout) findViewById(R.id.table1);
		layout2 = (TableLayout) findViewById(R.id.table2);
		layout3 = (TableLayout) findViewById(R.id.table3);

		btn_new.setOnClickListener(this);
		btn_sunny.setOnClickListener(this);
		btn_happy.setOnClickListener(this);
		img_back.setOnClickListener(this);

		// 新奇版
		initBtn(newBtn, suoImg1, 0, type);
		// 阳光版
		initBtn(sunnyBtn, suoImg2, 1, type);
		// 快乐版
		initBtn(happyBtn, suoImg3, 2, type);
	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_earlyInfo_new: // 新奇版按钮事件
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.GONE);
				layout3.setVisibility(View.GONE);
				root_linear.setBackgroundResource(R.drawable.bg1);
				break;
			case R.id.btn_earlyInfo_sunny: // 阳光版按钮事件
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				layout3.setVisibility(View.GONE);
				root_linear.setBackgroundResource(R.drawable.bg2);
				break;
			case R.id.btn_earlyInfo_happy: // 快乐版按钮事件
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.GONE);
				layout3.setVisibility(View.VISIBLE);
				root_linear.setBackgroundResource(R.drawable.bg3);
				break;
			case R.id.btn_earlyInfo_back:
				finish();
				break;
		}
	}

	/**
	 * 定义月份按钮方法
	 *
	 * @param btns    按钮id数组
	 * @param Imgs    锁id数组
	 * @param version 资源类型
	 * @param type    版本类型
	 */
	private void initBtn(int[] btns, int[] Imgs, final int version, final int type) {

		// 获取系统当前月份
		Time time = new Time("GTM+8");
		time.setToNow();
		int month = time.month;

		for (int i = 0; i < btns.length; i++) {
			ImageView mBtn = (ImageView) findViewById(btns[i]);
			ImageView sImg = (ImageView) findViewById(Imgs[i]);
			final int finalI = i;
			// 为当前月份按钮绑定点击事件
//			if (month == i) {
//				mBtn.setImageResource(imgs[i]);
				sImg.setVisibility(View.GONE);
				mBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(EarlyInfoActivity.this, MediaInfoActivity
								.class);
						intent.putExtra("Version", version);
						intent.putExtra("Month", finalI);
						intent.putExtra("Type", type);
						startActivity(intent);
					}
				});
//			}
		}
	}
}
