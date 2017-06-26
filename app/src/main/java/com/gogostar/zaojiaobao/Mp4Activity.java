package com.gogostar.zaojiaobao;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/6/1.
 */

public class Mp4Activity extends BaseActivity {
	// 播放
	private ImageView img_start;
	// 底部视图栏
	private RelativeLayout bottom_relative;
	// 上部试图栏
	private RelativeLayout top_relative;

	private View view;

	private FullVideoView video_VideoView;
	// 当前播放时间
	private TextView txt_current_time;
	// 总时间
	private TextView txt_max_time;
	// 当前播放进度
	private ImageView img_white;
	// 播放进度背景
	private ImageView img_bg;
	// 拖放进度
	private ImageView img_center_speed;
	// 返回按钮
	private ImageView img_back;

	private AudioManager mAudioManager;
	// 屏幕宽度
	private int width;
	// 屏幕高度
	private int height;
	// 手势
	private GestureDetector mGestureDetector;
	// 视频总长度
	private long mVideo_total_length;
	// 视频当前长度
	private long mVideo_current_length;
	// 按下手势时的X点
	private float downX = 0;
	// 按下手势时的Y点
	private float downY = 0;
	// 移动手势时的X点
	private float moveX = 0;
	// 移动手势时的Y点
	private float moveY = 0;
	// 记录上一次移动手势时的X点
	private float oldMoveX = 0;
	// 记录上一次移动手势时的Y点
	private float oldMoveY = 0;
	// 是否快进了
	private boolean isVideo = false;
	// 是否点击了开始播放
	private boolean isStart = false;

	private Dialog dialog;

	private static final int handKey = 123;

	private SubjectInfo subjectInfo;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == handKey) {
				setVideo();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mp4);

		subjectInfo = (SubjectInfo) getIntent().getSerializableExtra("SubInfo");
		initView();
		widgetListener();
	}

	private void initView() {
		img_start = (ImageView) findViewById(R.id.video_img_start);

		txt_current_time = (TextView) findViewById(R.id.video_txt_current_time);
		txt_max_time = (TextView) findViewById(R.id.video_txt_max_time);
		img_white = (ImageView) findViewById(R.id.video_videoview_pres_front);
		img_bg = (ImageView) findViewById(R.id.video_videoview_pres_bg);
		img_center_speed = (ImageView) findViewById(R.id.video_img_center_speed);
		img_back = (ImageView) findViewById(R.id.video_img_back);

		video_VideoView = (FullVideoView) findViewById(R.id.video_VideoView);

		view = findViewById(R.id.video_frame);

		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();

		bottom_relative = (RelativeLayout) findViewById(R.id.video_relative_bottom);
		top_relative = (RelativeLayout) findViewById(R.id.video_relative_top);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		new Thread(runnable2).start();
		dialog = ProgressDialog.show(this, "视频加载中...", "请您稍候");
	}

	/**
	 * 监听事件
	 */
	private void widgetListener() {

		/**
		 * 播放
		 */
		img_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (video_VideoView.isPlaying()) {
					video_VideoView.pause();
					img_start.setImageResource(R.drawable.start_video_df);
				} else {
					mVideo_total_length = video_VideoView.getDuration();
					txt_max_time.setText(length2time(mVideo_total_length));
					isStart = true;
					video_VideoView.start();
					img_start.setImageResource(R.drawable.pause_video_df);
					handler.postAtTime(runnable, 0);
				}
			}
		});

		video_VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				dialog.dismiss();
				video_VideoView.start();
				mVideo_total_length = video_VideoView.getDuration();
				txt_max_time.setText(length2time(mVideo_total_length));
				isStart = true;
				handler.postAtTime(runnable, 0);
			}
		});

		img_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private Runnable runnable2 = new Runnable() {
		@Override
		public void run() {
			Message m = handler.obtainMessage();
			m.what = handKey;
			handler.sendMessage(m);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;

		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
				endGesture();
				break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 手势结束
	 */
	private void endGesture() {
		if (!isVideo) {
			if (top_relative.isShown()) {
				top_relative.setVisibility(View.GONE);
			} else {
				top_relative.setVisibility(View.VISIBLE);
			}
			if (bottom_relative.isShown()) {
				bottom_relative.setVisibility(View.GONE);
			} else {
				bottom_relative.setVisibility(View.VISIBLE);
			}
		}

		isVideo = false;

		oldMoveX = 0;
		oldMoveY = 0;

		img_center_speed.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
	}

	/**
	 * 设置Video该播放视频的准备
	 */
	private void setVideo() {
		try {
			Uri uri = Uri.parse(Common.HEAD_URL + subjectInfo.getFile_url());
			video_VideoView.setVideoURI(uri);
			video_VideoView.requestFocus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			mVideo_current_length = video_VideoView.getCurrentPosition();

			if (mVideo_current_length >= mVideo_total_length) {
				mVideo_current_length = mVideo_total_length;
			}

			txt_current_time.setText(length2time(mVideo_current_length));
			ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) img_white.getLayoutParams();
			lp.width = (int) (((float) img_bg.getWidth()) / mVideo_total_length *
					mVideo_current_length);
			img_white.setLayoutParams(lp);

			handler.postDelayed(runnable, 1000);

			if (mVideo_current_length >= mVideo_total_length) {
				handler.removeCallbacks(runnable);
			}
		}
	};

	/**
	 * 调节视频进度
	 */
	private void onVideoSpeed(float distanceX) {
		// 当前播放长度
		mVideo_current_length = video_VideoView.getCurrentPosition();

		if (distanceX > 0) {
			// 往左滑动
			img_center_speed.setVisibility(View.VISIBLE);
			img_center_speed.setImageResource(R.drawable.retreat_video);
			mVideo_current_length -= 1000;// 快进之后长度
		} else if (distanceX < 0) {
			// 往右滑动
			img_center_speed.setVisibility(View.VISIBLE);
			img_center_speed.setImageResource(R.drawable.speed_video);
			mVideo_current_length += 1000;// 快进之后长度
		}

		if (mVideo_current_length >= mVideo_total_length) {
			mVideo_current_length = mVideo_total_length;
		} else if (mVideo_current_length <= 0) {
			mVideo_current_length = 0;
		}

		video_VideoView.seekTo((int) mVideo_current_length);
		// 定位播放在哪个位置
		handler.postDelayed(runnable, 0);
	}

	/**
	 * 将进度长度转变为进度时间
	 */
	private String length2time(long length) {
		length /= 1000L;
		long minute = length / 60L;
		long hour = minute / 60L;
		long second = length % 60L;
		minute %= 60L;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		// 双击
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return true;
		}

		// 滑动
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			downX = e1.getX();
			downY = e1.getY();
			moveX = e2.getX();
			moveY = e2.getY();
			if (oldMoveX == 0) {
				oldMoveX = downX;
				oldMoveY = downY;
			}

			if (Math.abs(moveY = downY) < Math.abs(moveX - downX) && isStart) {
				handler.removeCallbacks(runnable);
				onVideoSpeed((oldMoveX - moveX) / width);
				oldMoveX = moveX;
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
}
