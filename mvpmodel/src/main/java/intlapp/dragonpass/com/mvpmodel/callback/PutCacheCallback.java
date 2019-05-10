package intlapp.dragonpass.com.mvpmodel.callback;

import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;

/**
 * Created by steam_l on 2019/1/29.
 * Desprition :保存缓存回调
 */
public interface PutCacheCallback<T> {

    /**
     * @param data 网络请求的data,默认缓存不会走回调
     *               可以重写{@link intlapp.dragonpass.com.mvpmodel.base.ObjectObserver#isPutCache(ParaseData)}方法修改
     */
    void putCache(ParaseData<T> data);
}
