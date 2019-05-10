package intlapp.dragonpass.com.mvpmodel.utils;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import intlapp.dragonpass.com.mvpmodel.BuildConfig;

/**
 * Created by sundy on 16/5/24.
 */
public class MyLog {
    private static final boolean isDebug = BuildConfig.DEBUG;
    private static final int STACK_TRACE_LEVELS_UP = 5;

    public static void E(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, getClassAndLineNum() + msg);
        }
    }

    public static void rtLog(String tag, String msg){
        if (isDebug) {
            Log.w(tag, getClassAndLineNum() + msg);
        }
    }

    public static void logJson(String tag, String msg){
        if (isDebug) {
            Log.w(tag, getClassAndLineNum() + formatJson(msg));
        }
    }
    /*
    * 格式化json格式字符创
    * */
    private static String formatJson( String msg){
        JSONObject obj;
        try {
            obj = new JSONObject(msg);
            return obj.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
            return msg;
        }
    }

    private static int getLineNumber()
    {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getLineNumber();
    }

    private static String getClassName()
    {
        String fileName = Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getFileName();
        return TextUtils.isEmpty(fileName) ? "" : fileName.substring(0, fileName.length() - 5);
    }

    private static String getClassAndLineNum()
    {
        if(!TextUtils.isEmpty(getClassName())&&getLineNumber()>0) {
            return "(" + getClassName() + ".java:" + getLineNumber() + ")";
        }else{
            return "";
        }
    }
}
