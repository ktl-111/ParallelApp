package intlapp.dragonpass.com.mvpmodel.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by steam_l on 2018/10/31.
 * Desprition :
 */

public abstract class ObjectPresenter<V> {
    protected V mMvpView;
    protected CompositeDisposable mDisposable;
    protected ObjectView mObjectView;

    public ObjectPresenter(V mvpView) {
        this.mMvpView = mvpView;
    }

    public void setObjectView(ObjectView view) {
        mObjectView = view;
    }

    public void detachView() {
        mMvpView = null;
        clearDisposable();
    }

    //RXjava取消注册，以避免内存泄露
    public void clearDisposable() {
        if (mDisposable != null && mDisposable.size() != 0) {
            mDisposable.clear();
        }
    }


    public void addDisposable(Disposable disposable) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    public static String getName(ObjectPresenter presenter) {
        return presenter.getClass().getSimpleName();
    }
}
