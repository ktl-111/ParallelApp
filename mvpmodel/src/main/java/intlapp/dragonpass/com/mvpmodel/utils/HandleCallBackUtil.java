package intlapp.dragonpass.com.mvpmodel.utils;

import android.content.Context;

import intlapp.dragonpass.com.mvpmodel.base.ObjectView;


/**
 * Created by steam_l on 2018/11/2.
 * Desprition :统一处理的一些UI显示和数据的工具类
 */

public class HandleCallBackUtil {
    public static void onSuccess(ObjectView callback, String msg) {
        if (callback != null) {
            callback.onSuccess(msg);
        }
    }

    public static void onError(ObjectView callback, String msg) {
        if (callback != null) {
            callback.onError(msg);
        }
    }

    public static void showLoading(ObjectView callback) {
        if (callback != null) {
            callback.showLoading();
        }
    }

    public static void hindeLoading(ObjectView callback) {
        if (callback != null) {
            callback.hindeLoading();
        }
    }

}
