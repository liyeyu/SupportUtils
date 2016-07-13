package liyeyu.support.utils.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 *
 */
public class OtherUtil {

	/**
	 * 获取当前线程堆栈信息
	 *
	 * @return
	 */
	public static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[3];
	}

	/**
	 * 获取调用者堆栈信息
	 *
	 * @return
	 */
	public static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[5];
	}

	/**
	 * 判断是否是主线程
	 *
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public static boolean isUiThread(Context context) {
		return Thread.currentThread() == context.getMainLooper().getThread();
	}

	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 软键盘是否显示状态
	 *
	 * @param mContext
	 * @param view
	 * @param
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public static boolean hideSoftInputFromWindow(Activity mContext, View view) {
		boolean isActive = false;
		Configuration config = mContext.getResources().getConfiguration();
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (config.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_NO) { // 如果未隐藏
			isActive = true;
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		return isActive;
	}

	/**
	 * 获得当前在栈顶的Activity
	 *
	 * @param mContext
	 * @return
	 */
	public static String getCurrentActivity(Context mContext) {
		ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
		return runningActivity;
	}

	/**
	 * 通过反射获取栈顶activity的实例
	 * @return
     */
	public static Activity getCurrentActivity() {
		try {
			Class<?> aClass = Class.forName("android.app.ActivityThread");
			Object activityThread = aClass.getMethod("currentActivityThread").invoke(null);
			Field mActivities = aClass.getDeclaredField("mActivities");
			mActivities.setAccessible(true);
			Map activities = (Map) mActivities.get(activityThread);

			for (Object activityRecord : activities.values()) {
				Class activityRecordClass = activityRecord.getClass();
				Field pausedField = activityRecordClass.getDeclaredField("paused");

				pausedField.setAccessible(true);
				if (!pausedField.getBoolean(activityRecord)) {
					Field activityField = activityRecordClass.getDeclaredField("activity");
					activityField.setAccessible(true);
					Activity activity = (Activity) activityField.get(activityRecord);
					return activity;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}


}