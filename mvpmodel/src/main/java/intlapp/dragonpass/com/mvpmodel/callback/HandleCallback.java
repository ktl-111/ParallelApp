package intlapp.dragonpass.com.mvpmodel.callback;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;

import intlapp.dragonpass.com.mvpmodel.base.ObjectView;

/**
 * Created by steam_l on 2018/11/2.
 * Desprition : 统一处理一些信息和加载框的显示
 */

public class HandleCallback implements ObjectView {
    WeakReference<Context> mReference;
    private String sName;

    public HandleCallback(FragmentActivity activity) {
        if (activity != null) {
            mReference = new WeakReference<Context>(activity);
            sName = activity.getClass().getSimpleName();
        }
    }

    public HandleCallback(Context context) {
        if (context != null) {
            mReference = new WeakReference<Context>(context);
            sName = context.getClass().getSimpleName();
        }
    }

    public HandleCallback(Fragment fragment) {
        if (fragment != null) {
            mReference = new WeakReference<Context>(fragment.getContext());
            sName = fragment.getClass().getSimpleName();
        }
    }

    public HandleCallback setName(String name) {
        sName = name;
        return this;
    }

    @Override
    public void onSuccess(String msg) {
    }

    @Override
    public void onError(String msg) {
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hindeLoading() {
    }

}
