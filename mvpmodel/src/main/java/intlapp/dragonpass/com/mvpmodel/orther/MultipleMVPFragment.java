package intlapp.dragonpass.com.mvpmodel.orther;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObjectView;
import intlapp.dragonpass.com.mvpmodel.callback.CreateMultiplePresentListener;
import intlapp.dragonpass.com.mvpmodel.utils.HandleCallBackUtil;

/**
 * 多个presenter的mvpfragment
 * FragmentLifecycleCallbacks实例
 * 也是ObjectView实例,通过{@link HandleCallBackUtil}回调给{@link MultipleMVPFragment#init(ObjectView)}参数中的objectview
 * 同时有些方法可以调用,比如调用{@link MultipleMVPFragment#createPresents(ObjectPresenter[])} ()}创建P的集合
 * {@link MultipleMVPFragment#getPresent(Class)} 获取对应的P
 */

public class MultipleMVPFragment extends FragmentManager.FragmentLifecycleCallbacks implements ObjectView {
    private Map<String, ObjectPresenter> mMap;
    private ObjectView mHandleCallback;

    CreateMultiplePresentListener mCreatePresentListener;
    Fragment mFragment;

    public MultipleMVPFragment(CreateMultiplePresentListener listener, Fragment fragment) {
        mCreatePresentListener = listener;
        mFragment = fragment;
    }


    public void init() {
        init(null);
    }

    /**
     * @param view 一些成功失败提示和加载框,可以写个统一类
     */
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
    public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentDestroyed(fm, f);
        //解绑当前绑定callback
        if (f.equals(mFragment)) {
            Set<String> keys = mMap.keySet();
            for (String key : keys) {
                ObjectPresenter p = mMap.remove(key);
                if (p != null) {
                    p.detachView();
                }
            }
            fm.unregisterFragmentLifecycleCallbacks(this);
        }
    }

    /**
     * 自己创建list返回
     * 或者使用{@link MultipleMVPFragment#createPresents(ObjectPresenter[])}返回
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
     * @param <S>   具体需要的p
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
