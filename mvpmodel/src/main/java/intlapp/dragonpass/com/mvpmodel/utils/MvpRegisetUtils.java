package intlapp.dragonpass.com.mvpmodel.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.callback.CreateMultiplePresentListener;
import intlapp.dragonpass.com.mvpmodel.callback.CreateSingPresentListener;
import intlapp.dragonpass.com.mvpmodel.callback.MyActivityLifecycleCallbacks;
import intlapp.dragonpass.com.mvpmodel.orther.MultipleMVPActivity;
import intlapp.dragonpass.com.mvpmodel.orther.MultipleMVPFragment;
import intlapp.dragonpass.com.mvpmodel.orther.SingMVPActivity;
import intlapp.dragonpass.com.mvpmodel.orther.SingMVPFragment;


/**
 * Created by steam_l on 2018/11/5.
 * Desprition :一些注册mvp工具类
 */

public class MvpRegisetUtils {
    private static Map<String, FragmentManager.FragmentLifecycleCallbacks> fragmentMap = new HashMap<>();
    private static Map<String, MyActivityLifecycleCallbacks> activityMap = new HashMap<>();

    private static void registerFragmentCallback(Fragment fragment, FragmentManager.FragmentLifecycleCallbacks multipleMVPFragment) {
        ((AppCompatActivity) fragment.getContext()).getSupportFragmentManager()
                .registerFragmentLifecycleCallbacks(multipleMVPFragment, false);
    }

    /**
     * 注册多个p的fragment
     *
     * @param listener 创建P的回调接口
     * @param fragment 当前fragment
     * @return 返回singmvp操作实例 {@link MultipleMVPFragment},初始化调用{@link MultipleMVPFragment#init()}方法
     */
    public static MultipleMVPFragment registMultipleMvp(Fragment fragment, CreateMultiplePresentListener listener) {
        MultipleMVPFragment multipleMVPFragment = (MultipleMVPFragment) fragmentMap.get(fragment.getClass().getSimpleName());
        if (multipleMVPFragment == null) {
            multipleMVPFragment = new MultipleMVPFragment(listener, fragment);
            fragmentMap.put(fragment.getClass().getSimpleName(), multipleMVPFragment);
        }
        registerFragmentCallback(fragment, multipleMVPFragment);
        return multipleMVPFragment;
    }

    /**
     * 注册单个p的fragment
     *
     * @param listener 创建P的回调接口,实现的接口中的泛型要对应具体的P
     * @param fragment 当前fragment
     * @param <P>      当前对应的P
     * @return 返回singmvp操作实例 {@link SingMVPActivity<P>},初始化调用{@link SingMVPFragment#init()}方法
     */
    public static <P extends ObjectPresenter> SingMVPFragment<P> registSingMvp(Fragment fragment, CreateSingPresentListener<P> listener) {
        SingMVPFragment<P> singMVPFragment = (SingMVPFragment<P>) fragmentMap.get(fragment.getClass().getSimpleName());
        if (singMVPFragment == null) {
            singMVPFragment = new SingMVPFragment(listener, fragment);
            fragmentMap.put(fragment.getClass().getSimpleName(), singMVPFragment);
        }
        registerFragmentCallback(fragment, singMVPFragment);
        return singMVPFragment;
    }

    /**
     * 注册多个p的activity
     *
     * @param listener 创建P的回调接口
     * @param activity 当前activity
     * @return 返回multiplemvp操作实例 {@link MultipleMVPActivity},初始化调用{@link MultipleMVPActivity#init()}方法
     */
    public static MultipleMVPActivity registMultipleMvp(AppCompatActivity activity, CreateMultiplePresentListener listener) {
        MultipleMVPActivity multipleMVPActivity = (MultipleMVPActivity) activityMap.get(activity.getClass().getSimpleName());
        if (multipleMVPActivity == null) {
            multipleMVPActivity = new MultipleMVPActivity(listener, activity);
            activityMap.put(activity.getClass().getSimpleName(), multipleMVPActivity);
        }
        activity.getApplication().registerActivityLifecycleCallbacks(multipleMVPActivity);
        return multipleMVPActivity;
    }

    /**
     * 注册当个P的activity
     *
     * @param listener 创建P的回调接口,实现的接口中的泛型要对应具体的P
     * @param activity 当前activity
     * @param <P>      当前对应的P
     * @return 返回singmvp操作实例 {@link SingMVPActivity<P>},初始化调用{@link SingMVPActivity#init()}方法
     */
    public static <P extends ObjectPresenter> SingMVPActivity<P> registSingMvp(AppCompatActivity activity, CreateSingPresentListener<P> listener) {
        SingMVPActivity<P> singMVPActivity = (SingMVPActivity<P>) activityMap.get(activity.getClass().getSimpleName());
        if (singMVPActivity == null) {
            singMVPActivity = new SingMVPActivity<>(listener, activity);
            activityMap.put(activity.getClass().getSimpleName(), singMVPActivity);
        }
        activity.getApplication().registerActivityLifecycleCallbacks(singMVPActivity);
        return singMVPActivity;
    }

}
