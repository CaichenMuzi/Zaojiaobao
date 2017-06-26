package com.gogostar.zaojiaobao;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2017/5/26.
 */

public class DownloadTask extends AsyncTask<Void, Integer, String> {

	private final String TAG = "DownloadTask";
	private URL mUrl;
	private File mFile;
	private ProgressDialog mDialog;
	private int mProgress = 0;
	private ProgressReportingOutputStream mOutputStream;
	private Context mContext;

	public DownloadTask(String url, String out, Context context, String fileName) {
		super();
		if (context != null) {
			mDialog = new ProgressDialog(context);
			mContext = context;
		} else {
			mDialog = null;
		}

		try {
			mUrl = new URL(url);
			String fName = new File(mUrl.getFile()).getName();
			String prefix = fName.substring(fName.lastIndexOf("."));
			mFile = new File(out, fileName + prefix);
			File file = new File(out);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPreExecute() {
		if (mDialog != null) {
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(false);
			mDialog.show();
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		return downloadFile();
	}

	@Override
	protected void onPostExecute(String result) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if (isCancelled()) {
			return;
		}
		String contextStr = mContext.toString();
		String contextName = contextStr.substring(contextStr.lastIndexOf(".") + 1, contextStr
				.indexOf("@"));
		if (contextName.equals("MainActivity")) {
			((MainActivity) mContext).installApp(mFile);
		} else if (contextName.equals("MediaInfoActivity")) {
			((MediaInfoActivity) mContext).openApp(mFile);
		}
	}

	private String downloadFile() {
		URLConnection connection = null;
		try {
			connection = mUrl.openConnection();
			int length = connection.getContentLength();
			if (mFile.exists() && length == mFile.length()) {
				Log.d(TAG, "file " + mFile.getName() + " already exits!!");
				return mFile.getAbsolutePath();
			}
			mOutputStream = new ProgressReportingOutputStream(mFile);
			publishProgress(0, length);
			copy(connection.getInputStream(), mOutputStream);
			mOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mFile.getAbsolutePath();
	}

	/**
	 * 自定义复制文件方法
	 *
	 * @param inputStream  输入流
	 * @param outputStream 输出流
	 */
	private void copy(InputStream inputStream, OutputStream outputStream) {
		byte[] buffer = new byte[1024 * 8];
		// 指定文件带缓冲区的读取流且指定缓冲区大小为8KB
		BufferedInputStream in = new BufferedInputStream(inputStream, 1024 * 8);
		int n = 0;
		try {
			// 读取文件
			while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
				outputStream.write(buffer, 0, n);
			}
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 自定义文件输出流
	 */
	private final class ProgressReportingOutputStream extends FileOutputStream {

		/**
		 * 构造函数
		 *
		 * @param file
		 * @throws FileNotFoundException
		 */
		public ProgressReportingOutputStream(File file) throws FileNotFoundException {
			super(file);
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
			super.write(buffer, byteOffset, byteCount);
			mProgress += byteCount;
			publishProgress(mProgress);// 更新进度条
		}
	}
}
