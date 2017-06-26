package com.gogostar.zaojiaobao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 *
 * GridView适配器
 */

public class GridViewAdapter extends BaseAdapter {

	private Context context;
	private List<SubjectInfo> list;
	private MyBitmapUtils myBitmapUtils = new MyBitmapUtils();

	public GridViewAdapter(Context context, List<SubjectInfo> imageViews) {
		this.list = imageViews;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
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

		SubjectInfo subjectInfo = list.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.img_gv_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			// 三级缓存
			myBitmapUtils.display(viewHolder.iv, subjectInfo.getIcon_url());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView iv;
	}
}
