package liyeyu.support.utils.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * bitmap utils liyeyu
 */
public class ImageUtils {

	private static ImageUtils imageUtils;
	public static final int DEFAULE_SIZE = 100;// 100kb
	public static final int DEFAULT_WIDTH = 480;// 480px

	private ImageUtils() {
		// TODO Auto-generated constructor stub
	}

	public static ImageUtils getInstance() {

		if (imageUtils == null) {
			imageUtils = new ImageUtils();
		}
		return imageUtils;
	}

	private static Bitmap compressImage(Bitmap image, int size) {
		ByteArrayInputStream isBm = (ByteArrayInputStream) compressImageBackInputStream(
				image, size);
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}
	private static InputStream compressImageBackInputStream(Bitmap image,
															int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) {
			baos.reset();
			options -= 10;
			image.compress(CompressFormat.JPEG, options, baos);

		}
		System.out.println(baos.toByteArray().length / 1024);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}
	public static Bitmap getImageByPath(String srcPath, int size, int width,
										int height) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		newOpts.inJustDecodeBounds = false;
		newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap, size);
	}

	public InputStream getInputStreamByPath(String srcPath, int size,
											int width, int height) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		newOpts.inJustDecodeBounds = false;
		newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImageBackInputStream(bitmap, size);
	}
	public Bitmap getSmallImageByBitmap(Bitmap image, int size, int width,
										int height) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();
			image.compress(CompressFormat.JPEG, 50, baos);
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		newOpts.inJustDecodeBounds = false;
		newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
		newOpts.inPreferredConfig = Config.RGB_565;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap, size);
	}


	private static int calculateInSampleSize(BitmapFactory.Options options,
											 int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			while ((height / inSampleSize) >= reqHeight
					&& (width / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public File getCompressedImageFile(String srcPath, int size, int width,
									   int height, String storePath) {
		Bitmap bm = getImageByPath(srcPath, size, width, height);

		if (bm != null) {
			if (!TextUtils.isEmpty(storePath)) {
				File file = new File(storePath);
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				CompressFormat format = CompressFormat.JPEG;
				int quality = 100;
				OutputStream stream = null;
				try {
					stream = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				bm.compress(format, quality, stream);
				return file;
			}
		}
		return null;
	}

	public static File getCompressedImageFile(String srcPath, String storePath) {
		Bitmap bm = getImageByPath(srcPath, DEFAULE_SIZE, DEFAULT_WIDTH,
				DEFAULT_WIDTH);

		if (bm != null) {
			if (!TextUtils.isEmpty(storePath)) {
				File file = new File(storePath);
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				CompressFormat format = CompressFormat.JPEG;
				int quality = 100;
				OutputStream stream = null;
				try {
					stream = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				bm.compress(format, quality, stream);
				return file;
			}
		}
		return null;
	}

	public static  String saveBitmap(Bitmap mBitmap,String parentPath,String name) {
		// 获取sd卡根目录
		File files = new File(parentPath);
		if (!files.exists()) {
			files.mkdirs();
		}
		File mFile = new File(parentPath,name);
		try {
			FileOutputStream out = new FileOutputStream(mFile);
			mBitmap.compress(CompressFormat.PNG, 100, out);
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
	public static Bitmap loadImage(Context ctx, File file, int size) {
		if (file == null || !file.exists())
			return null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] bts = new byte[1024];
			int count = -1;
			while ((count = in.read(bts, 0, 1024)) != -1)
				outStream.write(bts, 0, count);
			bts = null;
			byte[] data = outStream.toByteArray();

			Bitmap bitmap = null;
			BitmapFactory.Options opt = new BitmapFactory.Options();
			// If we have to resize this image, first get the natural bounds.
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, opt);
			int actualWidth = opt.outWidth;
			int actualHeight = opt.outHeight;

			// Then compute the dimensions we would ideally like to decode to.
			int desiredWidth = getResizedDimension(size, size, actualWidth,
					actualHeight);
			int desiredHeight = getResizedDimension(size, size, actualHeight,
					actualWidth);

			// Decode to the nearest power of two scaling factor.
			opt.inJustDecodeBounds = false;
			// TODO(ficus): Do we need this or is it okay since API 8 doesn't
			// support it?
			// decodeOptions.inPreferQualityOverSpeed =
			// PREFER_QUALITY_OVER_SPEED;
			opt.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
					desiredWidth, desiredHeight);
			Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0,
					data.length, opt);

			// If necessary, scale down to the maximal acceptable size.
			if (tempBitmap != null
					&& (tempBitmap.getWidth() > desiredWidth || tempBitmap
					.getHeight() > desiredHeight)) {
				bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
						desiredHeight, true);
				tempBitmap.recycle();
			} else {
				bitmap = tempBitmap;
			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	private static int getResizedDimension(int maxPrimary, int maxSecondary,
										   int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	public static int findBestSampleSize(int actualWidth, int actualHeight,
										 int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	public static Bitmap getThumbnailByUri(Context context, Uri uri, int size) throws IOException {
		InputStream input = context.getContentResolver().openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither=true;//optional
		onlyBoundsOptions.inPreferredConfig= Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
		double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither=true;//optional
		bitmapOptions.inPreferredConfig= Config.ARGB_8888;//optional
		input = context.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}
	private static int getPowerOfTwoForSampleRatio(double ratio){
		int k = Integer.highestOneBit((int)Math.floor(ratio));
		if(k==0) return 1;
		else return k;
	}
}
