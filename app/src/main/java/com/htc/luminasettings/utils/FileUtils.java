package com.htc.luminasettings.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年7月4日 下午4:07:19 类说明
 */
public class FileUtils {

	/**
	 * 删除单个文件
	 * 
	 * @param filePath
	 *            被删除文件的文件名
	 * @return 文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}

	/**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public  boolean deleteDirectory(String filePath) {
    boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
            //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
            //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

   /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String filePath) {
    File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
            // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
            // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }
    
    /**
	 * 复制单个文件
	 * 
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		
		boolean isCopy=false;
		
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					//System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				isCopy=true;
			}
		} catch (Exception e) {
			//System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}
		return isCopy;
	}

	public static String readFileContent(String path){
		File file = new File(path);
		if (!file.exists())
			return null;

		try {
			StringBuilder fsb = new StringBuilder();
			InputStream stream = new FileInputStream(file);
			String content;
			InputStreamReader reader = new InputStreamReader(stream,"UTF-8");
			BufferedReader buffReader = new BufferedReader(reader);
			while((content = buffReader.readLine())!=null){
				if (content.contains("##")){
					//LogUtils.d("content",content);
					continue;
				}
				fsb.append(content);
			}
			buffReader.close();
			//LogUtils.d(TAG,"readFileContent "+fsb.toString());
			return fsb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 写内容
	 * @param logPath
	 * @param content
	 */
	public static void writeContent(String logPath, String content) {
		if (TextUtils.isEmpty(content)) {
			return;
		}
		File file = new File(logPath);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.flush();
			LogUtils.d("test","write finish");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

//	public static Drawable loadImageAsDrawable(Context context, String filePath){
//		FileInputStream fileInputStream = null;
//		try {
//			// 读取图像文件
//			File file = new File(filePath);
//			if(!file.exists()){
//				return null;
//			}
//			fileInputStream = new FileInputStream(file);
//			Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
//
//			// 转换为Drawable
//			Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
//			return drawable;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			if (fileInputStream != null) {
//				try {
//					fileInputStream.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	public static Drawable loadImageAsDrawable(Context context, String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		if (bitmap == null) {
			return null;
		}
		return new BitmapDrawable(context.getResources(), bitmap);
	}

}
