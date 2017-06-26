package com.gogostar.zaojiaobao;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/6/1.
 * <p>
 * 全屏播放器
 */

public class FullVideoView extends VideoView {

	public FullVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FullVideoView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public FullVideoView(Context context) {
		super(context);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
