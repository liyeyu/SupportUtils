package liyeyu.support.utils.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import liyeyu.support.utils.manager.PermissionsManager;

/**
 * Pictures selected image management tools
 * Created by liyeyu on 2016/5/17.
 */
public class PicUtil {

    public static int CODE_SELECT_PIC_MULTIPLE = 5560;
    public static int CODE_SELECT_PIC = 5561;
    public static int CODE_CAMERA_PIC = 5562;
    public static int CODE_CROP_PIC = 5563;
    public static String filePath =  "";


    /**
     * @param context
     */
    public static void openPhoto(Activity context){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        context.startActivityForResult(intent, CODE_SELECT_PIC);
    }
    /**
     * @param context
     */
    public static void openMultiplePhoto(Activity context){
//        Intent intent = new Intent(context, UserDefinedPhotoActivity.class);
//        context.startActivityForResult(intent, CODE_SELECT_PIC_MULTIPLE);
    }

    /**
     * @param context
     * @param filePath
     */
    public static void lookPhoto(Activity context, String filePath){
        if (!TextUtils.isEmpty(filePath)){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "image/*");
            context.startActivity(intent);
        }
    }

    /**
     * @param context
     * @return
     */
    public static void openCamera(final Activity context) {
        if(!isEnvironment(context)){
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionsManager.get().checkPermissions(context, Manifest.permission.CAMERA, new PermissionsManager.CheckCallBack() {
                @Override
                public void onSuccess(String permission) {
                    takeCamera(context);
                }

                @Override
                public void onError(String permission) {

                }
            });
        } else {
            takeCamera(context);
        }
    }

    private static String takeCamera(Activity context) {
        ContentValues values = new ContentValues();
        long time = new Date(System.currentTimeMillis()).getTime();
        values.put(MediaStore.Images.Media.TITLE, new Date(System.currentTimeMillis()).getTime());
        File photoFile = new File(getSDCardPath() + File.separator + "DCIM"
                + File.separator + time + ".jpg");
        filePath = photoFile.getPath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        context.startActivityForResult(intent, CODE_CAMERA_PIC);
        return filePath;
    }


    public static Object onActivityResult(Activity context,int requestCode, int resultCode, Intent data){
        if (resultCode==Activity.RESULT_OK){
            if (requestCode==CODE_SELECT_PIC){
                filePath = UriUtils.getImageAbsolutePath(context, data.getData());
                return  filePath;
            }else if(requestCode==CODE_CAMERA_PIC){
                return filePath;
            }else if(requestCode==CODE_SELECT_PIC_MULTIPLE){
                return data.getStringArrayListExtra("tag");
            }else  if(requestCode==CODE_CROP_PIC){
                startPhotoZoom(Uri.fromFile(new File(filePath)),context);
                return filePath;
            }
        }
        return null;
    }

    public static boolean isEnvironment(Context context) {

        String sdState = Environment.getExternalStorageState();
        if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "Please check whether the SD card installed", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @return
     */
    public static String getSDCardPath() {

        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        try {
            Process p = run.exec(cmd);
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                if (lineStr.contains("sdcard")
                        && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure",
                                "");
                        return result;
                    }
                }
                //Check whether the command is failure
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0 Said the end of the normal;1：abend
                    Log.e("getSDCardPath", "Failed to perform the command!");
                }
            }
        } catch (Exception e) {
            Log.e("getSDCardPath", e.toString());
            return Environment.getExternalStorageDirectory().getPath();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inBr != null) {
                    inBr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * @param uri
     */
    public static void startPhotoZoom(Uri uri,Activity mActivity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY Is wide high proportion
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY Is wide high cut images
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        mActivity.startActivityForResult(intent, CODE_CROP_PIC);
    }
}
