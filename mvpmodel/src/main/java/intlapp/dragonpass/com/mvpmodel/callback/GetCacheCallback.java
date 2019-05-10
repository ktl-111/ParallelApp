package intlapp.dragonpass.com.mvpmodel.callback;

import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;

/**
 * Created by steam_l on 2019/1/29.
 * Desprition :获取缓存回调
 */

public interface GetCacheCallback<T> {
    /**
     * @return 返回null相当于不拿缓存
     */
    ParaseData<T> returnCache();
}
