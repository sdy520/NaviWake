package com.example.naviwake.util;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

/**
 * 功能性函数扩展类
 */
public class FucUtil {

	/**
	 * 读取asset目录下文件。
	 * @return content
	 */
	public static String readFile(Context mContext,String file,String code)
	{
		int len = 0;
		byte []buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);			
			len  = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			
			result = new String(buf,code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取asset目录下音频文件。
	 * 
	 * @return 二进制文件数据
	 */
	public static byte[] readAudioFile(Context context, String filename) {
		try {
			InputStream ins = context.getAssets().open(filename);
			byte[] data = new byte[ins.available()];
			
			ins.read(data);
			ins.close();
			
			return data;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	private static final double EARTH_RADIUS = 6378.137;
	public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
		// 纬度
		double lat1 = Math.toRadians(latitude1);
		double lat2 = Math.toRadians(latitude2);
		// 经度
		double lng1 = Math.toRadians(longitude1);
		double lng2 = Math.toRadians(longitude2);
		// 纬度之差
		double a = lat1 - lat2;
		// 经度之差
		double b = lng1 - lng2;
		// 计算两点距离的公式
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
				Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
		// 弧长乘地球半径, 返回单位: 千米
		s =  s * EARTH_RADIUS;
		return s;
	}

	//删除文件夹和文件夹里面的文件
	public static void deleteDirWihtFile(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDirWihtFile(file); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}
	
}
