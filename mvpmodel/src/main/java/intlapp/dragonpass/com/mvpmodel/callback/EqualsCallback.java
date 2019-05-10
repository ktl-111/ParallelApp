package intlapp.dragonpass.com.mvpmodel.callback;

import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;

/**
 * Created by steam_l on 2019/1/29.
 * Desprition :对比回调
 */
public interface EqualsCallback<T> {
    /**
     * @param data
     * @return true-->不过滤  false-->过滤
     */
    boolean equalsData(ParaseData<T> data);
}
