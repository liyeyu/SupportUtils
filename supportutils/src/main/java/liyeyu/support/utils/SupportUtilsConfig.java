package liyeyu.support.utils;

import android.app.Application;

import liyeyu.support.utils.manager.BaseManager;
import liyeyu.support.utils.utils.CrashHandler;

/**配置utils
 * Created by Liyeyu on 2016/7/13.
 */
public class SupportUtilsConfig {
    public static void init(Application context) {
        BaseManager.init(context);
        CrashHandler.init(context);
    }
}
