package intlapp.dragonpass.com.mvpmodel.orther;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObjectView;
import intlapp.dragonpass.com.mvpmodel.callback.CreateSingPresentListener;
import intlapp.dragonpass.com.mvpmodel.utils.HandleCallBackUtil;


/**
 * 单个P的mvpfragment
 *
 * @param <P> 具体的P
 */
public class SingMVPFragment<P extends ObjectPresenter> extends FragmentManager.FragmentLifecycleCallbacks implements ObjectView {
    protected P mPresenter;
    private ObjectView mHandleCallback;
    CreateSingPresentListener<P> mCreatePresentListener;
    Fragment mFragment;

    public SingMVPFragment(CreateSingPresentListener<P> listener, Fragment fragment) {
        mCreatePresentListener = listener;
        mFragment = fragment;
    }

    public void init() {
        init(null);
    }

    public void init(ObjectView view) {
        mPresenter = createPresent();
        mPresenter.setObjectView(this);
        mHandleCallback = view;
    }

    @Override
    public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentDestroyed(fm, f);
        if (f.equals(mFragment)) {
            if (mPresenter != null) {
                mPresenter.detachView();
            }
            fm.unregisterFragmentLifecycleCallbacks(this);
        }
    }

    protected P createPresent() {
        return mCreatePresentListener.createPresent();
    }

    public P getPresenter() {
        return mPresenter;
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
