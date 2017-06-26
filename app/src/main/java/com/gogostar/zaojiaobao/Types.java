package com.gogostar.zaojiaobao;

/**
 * Created by Administrator on 2017/2/15.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 课程信息
 */
class SubjectInfo implements Parcelable, Serializable {

	// 课程id
	private int id;
	private int category_id;
	// 课程名字
	private String name;
	// 课程展示配置表封面图URL
	private String icon_url;
	private String file_url;
	private int type;
	private String introduction;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setIcon_url(String video_img) {
		this.icon_url = video_img;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public void setName(String title) {
		this.name = title;
	}

	public String getName() {
		return name;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	public String getFile_url() {
		return file_url;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getIntroduction() {
		return introduction;
	}

	public static final Creator<SubjectInfo> CREATOR = new Creator<SubjectInfo>() {
		@Override
		public SubjectInfo createFromParcel(Parcel source) {
			SubjectInfo subjectInfo = new SubjectInfo();

			subjectInfo.id = source.readInt();
			subjectInfo.category_id = source.readInt();
			subjectInfo.name = source.readString();
			subjectInfo.icon_url = source.readString();
			subjectInfo.file_url = source.readString();
			subjectInfo.introduction = source.readString();
			subjectInfo.type = source.readInt();

			return subjectInfo;
		}

		@Override
		public SubjectInfo[] newArray(int size) {
			return new SubjectInfo[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(category_id);
		dest.writeString(name);
		dest.writeString(icon_url);
		dest.writeString(file_url);
		dest.writeString(introduction);
		dest.writeInt(type);
	}
}

