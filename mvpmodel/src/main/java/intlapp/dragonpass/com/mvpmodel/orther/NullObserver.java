package intlapp.dragonpass.com.mvpmodel.orther;

import android.content.Context;

import intlapp.dragonpass.com.mvpmodel.base.ObjectObserver;

/**
 * 调用{@link intlapp.dragonpass.com.mvpmodel.base.ObservableBuilder#request(ObjectObserver)}时传递
 * 纯粹为了可以解析jsonbean
 *
 */
public class NullObserver<T> extends ObjectObserver<T> {
    public NullObserver() {
        this(null);
    }
    public NullObserver(Context context) {
        super(context);
    }

    @Override
    public void onSuccess(T data) {

    }

}
