package com.common.lib.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.RelativeLayout.LayoutParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

	/**
	 * 这个是等比例缩放
	 */
	public static byte[] getZoomImage(Context context, Bitmap sourceBitmap, int widthLimit, int heightLimit, boolean isThumbnail, int quality) {
		InputStream input = null;
		try {
			int sourceWidth = sourceBitmap.getWidth();
			int sourceHeight = sourceBitmap.getHeight();
			float scale = 1f;
			if (isThumbnail) {
				if (sourceWidth > sourceHeight) {
					scale = widthLimit * 1.0f / sourceWidth;
					if (scale * sourceHeight > heightLimit) {
						scale = heightLimit * 1.0f / sourceHeight;
					}
				}
				else {
					scale = heightLimit * 1.0f / sourceHeight;
					if (scale * sourceWidth > widthLimit) {
						scale = widthLimit * 1.0f / sourceWidth;
					}
				}
			}
			else {
				if (sourceWidth > sourceHeight) {
					if (sourceWidth > widthLimit) {
						scale = widthLimit * 1.0f / sourceWidth;
					}
				}
				else {
					if (sourceHeight > heightLimit) {
						scale = heightLimit * 1.0f / sourceHeight;
					}
				}
			}

			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, quality, os);
			bitmap.recycle();
			bitmap = null;
			return os.toByteArray();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 图片圆角
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, final float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		int roundColor = 0xff424242;
		paint.setColor(roundColor);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.reset();
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap compositeBitmap(Bitmap baseBM, Bitmap frontBM) {
		Canvas canvas = new Canvas(baseBM);
		final Paint paint = new Paint();
		canvas.drawBitmap(baseBM, 0, 0, paint);
		final Rect rect = new Rect(0, 0, baseBM.getWidth(), baseBM.getHeight());
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		canvas.drawBitmap(frontBM, rect, rect, paint);
		return baseBM;
	}

	/**
	 * 从Bitmap获取byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] getPortraitByteArray(Bitmap bm) {
		InputStream input = null;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 100, os);
			bm.recycle();
			return os.toByteArray();

		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * 这个是等比例缩放: bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
	 */
	public static byte[] getResizedImageData(Context context, Uri uri, int quality, int widthLimit, int heightLimit) {
		InputStream input = null;
		Options opt = decodeBitmapOptionsInfo(context, uri);
		if (opt == null) {
			return null;
		}
		int outWidth = opt.outWidth;
		int outHeight = opt.outHeight;
		int s = 1;
		while ((outWidth / s > widthLimit) || (outHeight / s > heightLimit)) {
			s *= 2;
		}

		Options options = new Options();
		options.inSampleSize = s;
		try {
			// options.inSampleSize = computeSampleSize(opt, -1, widthLimit *
			// heightLimit);

			input = context.getContentResolver().openInputStream(uri);
			Bitmap b = BitmapFactory.decodeStream(input, null, options);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			b.compress(CompressFormat.JPEG, quality, os);
			b.recycle();
			b = null;
			return os.toByteArray();

		}
		catch (Exception e) {
			try {
				input = new FileInputStream(new File(uri.toString()));
				Bitmap b = BitmapFactory.decodeStream(input, null, options);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, quality, os);
				b.recycle();
				b = null;
				return os.toByteArray();
			}
			catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		}
		else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;

	}

	private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),

		Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		}
		else if (minSideLength == -1) {
			return lowerBound;
		}
		else {
			return upperBound;
		}
	}

	public static Options decodeBitmapOptionsInfo(Context context, Uri uri) {
		InputStream input = null;
		Options opt = new Options();
		try {
			input = context.getContentResolver().openInputStream(uri);
			opt.inJustDecodeBounds = true;
			opt.inPreferredConfig = Config.ARGB_8888;
			BitmapFactory.decodeStream(input, null, opt);
			return opt;
		}
		catch (FileNotFoundException e) {
			String path = null;
			Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				path = cursor.getString(1);
				if (!SDKVersionUtil.hasICS()) {
					cursor.close();
				}
			}
			else {
				path = uri.toString();
				if (path.indexOf("file:///mnt") > -1) {
					path = path.substring("file:///mnt".length());
				}
				else if (path.indexOf("file://") > -1) {
					path = path.substring("file://".length());
				}
			}

			if (path != null) {
				try {
					input = new FileInputStream(new File(path));
					opt.inJustDecodeBounds = true;
					opt.inPreferredConfig = Config.ARGB_8888;
					BitmapFactory.decodeStream(input, null, opt);
					return opt;
				}
				catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		finally {
			if (null != input) {
				try {
					input.close();
				}
				catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static byte[] getScaleddImage(Context context, Uri uri, int widthLimit, int heightLimit) {
		InputStream input = null;
		try {
			input = context.getContentResolver().openInputStream(uri);
			Bitmap b = BitmapFactory.decodeStream(input, null, getBitmapOptions());
			Bitmap c = Bitmap.createScaledBitmap(b, widthLimit, heightLimit, true);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			c.compress(CompressFormat.JPEG, 100, os);
			b.recycle();
			c.recycle();
			b = null;
			c = null;
			return os.toByteArray();

		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存图片到SDCard的默认路径下.
	 * 
	 *            - eg. hello.jpg
	 * @throws IOException
	 */
	public static void saveBitmapToSDCard(File f, Bitmap bitmap) throws IOException {
		saveBitmapToSDCard(f, bitmap, CompressFormat.JPEG);
	}

	/**
	 * 保存图片到SDCard的默认路径下.
	 * 
	 *            - eg. hello.jpg
	 * @param mBitmap
	 * @throws IOException
	 */
	public static void saveBitmapToSDCard(File f, Bitmap mBitmap, CompressFormat imageType) throws IOException {
		if (f.exists()) {
			f.delete();
		}
		else {
			f.createNewFile();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(imageType, 100, fOut);
		try {
			fOut.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] getImageDataByUri(Uri uri, Context context) {
		return getResizedImageData(context, uri, 100, 800, 800);
	}

	/**
	 * 根据Uri返回Bitmap位图
	 * 
	 * @param context
	 * @param uri
	 *            //形如 content://media/external/images/media/3747
	 * @return
	 */
	public static Bitmap getImageDataByUri(Context context, Uri uri) {
		InputStream input = null;
		Bitmap b = null;
		try {
			input = context.getContentResolver().openInputStream(uri);
			b = BitmapFactory.decodeStream(input, null, getBitmapOptions());

		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return b;
	}

	public static Options getBitmapOptions() {
		Options opts = new Options();
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inPreferredConfig = Config.RGB_565;
		return opts;
	}

	public static Options getThumbBitmapOptions() {
		Options opts = new Options();
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inSampleSize = 2;
		opts.inPreferredConfig = Config.RGB_565;
		return opts;
	}

	public static byte[] getCardBackgroundImageDataByUri(Uri uri, Context context) {
		byte[] data = getResizedImageData(context, uri, 100, 960, 960);

		Bitmap sourceBitmap = null;

		// byte[] data = getZoomImage(context, sourceBitmap, 640, 960, false, 100);
		int quality = 100;
		int len = data.length;
		while (len > 1024L * 100) {
			quality = quality - 4;
			// data = getZoomImage(context, sourceBitmap, 640, 960, false, 90);
			Options opts = new Options();
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inPreferredConfig = Config.RGB_565;
			sourceBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			sourceBitmap.compress(CompressFormat.JPEG, quality, os);
			sourceBitmap.recycle();
			sourceBitmap = null;
			len = os.toByteArray().length;
			try {
				os.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			if (len <= 1024L * 100) {

				return data = os.toByteArray();
			}
		}

		return data;
	}

	public static byte[] getImageThumbDataByUri(Uri uri, Context context) {
		return getResizedImageData(context, uri, 100, 160, 160);
	}

	public static Bitmap imageCrop(Bitmap src, int width, int height) {
		int srcWid = src.getWidth();
		int srcHei = src.getHeight();
		int destW = Math.min(width, srcWid);
		int destH = Math.min(srcHei, height);
		return Bitmap.createBitmap(src, 0, 0, destW, destH, null, false);
	}

	public static Bitmap scaleToAdapterWh(Bitmap bm, int limitWidth, int limitHeight) {
		int srcWid = bm.getWidth();
		int srcHei = bm.getHeight();
		float scaleW = limitWidth * 1.0f / srcWid;
		float scaleH = limitHeight * 1.0f / srcHei;
		float destScale = Math.min(scaleW, scaleH);
		return Bitmap.createScaledBitmap(bm, (int) (srcWid * destScale), (int) (srcHei * destScale), false);
	}

	/**
	 * 获取视频缩略图
	 * 
	 * @param videoName
	 * @param activity
	 * @return
	 */
	public static Bitmap loadThumbnail(String videoName, Activity activity) {

		String[] proj = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME };
		Cursor videocursor = activity.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Video.Media.DISPLAY_NAME + "='" + videoName
				+ "'", null, null);
		Bitmap curThumb = null;

		if (videocursor.getCount() > 0) {
			videocursor.moveToFirst();
			ContentResolver crThumb = activity.getContentResolver();
			Options options = new Options();
			options.inSampleSize = 1;
			curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, videocursor.getInt(0), MediaStore.Video.Thumbnails.MICRO_KIND,
					(Options) null);
		}

		if (!SDKVersionUtil.hasICS()) {
			if (videocursor != null) {
				videocursor.close();
			}
		}

		return curThumb;
	}

	/**
	 * 生成缩略图可调节分辨率大小的缩略图
	 * 
	 * @param source
	 * @param width
	 * @param height
	 * @param recycle
	 * @return
	 */
	public static Bitmap extractMiniThumb(Bitmap source, int width, int height, boolean recycle) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		}
		else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap miniThumbnail = transform(matrix, source, width, height, false);

		if (recycle && miniThumbnail != source) {
			source.recycle();
		}
		return miniThumbnail;
	}

	public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, boolean scaleUp) {
		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension, than the target. Transform it by placing as much of the image as possible into the target and leaving the top/bottom or left/right (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()), deltaYHalf
					+ Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
			c.drawBitmap(source, src, dst, null);
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			}
			else {
				scaler = null;
			}
		}
		else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			}
			else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
		}
		else {
			b1 = source;
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

		if (b1 != source) {
			b1.recycle();
		}

		return b2;
	}

	public static String getFileName(Context context, String entireFilePath) {
		File imageFile = null;
		if (ContentResolver.SCHEME_CONTENT.equals(Uri.parse(entireFilePath).getScheme())) {
			ContentResolver cr = context.getContentResolver();
			Uri imageUri = Uri.parse(entireFilePath);
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = cr.query(imageUri, projection, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			imageFile = new File(cursor.getString(column_index));
			if (!SDKVersionUtil.hasICS()) {
				cursor.close();
			}
		}
		else if (ContentResolver.SCHEME_FILE.equals(Uri.parse(entireFilePath).getScheme())) {
			imageFile = new File(Uri.parse(entireFilePath).getPath());
		}
		else {
			imageFile = new File(entireFilePath);
		}
		return imageFile.getName();
	}

	private static final long DEFAULT_MAX_BM_SIZE = 1000 * 250;

	public static Bitmap loadBitmap(String bmPath) {
		if (TextUtils.isEmpty(bmPath)) {
			return null;
		}
		File file = new File(bmPath);
		if (!file.exists()) {
			return null;
		}
		else {
			Bitmap bm;
			Options opts = new Options();
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inPreferredConfig = Config.RGB_565;
			long length = file.length();
			if (length > DEFAULT_MAX_BM_SIZE) {
				long ratio = length / DEFAULT_MAX_BM_SIZE;
				long simpleSize = (long) Math.ceil(Math.sqrt(ratio));
				opts.inSampleSize = (int) simpleSize;
				try {
					bm = BitmapFactory.decodeFile(bmPath, opts);
				}
				catch (Exception e) {
					bm = null;
				}
			}
			else {
				bm = BitmapFactory.decodeFile(bmPath, opts);
			}
			return bm;
		}
	}

	public static Bitmap loadBitmap(byte[] bmByte) {
		if (bmByte == null || bmByte.length == 0) {
			return null;
		}
		Bitmap bm;
		Options opts = new Options();
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inPreferredConfig = Config.RGB_565;
		long length = bmByte.length;
		if (length > DEFAULT_MAX_BM_SIZE) {
			long ratio = length / DEFAULT_MAX_BM_SIZE;
			long simpleSize = (long) Math.ceil(Math.sqrt(ratio));
			opts.inSampleSize = (int) simpleSize;
			try {
				bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);
			}
			catch (Exception e) {
				bm = null;
			}
		}
		else {
			bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);
		}
		return bm;
	}

	public static Bitmap getImage(String srcPath, float limitW, float limitH) {
		Options newOpts = new Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > limitW) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / limitW);
		}
		else if (w < h && h > limitH) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / limitH);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
		return bitmap;
	}

	/**
	 * 压缩到图片100k以内
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	// /**加载进图片后进行分辨率适配*/
	// public static void chatBgAdapter(String chatBgPath) throws IOException{
	// if (LocalStringUtils.isEmpty(chatBgPath)) {
	// return;
	// }
	// File file = new File(chatBgPath);
	// if (file.exists()) {
	// Options opts = new Options();
	// opts.inPurgeable = true;
	// opts.inInputShareable = true;
	// opts.inPreferredConfig = Bitmap.Config.RGB_565;
	// Bitmap bm = BitmapFactory.decodeFile(chatBgPath, opts);
	// //生成合适分辨率的图,刚好合适时无动作.
	// Bitmap cropedBitmap = scaleChatBgBitmap(bm);
	// //保存覆盖原图
	// if(cropedBitmap!=null){
	// saveBitmapToSDCard(file, cropedBitmap, Bitmap.CompressFormat.JPEG);
	// cropedBitmap.recycle();
	// }
	// }
	// }

	// /**按照聊天背景高宽 裁剪出合适分辨率的图片 (无需裁剪时返回空)*/
	// public static Bitmap scaleChatBgBitmap(Bitmap bm) {
	// Bitmap cropedBitmap;
	// try {
	// int bmWidth = bm.getWidth();
	// int bmHeight = bm.getHeight();
	// int[] winWH = new int[]{Constant_Properties.SCREEN_WIDTH,Constant_Properties.CHAT_BG_HEIGHT};
	// double wScale = ((double)winWH[0])/ bmWidth;
	// double hScale = ((double)winWH[1])/ bmHeight;
	// double destScale = Math.max(wScale, hScale);
	//
	// if(bmWidth == winWH[0]&&bmHeight == winWH[1]){
	// return null;
	// }else if (bmWidth < winWH[0] || bmHeight < winWH[1]) { // 如果小于屏幕尺寸,先放大到与屏幕比例相适应的尺寸才截取
	// if (wScale > hScale) { // 如果是一张长图
	// Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
	// // bm.recycle();
	// cropedBitmap = Bitmap.createBitmap(scaledBitmp, 0, (int) ((scaledBitmp.getHeight() - winWH[1]) / 2), scaledBitmp.getWidth(), winWH[1]);
	// scaledBitmp = null;
	// // scaledBitmp.recycle(); //置空后让系统自动回收,否则Nexus机型出错
	// }
	// else { // 如果是一张宽图，或着比例合适的图
	// Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
	// // bm.recycle();
	// cropedBitmap = Bitmap.createBitmap(scaledBitmp, (int) ((scaledBitmp.getWidth() - winWH[0]) / 2), 0, winWH[0],scaledBitmp.getHeight());
	// scaledBitmp = null;
	// // scaledBitmp.recycle();
	// }
	// }
	// else { // 先缩小到与屏幕相等尺寸才截取
	// if (wScale > hScale) { // 如果是一张长图
	// if(wScale == 1){
	// //bm宽度刚好,高度略长
	// cropedBitmap = Bitmap.createBitmap(bm, 0, (int) ((bm.getHeight() - winWH[1]) / 2), bm.getWidth(), winWH[1]);
	// }else{
	// Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
	// // bm.recycle();
	// cropedBitmap = Bitmap.createBitmap(scaledBitmp, 0, (int) ((scaledBitmp.getHeight() - winWH[1]) / 2), scaledBitmp.getWidth(), winWH[1]);
	// scaledBitmp = null;
	// // scaledBitmp.recycle();
	// }
	// }
	// else { // 如果是一张宽图，或着比例合适的图
	// if(hScale == 1){
	// //bm 高度刚好,宽度略宽
	// cropedBitmap = Bitmap.createBitmap(bm, (int) ((bm.getWidth() - winWH[0]) / 2), 0, winWH[0], bm.getHeight());
	// }else{
	// Bitmap scaledBitmp = Bitmap.createScaledBitmap(bm, (int) (bmWidth * destScale), (int) (bmHeight * destScale), false);
	// // bm.recycle();
	// cropedBitmap = Bitmap.createBitmap(scaledBitmp, (int) ((scaledBitmp.getWidth() - winWH[0]) / 2), 0, winWH[0], scaledBitmp.getHeight());
	// scaledBitmp = null;
	// // scaledBitmp.recycle();
	// }
	// }
	// }
	// bm = null;
	// } catch (Exception ex) {
	// cropedBitmap = null;
	// ex.printStackTrace();
	// }
	// return cropedBitmap;
	// }

	public static void rotateImage(int degrees, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		try {
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	public static byte[] getCompressedImage(Context context, String path, int maxPixels) {
		Uri uri = null;
		if (ContentResolver.SCHEME_CONTENT.equals(Uri.parse(path).getScheme())) {
			uri = Uri.parse(path);
		}
		else if (ContentResolver.SCHEME_FILE.equals(Uri.parse(path).getScheme())) {
			uri = Uri.parse(path);
		}
		else {
			uri = Uri.fromFile(new File(path));
		}

		return getCompressedImage(context, uri, maxPixels);
	}

	public synchronized static byte[] getCompressedImage(Context context, Uri imageUri, int maxPixels) {
		byte[] ret = null;
		InputStream in = null;
		ByteArrayOutputStream os = null;
		try {
			final int IMAGE_MAX_SIZE = (maxPixels == 0) ? 800000 : maxPixels;

			String uriContent = imageUri.toString();
			Uri uri = null;
			if (uriContent.indexOf("://") <= 0) {
				uri = Uri.fromFile(new File(uriContent));
			}
			else {
				uri = imageUri;
			}
			in = context.getContentResolver().openInputStream(uri);
			// Decode image size
			Options o = new Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();
			int scale = 1;

			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}

			Bitmap b = null;
			in = context.getContentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);
				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
				double x = (y / height) * width;
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
				// b.recycle();
				b = scaledBitmap;
				os = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, 85, os);
				b.recycle();
				b = null;
				ret = os.toByteArray();
				System.gc();
			}
			else {
				b = BitmapFactory.decodeStream(in);
				os = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, 100, os);
				b.recycle();
				b = null;
				ret = os.toByteArray();
				System.gc();
			}
			return ret;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (os != null) {
				try {
					os.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int getCompressedImageOptions(Context context, InputStream in, int maxPixels) {
		final int IMAGE_MAX_SIZE = (maxPixels == 0) ? 800000 : maxPixels;

		// Decode image size
		Options o = new Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, o);
		try {
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		int scale = 1;

		while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
			scale++;
		}
		return scale;
	}

	public synchronized static byte[] getCompressedImageRet(Context context, int scale, InputStream in, int maxPixels) {
		byte[] ret = null;
		ByteArrayOutputStream os = null;
		try {
			final int IMAGE_MAX_SIZE = (maxPixels == 0) ? 800000 : maxPixels;

			Options o = new Options();

			Bitmap b = null;
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);
				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
				double x = (y / height) * width;
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
				// b.recycle();
				b = scaledBitmap;
				os = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, 85, os);
				b.recycle();
				b = null;
				ret = os.toByteArray();
				System.gc();
			}
			else {
				b = BitmapFactory.decodeStream(in);
				os = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, 100, os);
				b.recycle();
				b = null;
				ret = os.toByteArray();
				System.gc();
			}
			return ret;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (os != null) {
				try {
					os.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized static Bitmap getBitmapForView(String imagePath) {
		int targetMax = 4096;
		// Get the dimensions of the bitmap
		Options bmOptions = new Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, bmOptions);
		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = computeInitialSampleSize(bmOptions, targetMax, targetMax * targetMax);
		bmOptions.inPurgeable = true;
		bmOptions.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
		return bitmap;

	}

	/**
	 * 按图片比例设置创建ImageView的LayoutParams，最大不超过maxWidth * maxHeight
	 */
	public static LayoutParams getImageLayoutParams(Context context, Bitmap bitmap, int maxWidth, int maxHeight) {
		LayoutParams layout = null;

		if (bitmap != null) {
			if (bitmap.getWidth() > bitmap.getHeight()) {
				layout = new LayoutParams(DipPixUtil.dip2px(context, maxWidth), DipPixUtil.dip2px(context, maxWidth * bitmap.getHeight() / bitmap.getWidth()));
			}
			else if (bitmap.getWidth() < bitmap.getHeight()) {
				layout = new LayoutParams(DipPixUtil.dip2px(context, maxHeight * bitmap.getWidth() / bitmap.getHeight()), DipPixUtil.dip2px(context, maxHeight));
			}
			else if (bitmap.getWidth() == bitmap.getHeight()) {
				layout = new LayoutParams(DipPixUtil.dip2px(context, maxWidth), DipPixUtil.dip2px(context, maxHeight));
			}
		}
		else {
			layout = new LayoutParams(DipPixUtil.dip2px(context, 100), DipPixUtil.dip2px(context, 80));
		}
		// layout.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		return layout;
	}

	/**
	 * 将Bitmap图片转换成灰度状态Bitmap
	 * 
	 * @param bmpOriginal
	 * @return
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

}
