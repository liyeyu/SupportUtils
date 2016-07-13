package liyeyu.support.utils.manager;

import android.app.Application;

/**
 * Created by Administrator on 2016/6/3.
 * 作者：mayichao
 * 邮箱：mayichao@hn-ssc.com
 */
public class BaseManager {

    public static Application app;

    public static void init(Application context) {
        app =  context;
    }
}
