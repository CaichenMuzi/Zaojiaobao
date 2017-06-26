package com.gogostar.zaojiaobao;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class Mp3ListViewAdapter extends BaseAdapter {

	private List<SubjectInfo> subList;
	private Context context;
	private int selectIndex = 0;

	public Mp3ListViewAdapter(Context context, List<SubjectInfo> imageViews) {
		this.context = context;
		this.subList = imageViews;
	}

	@Override
	public int getCount() {
		return subList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		SubjectInfo subjectInfo = subList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.mp3_listview_item, null);
			viewHolder.tv = (TextView) convertView.findViewById(R.id.tv_mp3_item_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position == selectIndex) {
			convertView.setSelected(true);
			convertView.setBackgroundResource(R.drawable.on);
			viewHolder.tv.setTextColor(Color.BLACK);
		} else {
			convertView.setSelected(false);
			convertView.setBackgroundResource(R.drawable.off);
			viewHolder.tv.setTextColor(Color.GRAY);
		}
		viewHolder.tv.setText(subjectInfo.getName());

		return convertView;
	}

	private class ViewHolder {
		TextView tv;
	}

	/**
	 * 定义设置选中的下标的方法
	 *
	 * @param i
	 */
	public void setSelectIndex(int i) {
		selectIndex = i;
	}
}
