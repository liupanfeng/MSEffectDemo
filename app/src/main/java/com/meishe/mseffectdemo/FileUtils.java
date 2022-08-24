package com.meishe.mseffectdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FileUtils {

    public static boolean copyFileIfNeed(Context context, String fileName, String className) {
        String path = getFilePath(context, className + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                //如果模型文件不存在
                try {
                    if (file.exists())
                        file.delete();

                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    file.createNewFile();

                    InputStream in = context.getAssets().open(className + File.separator + fileName);
                    if (in == null) {
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static String getFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }
        return path;
    }

    public static List<String> copyStickerZipFiles(Context context, String className) {
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".zip") != -1) {
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static List<String> getStickerZipFilesFromSd(Context context, String className) {
        ArrayList<String> modelFiles = new ArrayList<String>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static String getStickerZipFileFromSd(Context context, String pathName) {
        ArrayList<String> modelFiles = new ArrayList<String>();

        String stickerpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            stickerpath = dataDir.getAbsolutePath() + File.separator + pathName;

            File zipFile = new File(stickerpath);

            if (!zipFile.exists()) {
                return null;
            }
        }
        return stickerpath;
    }

    public static Map<String, Bitmap> copyStickerIconFiles(Context context, String className) {
        String files[] = null;
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".png") != -1) {
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为png结尾
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static Map<String, Bitmap> getStickerIconFilesFromSd(Context context, String className) {
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为png结尾
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static List<String> getStickerNames(Context context, String className) {
        ArrayList<String> modelNames = new ArrayList<String>();
        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;
            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip") && filename.indexOf("filter") == -1) {
                    String name = subFile[i].getName();
                    modelNames.add(getFileNameNoEx(name));
                }
            }
        }

        return modelNames;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static final String getCaptureFilePath(final String ext) {
        File captureDir = new File(Environment.getExternalStorageDirectory(), "NvStreamingSdk" + File.separator + "Record");
        if (!captureDir.exists() && !captureDir.mkdirs()) {
            Log.e("TAG", "Failed to make Record directory");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return captureDir.getPath() + File.separator + "VIDEO_" + timeStamp + ext;
    }

    public static String readTextFileFromRawResource(final Context context, final int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }
}
