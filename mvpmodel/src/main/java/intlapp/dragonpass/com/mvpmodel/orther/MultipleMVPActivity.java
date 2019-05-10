package intlapp.dragonpass.com.mvpmodel.orther;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObjectView;
import intlapp.dragonpass.com.mvpmodel.callback.CreateMultiplePresentListener;
import intlapp.dragonpass.com.mvpmodel.callback.MyActivityLifecycleCallbacks;
import intlapp.dragonpass.com.mvpmodel.utils.HandleCallBackUtil;

/**
 * 多个presenter
 */

public class MultipleMVPActivity extends MyActivityLifecycleCallbacks implements ObjectView {
    private final CreateMultiplePresentListener mCreatePresentListener;
    private Map<String, ObjectPresenter> mMap;
    private ObjectView mHandleCallback;
    AppCompatActivity mActivity;

    public MultipleMVPActivity(CreateMultiplePresentListener listener, AppCompatActivity activity) {
        mCreatePresentListener = listener;
        mActivity = activity;
    }

    public void init() {
        init(null);
    }

    public void init(ObjectView view) {
        mMap = new ConcurrentHashMap<>();
        List<ObjectPresenter> presenters = createPresent();
        for (ObjectPresenter p : presenters) {
            p.setObjectView(this);
            mMap.put(p.getClass().getSimpleName(), p);
        }
        mHandleCallback = view;
    }


    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity.equals(mActivity)) {
            Set<String> keys = mMap.keySet();
            for (String key : keys) {
                ObjectPresenter p = mMap.remove(key);
                if (p != null) {
                    p.detachView();
                }
            }
            activity.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }

    /**
     * 自己返回
     * 或者使用该方法创建List{@link MultipleMVPActivity#createPresents(ObjectPresenter[])} ()}
     *
     * @return
     */
    protected List<ObjectPresenter> createPresent() {
        return mCreatePresentListener.createPresent();
    }

    /**
     * 创建p的list
     *
     * @param p
     * @return
     */
    public List<ObjectPresenter> createPresents(ObjectPresenter... p) {
        return Arrays.asList(p);
    }

    /**
     * 查找添加的P
     *
     * @param clazz
     * @return
     */
    public <S extends ObjectPresenter> S getPresent(Class<S> clazz) {
        ObjectPresenter p = mMap.get(clazz.getSimpleName());
        if (p == null) {
            throw new RuntimeException("You didn't add " + clazz.getSimpleName() + ",Pleasr add first.");
        }
        return (S) p;
    }


    @Override
    public void onSuccess(String msg) {
        HandleCallBackUtil.onSuccess(mHandleCallback, msg);
    }

    @Override
    public void onError(String msg) {
        HandleCallBackUtil.onError(mHandleCallback, msg);
    }

    @Override
    public void showLoading() {
        HandleCallBackUtil.showLoading(mHandleCallback);
    }

    @Override
    public void hindeLoading() {
        HandleCallBackUtil.hindeLoading(mHandleCallback);
    }

}
