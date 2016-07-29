package liyeyu.support.utils.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Liyeyu on 2016/6/29.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler myCrashHandler = null;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private Context mContext = null;

    private SimpleDateFormat dataFormat = null;
    private static  String ERROR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"CrashHandler";

    private CrashHandler(Context context) {
        initConfig(context);
    }

    /**
     * init in application
     * @param context
     * @return
     */
    public static CrashHandler init(Application context) {
        if (myCrashHandler != null) {
            synchronized (CrashHandler.class) {
                if (myCrashHandler != null) {
                    myCrashHandler = new CrashHandler(context);
                }
            }
        }
        return myCrashHandler;
    }

    public static void setErrorPath(String path){
        ERROR_PATH = path;
    }

    public void initConfig(Context context) {
        this.mContext = context;
        dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
//		CallManager.getInstance().logout();
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        final String versionInfo = getVersionInfo();
        final String mobileInfo = getMobileInfo();
        final String errorInfo = getErrorInfo(ex);
        WriteErrorFileData(ERROR_PATH, composeErrors(
                dataFormat.format(new Date()), versionInfo, mobileInfo,
                errorInfo));
        return false;
    }

    /* 合成错误信息 */
    private String composeErrors(String errorTime, String errorVersionMsg,
                                 String errorMobileMsg, String errorMsg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n*********Bug Info********\r\n");
        sb.append("time:");
        sb.append(errorTime);
        sb.append("\r\n");
        sb.append("version:");
        sb.append(errorVersionMsg);
        sb.append("\r\n");
        sb.append("mobile:");
        sb.append(errorMobileMsg);
        sb.append("\r\n");
        sb.append("log:");
        sb.append(errorMsg);
        sb.append("\r\n");
        return sb.toString();
    }

    /**
     * @param arg1
     * @return
     */
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }

    /**
     * @return
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "version none";
        }
    }

    /**
     *
     * @return
     */
    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void WriteErrorFileData(String path, String content) {

        String errorLogFilePath = path;
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String datetime = dataFormat.format(new Date());

        FileWriter writer = null;
        try {
            File file = new File(errorLogFilePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            writer = new FileWriter(errorLogFilePath + datetime + ".txt");
            writer.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }
}