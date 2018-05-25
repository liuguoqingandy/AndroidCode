package com.common.lib.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class FileUtils {

    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值
    private static final HashMap<String, String> mFileTypes = new HashMap<String, String>();
    public static final int BUFSIZE = 2048;
    private static final String TAG = FileUtils.class.getSimpleName();

    static {
        //images
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        //
        mFileTypes.put("41433130", "dwg"); //CAD
        mFileTypes.put("38425053", "psd");
        mFileTypes.put("7B5C727466", "rtf"); //日记本
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("44656C69766572792D646174653A", "eml"); //邮件
        mFileTypes.put("D0CF11E0", "doc");
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E", "pdf");
        mFileTypes.put("504B0304", "zip");
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");
        mFileTypes.put("", "");
        mFileTypes.put("", "");
    }

    /**
     * 读取源文件字符数组
     *
     * @param file 获取字符数组的文件
     * @return 字符数组
     */
    public static byte[] readFileByte(File file) {
        FileInputStream fis = null;
        FileChannel fc = null;
        byte[] data = null;
        try {
            fis = new FileInputStream(file);
            fc = fis.getChannel();
            data = new byte[(int) (fc.size())];
            fc.read(ByteBuffer.wrap(data));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return data;
    }

    /**
     * 字符数组写入文件
     *
     * @param bytes 被写入的字符数组
     * @param file  被写入的文件
     * @return 字符数组
     */
    public static boolean writeByteFile(byte[] bytes, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 复制文件
     *
     * @param srcFile
     * @param destFile
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        if (srcFile != null && srcFile.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(srcFile);
                result = copyToFile(in, destFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return result;
    }

    /**
     * 流存为文件
     *
     * @param inputStream
     * @param destFile
     * @return
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            } else if (destFile.exists()) {
                destFile.delete();
            }
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除目录以及子目录
     *
     * @param filepath         要删除的目录地址
     * @param isDeleteThisPath 是否删除该目录
     * @throws IOException
     */
    public static void deleteDir(String filepath, boolean isDeleteThisPath) {
        File f = new File(filepath);
        if (f.exists() && f.isDirectory()) {
            if (f.listFiles().length == 0) {
                if (isDeleteThisPath) {
                    f.delete();
                }
            } else {
                File[] delFiles = f.listFiles();
                for (File delFile : delFiles) {
                    if (delFile.isDirectory()) {
                        deleteDir(delFile.getAbsolutePath(), true);
                    }
                    delFile.delete();
                }
                if (isDeleteThisPath) {
                    f.delete();
                }
            }
        }
    }

    /**
     * 删除目录以及子目录
     *
     * @param filepath 要删除的目录地址
     * @throws IOException
     */
    public static void deleteDir(String filepath) {
        File f = new File(filepath);
        if (f.exists() && f.isDirectory()) {
            if (f.listFiles().length == 0) {
                f.delete();
            } else {
                File[] delFiles = f.listFiles();
                for (File delFile : delFiles) {
                    if (delFile.isDirectory()) {
                        deleteDir(delFile.getAbsolutePath(), true);
                    }
                    delFile.delete();
                }
                f.delete();
            }
        }
    }

    /**
     * 安全删除文件或文件夹方式;
     *
     * @param file 要删除的文件或文件夹；
     */

    public static void deleteFileSavely(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFileSavely(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }

    /**
     * 安全删除文件.
     *
     * @param file 要删除的文件；
     * @return
     */
    private static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    /**
     * 改名
     *
     * @param src
     * @param dst
     * @return
     */
    public static boolean safeRenameTo(File src, File dst) {
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        boolean ret = src.renameTo(dst);
        if (!ret) {
            ret = copyFile(src, dst);
            if (ret) {
                if (src.isDirectory()) {
                    deleteDir(src.getAbsolutePath());
                } else {
                    src.delete();
                }
            }
        }
        return ret;
    }

    /**
     * 文件夹下的文件个数
     *
     * @param aDir
     * @return
     */
    public static long countDirSize(File aDir) {
        long ret = 0L;
        File[] files = aDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                ret += countDirSize(file);
            } else {
                ret += file.length();
            }
        }
        return ret;
    }

    public static void deleteFiles(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                if (file.listFiles().length > 0) {
                    File[] delFiles = file.listFiles();
                    for (File delFile : delFiles) {
                        deleteFiles(delFile);
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * 将文件读取为String
     *
     * @param path
     * @param charsetName
     * @return
     */
    public static String readFile(String path, String charsetName) {
        StringBuffer contents = new StringBuffer("");
        try {
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(
                    urlFile), charsetName);
            @SuppressWarnings("resource")
            BufferedReader breader = new BufferedReader(isr);
            String mimeTypeLine = null;
            while ((mimeTypeLine = breader.readLine()) != null) {
                contents.append(mimeTypeLine);
            }
            breader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents.toString();
    }

    /**
     * 从工程的asset中读取文件
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 返回文件中的内容(字符串)
     */
    public static String readFileFromAssets(Context context, String fileName) {
        String contents = "";
        try {
            contents = readFileFromAssets(context,
                    context.getResources().getAssets().open(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }

    /**
     * 从工程的asset中读取文件
     *
     * @param context 上下文
     * @param fileIns 文件的内容(InputStream)
     * @return 返回文件中的内容(字符串)
     */
    public static String readFileFromAssets(Context context, InputStream fileIns) {
        StringBuffer contents = new StringBuffer("");
        try {
            InputStreamReader isr = new InputStreamReader(fileIns);
            BufferedReader breader = new BufferedReader(isr);
            String mimeTypeLine = null;
            while ((mimeTypeLine = breader.readLine()) != null) {
                contents.append(mimeTypeLine);
            }
            breader.close();
            if (isr != null)
                isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents.toString();
    }

    /**
     * 判断是否有这个文件
     *
     * @param str
     * @return
     */
    public static boolean fileIsExists(String str) {
        try {
            File f = new File(str);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            Log.e("获取文件大小", "获取失败!", e);
        }
        return formatFileSize(blockSize, sizeType);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        } else {
//            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.0");
        double fileSizeLong = 0;

        if (fileS == 0) {
            return fileSizeLong;
        }
        try {
            switch (sizeType) {
                case SIZETYPE_B:
                    fileSizeLong = Double.valueOf(df.format((double) fileS));
                    break;
                case SIZETYPE_KB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                    break;
                case SIZETYPE_MB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                    break;
                case SIZETYPE_GB:
                    fileSizeLong = Double.valueOf(df
                            .format((double) fileS / 1073741824));
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
        return fileSizeLong;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + " B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + " KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + " GB";
        }
        return fileSizeString;
    }

    public static String[] fileTypes = new String[]{"apk", "avi", "bmp", "chm", "dll", "doc", "docx", "dos", "gif", "html", "jpeg", "jpg", "movie", "mp3", "dat", "mp4", "mpe", "mpeg", "mpg", "pdf", "png", "ppt", "pptx", "rar", "txt", "wav", "wma", "wmv", "xls", "xlsx", "xml", "zip"};
    public static final int FILE_INVALID = 401;

    public FileUtils() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathFromURI(Context context, Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String displayName = null;
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                    filePath = cursor.getString(column_index);
                    displayName = cursor.getString(cursor.getColumnIndexOrThrow(filePathColumn[1]));
                }
            } catch (Exception e) {
                Log.e(TAG, "getFileByUri(Context, Uri): failed to read cursor", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // We have to copy out the content for after KITKAT
                try {
                    Random rand = new Random();
                    rand.setSeed(System.nanoTime());
                    String randString = Integer.toString(rand.nextInt(100000000));
                    String filename = displayName == null ? "attach-" + randString : displayName;
                    File tmpDir = new File(context.getCacheDir(), randString);
                    if (tmpDir.mkdirs()) {
                        tmpDir.deleteOnExit();
                        File tempFile = new File(tmpDir, filename);
                        tempFile.deleteOnExit();
                        InputStream in = context.getContentResolver().openInputStream(uri);
                        saveStreamToPath(in, tempFile.getPath());
                        filePath = tempFile.getPath();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "getFileByUri(Context, Uri): failed to create temp file", e);
                } catch (Exception e) {
                    Log.e(TAG, "Unknown error", e);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context, "File not exist", Toast.LENGTH_SHORT).show();
            return null;
        }
//        //limit the size < 10M
//        if (file.length() > 10 * 1024 * 1024) {
//            Toast.makeText(context, "The file is not greater than 10 m", Toast.LENGTH_SHORT).show();
//            return null;
//        }
        return filePath;
    }

    public static File[] loadFiles(File var0) {
        File[] var1 = var0.listFiles();
        if (var1 == null) {
            var1 = new File[0];
        }

        ArrayList var2 = new ArrayList();
        ArrayList var3 = new ArrayList();
        File[] var4 = var1;
        int var5 = var1.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            File var7 = var4[var6];
            if (var7.isDirectory()) {
                var2.add(var7);
            } else if (var7.isFile()) {
                var3.add(var7);
            }
        }

        MyComparator var8 = new MyComparator();
        Collections.sort(var2, var8);
        Collections.sort(var3, var8);
        File[] var9 = new File[var2.size() + var3.size()];
        System.arraycopy(var2.toArray(new File[var2.size()]), 0, var9, 0, var2.size());
        System.arraycopy(var3.toArray(new File[var3.size()]), 0, var9, var2.size(), var3.size());
        return var9;
    }

    public static String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    public static String getMIMEType(String var0) {
        String var1 = "";
        String var2 = var0.substring(var0.lastIndexOf(".") + 1, var0.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var2);
        return var1;
    }

    public static String getFileSuffix(String var0) {
        String var2 = "";
        try {
            var2 = var0.substring(var0.lastIndexOf(".") + 1, var0.length()).toLowerCase();
        } catch (Exception e) {
            return "null";
        }
        return var2;
    }

    public static void openFile(File var0, Activity var1) {
        if (var1 == null || var0 == null)
            return;
        Intent var2 = new Intent();
        var2.addFlags(FLAG_ACTIVITY_NEW_TASK);
        var2.setAction("android.intent.action.VIEW");
        String var3 = getMIMEType(var0);
        var2.setDataAndType(Uri.fromFile(var0), var3);

        try {
            var1.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var1, "Can\'t find proper app to open this file", Toast.LENGTH_LONG).show();
        }

    }

    public static void openFile(Uri var0, String var1, Activity var2) {
        Intent var3 = new Intent();
        var3.addFlags(FLAG_ACTIVITY_NEW_TASK);
        var3.setAction("android.intent.action.VIEW");
        var3.setDataAndType(var0, var1);

        try {
            var2.startActivity(var3);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var2, "Can\'t find proper app to open this file", Toast.LENGTH_LONG).show();
        }

    }

    public static synchronized void saveObjectToFile(Object var0, File var1) throws Exception {
        ObjectOutputStream var2 = new ObjectOutputStream(new FileOutputStream(var1));
        var2.writeObject(var0);
        var2.flush();
        var2.close();
    }

    public static synchronized Object readObjectFromFile(File var0) throws Exception {
        ObjectInputStream var1 = new ObjectInputStream(new FileInputStream(var0));
        return var1.readObject();
    }

    public static class MyComparator implements Comparator<File> {
        public MyComparator() {
        }

        public int compare(File var1, File var2) {
            return var1.getName().compareTo(var2.getName());
        }
    }


    public static String getFileType(String filePath) {
        return mFileTypes.get(getFileHeader(filePath));
    }

    //获取文件头信息
    private static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }


    public static boolean prepareDir(String filePath) {
//		if (!filePath.endsWith(File.separator)) {
//			return false;
//		}
        File file = new File(filePath);
        if (file.exists() || file.mkdirs()) {
            Logger.d(TAG, "prepareDir_create folder:" + filePath + ",result:true");
            return true;
        } else {
            Logger.d(TAG, "prepareDir_create folder:" + filePath + ",result:false");
            return false;
        }
    }

    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile.exists() == false) {
            prepareDir(parentFile.getAbsolutePath());
        }
        try {
            boolean result = file.createNewFile();
            Logger.i("createFile", "create folder:" + filePath + ",result:" + result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Logger.i("createFile", "create folder:" + filePath + ",result:false");
            return false;
        }
    }

    public static String getFileSize(long lvalue) {
        float ftmp = (float) lvalue;
        if (lvalue >= 1048576) {
            // float res = lvalue / 1048576;
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.00 ");
            return String.valueOf(df.format(ftmp / 1048576)) + "M";
        } else if (lvalue < 1048576 && lvalue > 1024) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.00 ");
            return String.valueOf(df.format(ftmp / 1024)) + "K";
        } else if (lvalue > 0 && lvalue <= 1024) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00 ");
            return String.valueOf(df.format(ftmp / 1024)) + "K";
        } else {
            return "0.00K";
        }
    }

    public static String getFileSize1(long lvalue) {
        float ftmp = (float) lvalue;
        // float res = lvalue / 1048576;
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00 ");
        return String.valueOf(df.format(ftmp / 1048576)) + "M";
    }

    /**
     * 将带file://的路径从/sdcard开始截取
     *
     * @param path
     * @return
     */
    public static String formatPath(String path) {
        if (path.indexOf("/sd") > -1) {
            return path.substring(path.indexOf("/sd"));
        }
        return path;
    }

    public static byte[] fileToByteArray(Context context, String path) {
        InputStream is = null;
        byte[] data = null;
        try {
            File file = null;
            if (ContentResolver.SCHEME_CONTENT.equals(Uri.parse(path).getScheme())) {
                ContentResolver cr = context.getContentResolver();
                Uri imageUri = Uri.parse(path);
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = cr.query(imageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                file = new File(cursor.getString(column_index));
                if (!SDKVersionUtil.hasICS()) {
                    cursor.close();
                }
            } else if (ContentResolver.SCHEME_FILE.equals(Uri.parse(path).getScheme())) {
                file = new File(Uri.parse(path).getPath());
            } else {
                file = new File(path);
            }
            is = new FileInputStream(file);
            data = new byte[is.available()];
            int i = 0;
            int temp = 0;
            while ((temp = is.read()) != -1) {
                data[i] = (byte) temp;
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static File getFile(Uri uri) {
        if (uri != null) {
            String filepath = uri.getPath();
            if (filepath != null) {
                return new File(filepath);
            }
        }
        return null;
    }

    public static File getFile(String curdir, String file) {
        String separator = "/";
        if (curdir.endsWith("/")) {
            separator = "";
        }
        File clickedFile = new File(curdir + separator + file);
        return clickedFile;
    }

    public static File contentUriToFile(Context context, Uri uri) {
        File file = null;
        if (uri != null) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = ((Activity) context).managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            if (!SDKVersionUtil.hasICS()) {
                if (actualimagecursor != null) {
                    actualimagecursor.close();
                }
            }
            file = new File(img_path);
        }
        return file;
    }

    public static InputStream getFileInputStream(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return is;
    }

    public static boolean deleteFile(File file) {
        boolean delete = false;
        try {
            delete = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delete;
    }

    public static boolean deleteFile(String path) {
        boolean delete = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                delete = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delete;
    }

    public static void delete(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        if (f.isDirectory()) {
                            delete(f.getAbsolutePath());
                        } else {
                            f.delete();
                        }
                    }
                } else {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一组文件 (e.g. 一个大于30s的AMR被分割解析完成后在删除)
     *
     * @param delFilePaths 一组待删除的文件的路径
     * @return
     */
    public static boolean deleteFileArrays(String[] delFilePaths) {
        if (delFilePaths != null) {
            File delFile = null;
            for (String delFilePath : delFilePaths) {
                delFile = new File(delFilePath);
                if (delFile.exists()) {
                    return delFile.delete();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static File getFile(File curdir, String file) {
        return getFile(curdir.getAbsolutePath(), file);
    }

    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    public static byte[] getByteArrayByFile(File file) {
        // modify by gaotong not The entire file into memory
        BufferedInputStream stream = null;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outsteam = null;
        try {
            FileInputStream in = new FileInputStream(file);
            stream = new BufferedInputStream(in);
            outsteam = new ByteArrayOutputStream();
            while (stream.read(buffer) != -1) {
                outsteam.write(buffer);
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                    outsteam.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return outsteam.toByteArray();
    }

    /**
     * @param f    - 指定的目录
     * @param buff
     */
    public static void saveByteToFile(File f, byte[] buff) {
        FileOutputStream fOut = null;
        try {
            if (buff != null && buff.length != 0) {
                if (f.exists()) {
                    f.delete();
                }
                f.createNewFile();
                fOut = new FileOutputStream(f);
                fOut.write(buff);
                fOut.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveByteToPath(String path, ArrayList<byte[]> buffs) {
        FileOutputStream fOut = null;
        File f = new File(path);
        try {
            if (!f.exists()) {
                createFile(path);
                f = new File(path);
            }
            fOut = new FileOutputStream(f);
            for (int i = 0; i < buffs.size(); i++) {
                fOut.write(buffs.get(i));
            }
            fOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveByteToFile(File f, ArrayList<byte[]> buffs) {
        FileOutputStream fOut = null;
        try {
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            fOut = new FileOutputStream(f);
            for (int i = 0; i < buffs.size(); i++) {
                fOut.write(buffs.get(i));
            }
            fOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存图片到SDCard的默认路径下. 屏刷新图库
     *
     * @throws IOException
     */
    public static void saveByteToSDCard(Context context, File f, byte[] buff) throws IOException {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            fOut.write(buff);
            fOut.flush();
            refreshAlbum(context, f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存图片到data路径下
     */
    public static void saveByteToData(Context context, Bitmap bitmap, String fileName) throws IOException {
        try {
            File file = new File(fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveStreamToPath(InputStream is, String path) {
        FileOutputStream fOut = null;
        File f = new File(path);
        try {
            if (!f.exists()) {
                createFile(path);
                f = new File(path);
            }
            fOut = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fOut.write(buffer, 0, len);
            }
            fOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    is.close();
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void refreshAlbum(Context context, File imageFile) {
        if (context != null) {
            Uri localUri = Uri.fromFile(imageFile);
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            context.sendBroadcast(localIntent);
        }
    }

    public static void refreshAlbum(Context context, Uri localUri) {
        if (context != null) {
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            context.sendBroadcast(localIntent);
        }
    }

    public static void copyFile(String fileFromPath, String fileToPath) throws Exception {
        if (!fileFromPath.equals(fileToPath)) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(fileFromPath);
                out = new FileOutputStream(fileToPath);
                int length = in.available();
                int len = (length % BUFSIZE == 0) ? (length / BUFSIZE) : (length / BUFSIZE + 1);
                byte[] temp = new byte[BUFSIZE];
                for (int i = 0; i < len; i++) {
                    in.read(temp);
                    out.write(temp);
                }
            } finally {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
        }
    }

    /**
     * 将指定的输入流读到指的文件路径下,不关闭输入流
     *
     * @param inputStream
     * @param destFilePath
     * @return
     */
    public static boolean copyFile(InputStream inputStream, String destFilePath) {
        int bufferSize = 8 * 1024;
        OutputStream out = null;
        try {
            out = new FileOutputStream(destFilePath);
            byte[] buffer = new byte[bufferSize];
            int reacCount = 0;
            while ((reacCount = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, reacCount);
            }
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFile(File file) {
        String data = "";
        try {
            FileInputStream stream = new FileInputStream(file);
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = stream.read()) != -1) {
                sb.append((char) c);
            }
            stream.close();
            data = sb.toString();
        } catch (FileNotFoundException e) {
            Logger.d(TAG, "readFile(File): file not exist." + file.getPath());
        } catch (IOException e) {
            Logger.d(TAG, "readFile(File): IOE", e);
        }
        return data;
    }

    public static String ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        String content = ""; // 文件内容字符串
        // 打开文件
        File file = new File(path);
        // 如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Logger.d("TestFile", "ReadTxtFile(String): File is dir:" + file.getPath());
        } else {
            InputStream instream = null;
            try {
                instream = new FileInputStream(file);
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                // 分行读取
                while ((line = buffreader.readLine()) != null) {
                    content += line + "\n";
                }
            } catch (FileNotFoundException e) {
                Logger.d(TAG, "ReadTxtFile(String): File not exist:" + file.getPath());
            } catch (IOException e) {
                Logger.d(TAG, "ReadTxtFile(String): IOE", e);
            } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (IOException e) {
                        Logger.d(TAG, "ReadTxtFile(String): ioe closing", e);
                    }
                }
            }
        }
        return content;
    }

    /**
     * 按最大spliTime时长分割一个时长大于30s的amr音频文件为多份.
     *
     * @param sourceFilePath 被分割的文件的全路径
     * @param amrTotalTimes  被分割的源文件的总时长
     * @param spliTime       分割后的文件最大时长
     * @return 分割后的所有文件全路径 组成的数组
     * @throws IOException
     */
    public static String[] splitAmrFileByTime(Context context, String sourceFilePath, int amrTotalTimes, int spliTime) throws IOException {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists() || sourceFile.isDirectory()) {
            throw new IOException("No SD Card");
        }

        // 取得文件的大小
        long fileLength = sourceFile.length();
        Logger.d(TAG, "splitAmrFileByTime 源文件大小: " + fileLength);

        // 获得被分割文件父文件，将来被分割成的小文件就存放在这个目录下
        File parentFile = sourceFile.getParentFile();
        // amr 头文件信息
        byte[] header = ("#!AMR\n").getBytes();
        // 取得分割后的小文件的数目
        int fileNum = (amrTotalTimes + spliTime - 1) / spliTime;

        // 存放分割后的小文件名
        String[] outFileNames = new String[fileNum];

        // XXX 整个amr音频转换为byte, 有可能OOM
        byte[] sourceFileByte = getByteArrayByFile(sourceFile);

        // 计算分割后的每个文件的byte大小
        int singleFileSize = sourceFileByte.length * spliTime / amrTotalTimes;
        Logger.d(TAG, "splitAmrFileByTime 每个byte长度  " + singleFileSize);

        // 根据要分割的数目分割文件
        File outFile = null;
        FileOutputStream fos = null;
        byte[] singleCutFileBytes = null;    //切歌后的每个amr文件的byte数组

        for (int outFileIndex = 0; outFileIndex < fileNum; outFileIndex++) {
            // 对于前outFileIndex-1个文件，大小都是size
            outFile = new File(parentFile, sourceFile.getName() + "_" + outFileIndex + ".amr");
            fos = new FileOutputStream(outFile);
            // 非第一个文件写入头文件信息
            if (outFileIndex != 0) {
                fos.write(header);
            }
            // 计算分割后的每个byte
            int offset = outFileIndex * singleFileSize;
            int end = offset + singleFileSize;

            if (end > sourceFileByte.length) {
                end = sourceFileByte.length;
            }
            Logger.d(TAG, "每个byte起始,结束位置  " + offset + "------" + end);

            singleCutFileBytes = new byte[end - offset];
            int x = 0;
            for (; offset < end; offset++) {
                singleCutFileBytes[x] = sourceFileByte[offset];
                x++;
            }

            fos.write(singleCutFileBytes);
            outFileNames[outFileIndex] = outFile.getAbsolutePath();
        }

        outFile = null;
        sourceFileByte = null;
        singleCutFileBytes = null;

        fos.close();
        return outFileNames;
    }

    /**
     * 将字节缓冲区按照固定大小进行分割成数组
     *
     * @param buffer 缓冲区
     * @param length 缓冲区大小
     * @param spsize 切割块大小
     * @return
     */
    public static ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        if (spsize <= 0 || length <= 0 || buffer == null || buffer.length < length)
            return array;
        int size = 0;
        while (size < length) {
            int left = length - size;
            if (spsize < left) {
                byte[] sdata = new byte[spsize];
                System.arraycopy(buffer, size, sdata, 0, spsize);
                array.add(sdata);
                size += spsize;
            } else {
                byte[] sdata = new byte[left];
                System.arraycopy(buffer, size, sdata, 0, left);
                array.add(sdata);
                size += left;
            }
        }
        return array;
    }


    /**
     * 获取包对应的临时文件目录，优先级sdCache > 内置内存cache
     */
    public static File getDiskCacheDir(Context context) {
        File cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cachePath = context.getExternalCacheDir();
        } else {
            cachePath = context.getCacheDir();
        }
        return cachePath;
    }

    public static File getDiskFileDir(Context context, String type) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = context.getExternalFilesDir(type);
        } else {
            file = new File(context.getFilesDir().getAbsolutePath() + "/" + type + "/");
        }
        if (file != null && !file.exists()) file.mkdirs();
        return file;
    }
}

