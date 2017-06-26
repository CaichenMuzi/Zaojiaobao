package com.gogostar.zaojiaobao;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class Mp3Activity extends BaseActivity {

	private MediaPlayer mediaPlayer;

	private List<SubjectInfo> subjectInfoList;
	private int selectedIndex;

	private ListView mListView;
	private Mp3ListViewAdapter listViewAdapter;

	private ImageView img_logo, img_pause, img_replay, img_back, img_yy;
	private TextView tv_name;

	private Player player;
	private SeekBar musicProgress;

	private ObjectAnimator animator;
	private MyAnimatorUpdateListener updateListener = new MyAnimatorUpdateListener();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					String name = (String) msg.obj;
					tv_name.setText(name);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mp3);

		mediaPlayer = new MediaPlayer();
		subjectInfoList = (List<SubjectInfo>) getIntent().getSerializableExtra("SubList");
		selectedIndex = getIntent().getIntExtra("index", -1);

		mListView = (ListView) findViewById(R.id.lv_mp3);
		img_logo = (ImageView) findViewById(R.id.img_mp3_logo);
		img_pause = (ImageView) findViewById(R.id.img_mp3_pause);
//		img_replay = (ImageView) findViewById(R.id.img_mp3_replay);
		img_back = (ImageView) findViewById(R.id.img_mp3_back);
		tv_name = (TextView) findViewById(R.id.tv_mp3_name);
		img_yy = (ImageView) findViewById(R.id.img_mp3_yy);

		musicProgress = (SeekBar) findViewById(R.id.music_progress);
		player = new Player(musicProgress);

		animator = ObjectAnimator.ofFloat(img_logo, "rotation", 0, 359);
		animator.setRepeatCount(-1);
		animator.setDuration(5000);
		animator.addUpdateListener(updateListener);

		musicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			int progress;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				player.mediaPlayer.seekTo(progress);
			}
		});

		img_logo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				player.pause();
				img_pause.setVisibility(View.VISIBLE);
				img_logo.setClickable(false);
				updateListener.pause();
				img_yy.setImageResource(R.drawable.yy_11);
			}
		});

		img_pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player != null && !player.mediaPlayer.isPlaying()) {
					player.play();
					img_pause.setVisibility(View.GONE);
					img_logo.setClickable(true);
					updateListener.play();
					img_yy.setImageResource(R.drawable.animated_rocket);
				}
			}
		});

		img_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		initListView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (player != null) {
			player.stop();
			player = null;
		}
	}

	private void initListView() {
		listViewAdapter = new Mp3ListViewAdapter(this, subjectInfoList);
		mListView.setAdapter(listViewAdapter);
		listViewAdapter.setSelectIndex(selectedIndex);
		listViewAdapter.notifyDataSetChanged();
		new Thread(new Runnable() {
			@Override
			public void run() {
				player.playUrl(Common.HEAD_URL + subjectInfoList.get(selectedIndex).getFile_url());
				mHandler.obtainMessage(1, subjectInfoList.get(selectedIndex).getName())
						.sendToTarget();
			}
		}).start();

		animator.start();
		updateListener.play();

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long
					id) {
				selectedIndex = position;
				listViewAdapter.setSelectIndex(selectedIndex);
				listViewAdapter.notifyDataSetChanged();
				final SubjectInfo subjectInfo = subjectInfoList.get(selectedIndex);
				new Thread(new Runnable() {
					@Override
					public void run() {
						player.playUrl(Common.HEAD_URL + subjectInfo.getFile_url());
						mHandler.obtainMessage(1, subjectInfo.getName()).sendToTarget();
					}
				}).start();
				animator.start();
				updateListener.play();
			}
		});

		player.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				animator.end();
				animator.start();
				updateListener.play();
				if (selectedIndex < subjectInfoList.size() - 1 && selectedIndex >= 0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							player.playUrl(Common.HEAD_URL + subjectInfoList.get(selectedIndex + 1)
									.getFile_url());
							mHandler.obtainMessage(1, subjectInfoList.get(selectedIndex + 1)
									.getName()
							).sendToTarget();
						}
					}).start();
					listViewAdapter.setSelectIndex(selectedIndex+1);
					listViewAdapter.notifyDataSetChanged();
				} else if (selectedIndex == subjectInfoList.size() - 1) {
					selectedIndex = 0;
					new Thread(new Runnable() {
						@Override
						public void run() {
							player.playUrl(Common.HEAD_URL + subjectInfoList.get(selectedIndex)
									.getFile_url());
							mHandler.obtainMessage(1, subjectInfoList.get(selectedIndex)
									.getName()
							).sendToTarget();
						}
					}).start();
					listViewAdapter.setSelectIndex(selectedIndex);
					listViewAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	/**
	 *
	 */
	class MyAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
		private boolean isPause = false;

		private boolean isPaused = false;

		private float fraction = 0.0f;

		private long mCurrentPlayTime = 0l;

		public boolean isPause() {
			return isPause;
		}

		public void play() {
			isPause = true;
		}

		public void pause() {
			isPause = false;
			isPaused = false;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			if (!isPause) {
				if (!isPaused) {
					mCurrentPlayTime = animation.getCurrentPlayTime();
					fraction = animation.getAnimatedFraction();
					animation.setInterpolator(new TimeInterpolator() {
						@Override
						public float getInterpolation(float input) {
							return fraction;
						}
					});
					isPaused = true;
				}

				new CountDownTimer(ValueAnimator.getFrameDelay(), ValueAnimator.getFrameDelay()) {
					@Override
					public void onTick(long millisUntilFinished) {
					}

					@Override
					public void onFinish() {
						animator.setCurrentPlayTime(mCurrentPlayTime);
					}
				}.start();
			} else {
				animation.setInterpolator(null);
			}
		}
	}
}
