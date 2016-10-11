package liyeyu.support.utils.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressLint("NewApi")
public class ImageUtil {

	public final static String SDCARD_MNT = "/mnt/sdcard";
	public final static String SDCARD = "/sdcard";

	/** 请求相册 */
	public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
	/** 请求相机 */
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
	/** 请求裁剪 */
	public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;
	public static final int RADIUS = 24;

	/**
	 * Bitmap to byte array
	 *
	 * @param b
	 * @return
	 */
	public static byte[] bitmapToByte(Bitmap b) {
		if (b == null) {
			return null;
		}

		ByteArrayOutputStream o = new ByteArrayOutputStream();
		b.compress(CompressFormat.PNG, 100, o);
		return o.toByteArray();
	}

	/**
	 * save image to /data/data/PACKAGE_NAME/files
	 * 
	 * @throws IOException
	 */
	public static void saveImage(Context context, String fileName, Bitmap bitmap)
			throws IOException {
		saveImage(context, fileName, bitmap, 100);
	}

	public static void saveImage(Context context, String fileName,
			Bitmap bitmap, int quality) throws IOException {
		if (bitmap == null || fileName == null || context == null)
			return;

		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, quality, stream);
		byte[] bytes = stream.toByteArray();
		fos.write(bytes);
		fos.close();
	}

	/**
	 * save image to SD card
	 * 
	 * @throws IOException
	 */
	public static void saveImageToSD(Context ctx, String filePath, Bitmap bitmap, int quality) throws IOException {
		if (bitmap != null) {
			File file = new File(filePath.substring(0,
					filePath.lastIndexOf(File.separator)));
			if (!file.exists()) {
				file.mkdirs();
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(filePath));
			bitmap.compress(CompressFormat.JPEG, quality, bos);
			bos.flush();
			bos.close();
			if (ctx != null) {
				scanPhoto(ctx, filePath);
			}
		}
	}

	/**
	 *  scan image
	 */
	private static void scanPhoto(Context ctx, String imgFileName) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File file = new File(imgFileName);
		Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		ctx.sendBroadcast(mediaScanIntent);
	}

	/**
	 * get bitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * get bitmap path
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapByPath(String filePath) {
		return getBitmapByPath(filePath, null);
	}

	public static Bitmap getBitmapByPath(String filePath,
			BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * @param file
	 * @return
	 */
	public static Bitmap getBitmapByFile(File file) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * @return
	 */
	public static String getTempFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = format.format(new Timestamp(System
				.currentTimeMillis()));
		return fileName;
	}

	/**
	 *
	 * @return
	 */
	public static String getCamerPath() {
		return Environment.getExternalStorageDirectory() + File.separator
				+ "FounderNews" + File.separator;
	}

	/**
	 * check uri schema otherwise return AbsolutePath
	 * 
	 * @return
	 */
	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file://" + SDCARD + File.separator;
		String pre2 = "file://" + SDCARD_MNT + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	/**
	 * get file AbsolutePath by uri
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAbsoluteImagePath(Activity context, Uri uri) {
		String imagePath = "";
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, proj, // Which columns to
				// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}

		return imagePath;
	}



	public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
		Bitmap bitmap = getBitmapByPath(filePath);
		return zoomBitmap(bitmap, w, h);
	}

	/**
	 * get last image by store
	 * @return
	 */
	public static String getLatestImage(Activity context) {
		String latestImage = null;
		String[] items = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null,
				null, MediaStore.Images.Media._ID + " desc");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				latestImage = cursor.getString(1);
				break;
			}
		}

		return latestImage;
	}

	/**
	 * compute image size
	 * 
	 * @param img_size
	 * @param square_size
	 * @return
	 */
	public static int[] scaleImageSize(int[] img_size, int square_size) {
		if (img_size[0] <= square_size && img_size[1] <= square_size)
			return img_size;
		double ratio = square_size
				/ (double) Math.max(img_size[0], img_size[1]);
		return new int[] { (int) (img_size[0] * ratio),
				(int) (img_size[1] * ratio) };
	}

	/**
	 * create Image Thumbnail
	 * 
	 * @param context
	 * @param largeImagePath
	 *            source
	 * @param thumbfilePath
	 *            target
	 * @param square_size
	 * @param quality
	 * @throws IOException
	 */
	public static void createImageThumbnail(Context context,
			String largeImagePath, String thumbfilePath, int square_size,
			int quality) throws IOException {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		Bitmap cur_bitmap = getBitmapByPath(largeImagePath, opts);

		if (cur_bitmap == null)
			return;

		int[] cur_img_size = new int[] { cur_bitmap.getWidth(),
				cur_bitmap.getHeight() };
		int[] new_img_size = scaleImageSize(cur_img_size, square_size);
		Bitmap thb_bitmap = zoomBitmap(cur_bitmap, new_img_size[0],
				new_img_size[1]);
		saveImageToSD(null, thumbfilePath, thb_bitmap, quality);
	}

	/**
	 *
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
					true);
		}
		return newbmp;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int newWidth = 200;
		int newHeight = 200;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}

	/**
	 *
	 * @param context
	 *            Activity
	 * @param bitmap
	 * @return
	 */
	public static Bitmap reDrawBitMap(Activity context, Bitmap bitmap) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int rHeight = dm.heightPixels;
		int rWidth = dm.widthPixels;
		// float rHeight=dm.heightPixels/dm.density+0.5f;
		// float rWidth=dm.widthPixels/dm.density+0.5f;
		// int height=bitmap.getScaledHeight(dm);
		// int width = bitmap.getScaledWidth(dm);
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		float zoomScale;
		/** way 1 **/
		// if(rWidth/rHeight>width/height){//以高为准
		// zoomScale=((float) rHeight) / height;
		// }else{
		// //if(rWidth/rHeight<width/height)
		// zoomScale=((float) rWidth) / width;
		// }
		/** way 2 **/
		// if(width*1.5 >= height) {
		// if(width >= rWidth)
		// zoomScale = ((float) rWidth) / width;
		// else
		// zoomScale = 1.0f;
		// }else {
		// if(height >= rHeight)
		// zoomScale = ((float) rHeight) / height;
		// else
		// zoomScale = 1.0f;
		// }
		/** way 3 **/
		if (width >= rWidth)
			zoomScale = ((float) rWidth) / width;
		else
			zoomScale = 1.0f;
		Matrix matrix = new Matrix();
		matrix.postScale(zoomScale, zoomScale);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
						: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	/**
	 * Obtain rounded picture method
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * To get the reflection image method
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * Get photo type
	 * 
	 * @param file
	 * @return
	 */
	public static String getImageType(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			String type = getImageType(in);
			return type;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Get photo type
	 * 
	 * @param in
	 * @return
	 * @see #getImageType(byte[])
	 */
	public static String getImageType(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			byte[] bytes = new byte[8];
			in.read(bytes);
			return getImageType(bytes);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Get photo type
	 * 
	 * @param bytes
	 *            2~8 byte at beginning of the image file
	 * @return image mimetype or null if the file is not image
	 */
	public static String getImageType(byte[] bytes) {
		if (isJPEG(bytes)) {
			return "image/jpeg";
		}
		if (isGIF(bytes)) {
			return "image/gif";
		}
		if (isPNG(bytes)) {
			return "image/png";
		}
		if (isBMP(bytes)) {
			return "application/x-bmp";
		}
		return null;
	}

	private static boolean isJPEG(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
	}

	private static boolean isGIF(byte[] b) {
		if (b.length < 6) {
			return false;
		}
		return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
				&& (b[4] == '7' || b[4] == '9') && b[5] == 'a';
	}

	private static boolean isPNG(byte[] b) {
		if (b.length < 8) {
			return false;
		}
		return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78
				&& b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
				&& b[6] == (byte) 26 && b[7] == (byte) 10);
	}

	private static boolean isBMP(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == 0x42) && (b[1] == 0x4d);
	}

	/**
	 * Get to the zoom matrix image parameter.
	 * */
	public static Bitmap getMatrixImage(int newWidth, int newHeight, Bitmap bit) {
		int width = bit.getWidth();
		int height = bit.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bit, 0, 0, width, height, matrix, true);
	}

	/**
	 * HTTP a request, and get the Bitmap object
	 * 
	 * @param imageUrl
	 */
	public static Bitmap downloadBitmap(String imageUrl) {
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL url = new URL(imageUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5 * 1000);
			con.setReadTimeout(10 * 1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			InputStream is = con.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			bitmap = getMatrixImage(50, 50, bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return bitmap;
	}
	 //Bitmap:Put a url network into a local Bitmap images
	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * Byte conversion to images
	 * 
	 * @param bytes
	 * */
	public static Bitmap conversionByteToBitmap(byte[] bytes) {
		float sc = 50;
		BitmapFactory.Options options = new BitmapFactory.Options();
		// This parameter represents the, true not allocate memory for the bitmap space
		// only some of the image information (such as picture size)
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		options.inJustDecodeBounds = false;
		int be = (int) (options.outHeight / sc);
		if (be <= 0) {
			be = 1;
		} else if (be > 3) {
			be = 3;
		}
		options.inSampleSize = be;
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory
					.decodeByteArray(bytes, 0, bytes.length, options);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception

			System.gc();
			bmp = null;
		}
		return bmp;
	}

	/**
	 * :Image converted into bytes
	 * 
	 *            @
	 * */
	public static byte[] conversionImageToByte(String pathURL) {
		Log.i("", ">>>>>>>>>>>" + pathURL);
		URL m;
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		byte isBuffer[] = new byte[1024];
		if (pathURL == null) {
			return null;
		}
		try {
			m = new URL("file:"+pathURL);
			i = (InputStream) m.getContent();
			bis = new BufferedInputStream(i, 1024 * 4);
			out = new ByteArrayOutputStream();
			int len = 0;

			while ((len = bis.read(isBuffer)) != -1) {
				out.write(isBuffer, 0, len);
			}
			out.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (out == null) {
			return null;
		}
		byte[] data = out.toByteArray();
		return data;
	}

	/**
	 * @param url
	 * @param sc
	 *
	 * 
	 */
	public static Bitmap loadImageFromUrl(String url, int sc) {

		URL m;
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		byte isBuffer[] = new byte[1024];
		if (url == null) {
			return null;
		}
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			bis = new BufferedInputStream(i, 1024 * 4);
			out = new ByteArrayOutputStream();
			int len = 0;

			while ((len = bis.read(isBuffer)) != -1) {
				out.write(isBuffer, 0, len);
			}
			out.close();
			bis.close();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (out == null) {
			return null;
		}
		byte[] data = out.toByteArray();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		options.inJustDecodeBounds = false;
		int be = (int) (options.outHeight / (float) sc);
		if (be <= 0) {
			be = 1;
		} else if (be > 3) {
			be = 3;
		}
		options.inSampleSize = be;
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception

			System.gc();
			bmp = null;
		}
		return bmp;
	}

	/**
	 *
	 * @param imagePath
	 */
	public static Bitmap getZoomBitmap(String imagePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false;
		int be = (int) (options.outHeight / (float) 300);
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bm = BitmapFactory.decodeFile(imagePath, options);

		return bm;
	}
	public static Bitmap Base64ToBitmap(String base64) {
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(base64, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}


	/**
	 * @return
	 * @throws IOException
	 * int degree = ImageUtils.readPictureDegree(filePath);
		Bitmap bitmap = ImageUtils.rotateBitmap(ImageUtils.getBitmapByPath(filePath), degree);
		filePath = ImageUtils.saveBitmap(bitmap, Constants.LOCAL_PATH,System.currentTimeMillis() + ".jpg");
	 */
	public static  String compressImageSize(String src) throws IOException {
		int degree = readPictureDegree(src);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(src)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(src)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		String parentPath = src.substring(0, src.lastIndexOf(File.separator));
		Bitmap bitmap0 = rotateBitmap(bitmap, degree);
		String name = src.substring(src.lastIndexOf(File.separator)+1,src.lastIndexOf("."))+"_cpress"+src.substring(src.lastIndexOf("."));
		return saveBitmap(bitmap0, parentPath, name);
	}


	public static  String saveBitmap(Bitmap mBitmap,String parentPath,String name) {
		File files = new File(parentPath);
		if (!files.exists()) {
			files.mkdirs();
		}
		File mFile = new File(parentPath,name);
		try {
			FileOutputStream out = new FileOutputStream(mFile);
			mBitmap.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String filePath="";
		if(mFile.toString().contains("file:///")){
			filePath=mFile.toString();
		}else{
			filePath = mFile.toString();
		}
		return filePath;

	}

	/**
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static  int readPictureDegree(String path) {    
		int degree  = 0;    
		try {    
			ExifInterface exifInterface = new ExifInterface(path);    
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);    
			switch (orientation) {    
			case ExifInterface.ORIENTATION_ROTATE_90:    
				degree = 90;    
				break;    
			case ExifInterface.ORIENTATION_ROTATE_180:    
				degree = 180;    
				break;    
			case ExifInterface.ORIENTATION_ROTATE_270:    
				degree = 270;    
				break;    
			}    
		} catch (IOException e) {    
			e.printStackTrace();    
		}    
		return degree;    
	}  
	public static Bitmap rotateBitmap(Bitmap bitmap, int rotate){  
		if(bitmap == null)  
			return null ;  

		int w = bitmap.getWidth();  
		int h = bitmap.getHeight();  

		// Setting post rotate to 90  
		Matrix mtx = new Matrix();  
		mtx.postRotate(rotate);  
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);  
	}  
}
