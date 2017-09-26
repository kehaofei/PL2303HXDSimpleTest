package com.dfey.cxy.listviewchoose.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leven on 2016/10/30.
 */

public class FileUtils {

    //删除文件夹中的所有文件和文件夹
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹  
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在  
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    //获取文件夹中的所有文件url（不包括文件夹）
    public static List<String> getAllFiles(String dirPath) {
        List<String> filePaths = new ArrayList<>();
        File carouselDir = new File(dirPath);
        System.out.println(carouselDir);

        File[] carouselFiles = carouselDir.listFiles();
        System.out.println("getAllFiles-carouselFiles:" + carouselFiles);
        if (carouselFiles != null) {

            if (carouselFiles != null && carouselFiles.length > 0) {
                for (int i = 0; i < carouselFiles.length; i++) {
                    if (carouselFiles[i].isFile()) {
                        filePaths.add(carouselFiles[i].getName());
                    }
                }
            }
        }
        return filePaths;
    }

    //把文件写入本地

    public static void writeFileToLocal(String path) {
        File file = new File(path);
        try {
            int bytesum = 0;
            int byteread = 0;
            if (file.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(file); //读入原文件
                FileOutputStream fs = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    //字符串、json 写入文件
    public static void writeStringToFile(String json, String filePath) {
        File txt = new File(filePath);
        if (!txt.exists()) {
            try {
                txt.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] bytes = json.getBytes(); //新加的
        int b = json.length(); //改
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(txt);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //bitmap转成jpg保存到本地
    public static void saveMyBitmap(Bitmap mBitmap, String filePath) {
        File f = new File(filePath);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            DebugMessage.put("在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *   
     *      * 复制整个文件夹内容  
     *      * @param oldPath String 原文件路径 如：c:/fqf  
     *      * @param newPath String 复制后路径 如：f:/fqf/ff  
     *      * @return boolean  
     *      
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹   
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹   
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 读取文本文件
     *  
     *  @param filePath
     *  @return
     *      
     */
    public static String readTextFile(String filePath) {
        String SDCardPATH = Environment.getExternalStorageDirectory() + "/";

        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(SDCardPATH + filePath);
            InputStream in = null;
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                sb.append((char) tempbyte);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
