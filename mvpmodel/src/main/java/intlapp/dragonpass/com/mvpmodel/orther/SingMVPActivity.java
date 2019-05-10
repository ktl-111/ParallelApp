package intlapp.dragonpass.com.mvpmodel.orther;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObjectView;
import intlapp.dragonpass.com.mvpmodel.callback.CreateSingPresentListener;
import intlapp.dragonpass.com.mvpmodel.callback.MyActivityLifecycleCallbacks;
import intlapp.dragonpass.com.mvpmodel.utils.HandleCallBackUtil;


/**
 * 单个presenter
 *
 * @param <P>具体的presenter
 */
public class SingMVPActivity<P extends ObjectPresenter> extends MyActivityLifecycleCallbacks implements ObjectView {

    protected P mPresenter;
    private ObjectView mHandleCallback;
    CreateSingPresentListener<P> mCreatePresentListener;
    AppCompatActivity mActivity;

    public SingMVPActivity(CreateSingPresentListener<P> listener, AppCompatActivity activity) {
        mCreatePresentListener = listener;
        mActivity = activity;
    }

    public void init() {
        init(null);
    }

    public void init(ObjectView view) {
        mPresenter = createPresenter();
        mHandleCallback = view;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
        if (activity.equals(mActivity)) {

            if (mPresenter != null) {
                mPresenter.detachView();
            }
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }

    /**
     * @return 返回具体的Persenter
     */
    protected P createPresenter() {
        return mCreatePresentListener.createPresent();
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
