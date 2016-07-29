package liyeyu.support.utils.manager;

import android.app.Application;

/**
 * Created by liyeyu on 2016/6/3.
 */
public class BaseManager {

    public static Application app;
    public static void init(Application context) {
        app =  context;
    }
}
