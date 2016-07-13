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
 * bitmap操作类 liyeyu
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

	/**
	 * 根据图片的质量进行压缩
	 * 
	 * @param image
	 * @param size
	 *            质量压缩的大小阈值
	 * @return
	 */
	private static Bitmap compressImage(Bitmap image, int size) {
		ByteArrayInputStream isBm = (ByteArrayInputStream) compressImageBackInputStream(
				image, size);
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 按质量压缩图片返回一个InputStream
	 * 
	 * @param image
	 * @param size
	 *            质量压缩的大小阈值
	 * @return
	 */
	private static InputStream compressImageBackInputStream(Bitmap image,
			int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
		System.out.println(baos.toByteArray().length / 1024);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		return is;
	}

	/**
	 * 按文件路径等比例压缩之后再进行质量压缩
	 * 
	 * @param srcPath
	 * @param size
	 *            质量压缩的大小阈值
	 * @param width
	 *            预期压缩的宽
	 * @param height
	 *            预期压缩的高
	 * @return
	 */
	public static Bitmap getImageByPath(String srcPath, int size, int width,
			int height) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		// int w = newOpts.outWidth;
		// int h = newOpts.outHeight;
		// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// int hh = height;// 这里设置高度为800f
		// int ww = width;// 这里设置宽度为480f
		// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		// int be = 1;// be=1表示不缩放
		// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
		// be = (int) (newOpts.outHeight / hh);
		// }
		// if (be <= 0)
		// be = 1;
		// newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap, size);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 按文件路径等比例压缩之后再进行质量压缩
	 * 
	 * @param srcPath
	 * @param size
	 *            质量压缩的大小阈值
	 * @param width
	 *            预期压缩的宽
	 * @param height
	 *            预期压缩的高
	 * @return
	 */
	public InputStream getInputStreamByPath(String srcPath, int size,
			int width, int height) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		// int w = newOpts.outWidth;
		// int h = newOpts.outHeight;
		// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// int hh = height;// 这里设置高度为800f
		// int ww = width;// 这里设置宽度为480f
		// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		// int be = 1;// be=1表示不缩放
		// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
		// be = (int) (newOpts.outHeight / hh);
		// }
		// if (be <= 0)
		// be = 1;
		// newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImageBackInputStream(bitmap, size);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 将bitmap压缩之后返回新的bitmap
	 * 
	 * @param image
	 * @param size
	 *            质量压缩的大小阈值
	 * @param width
	 *            预期压缩的宽
	 * @param height
	 *            预期压缩的高
	 * @return
	 */
	public Bitmap getSmallImageByBitmap(Bitmap image, int size, int width,
			int height) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		// int w = newOpts.outWidth;
		// int h = newOpts.outHeight;
		// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// int hh = height;// 这里设置高度为800f
		// int ww = width;// 这里设置宽度为480f
		// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		// int be = 1;// be=1表示不缩放
		// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
		// be = (int) (newOpts.outHeight / hh);
		// }
		// if (be <= 0)
		// be = 1;;
		// newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);
		newOpts.inPreferredConfig = Config.RGB_565;// 降低图片从ARGB888到RGB565
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap, size);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 计算压缩比例值
	 * 
	 * @param options
	 *            解析图片的配置信息
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 保存图片原宽高值
		final int height = options.outHeight;
		final int width = options.outWidth;
		// 初始化压缩比例为1
		int inSampleSize = 1;

		// 当图片宽高值任何一个大于所需压缩图片宽高值时,进入循环计算系统
		if (height > reqHeight || width > reqWidth) {

			// final int halfHeight = height / 2;
			// final int halfWidth = width / 2;

			// 压缩比例值每次循环两倍增加,
			// 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
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
	/**
	 * 保存图片到指定目录 parentPath
	 *
	 * @param
	 */
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
		/*	Uri uri =  Uri.fromFile(mFile);
		Log.i("保存返回", uri+"");
		Log.i("保存返回1", uri.getEncodedPath()+"");*/
		//bmp.add(mBitmap);
		String filePath="";
		if(mFile.toString().contains("file:///")){
			filePath=mFile.toString();
		}else{
			filePath = mFile.toString();
		}
		return filePath;

	}
	// 加载本地图片
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
	
	/**
	 * 从uri读取图片并进行压缩
	 * @param context
	 * @param uri
	 * @param size
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
