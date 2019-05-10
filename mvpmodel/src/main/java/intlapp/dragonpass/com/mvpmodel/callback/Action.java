package intlapp.dragonpass.com.mvpmodel.callback;

import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;

/**
 * Created by liub on 2019/4/23.
 * Desprition :处理数据前最后一次数据过度action
 */

public interface Action<T> {
    /**
     * 子线程
     *
     * @param data
     * @return
     */
    ParaseData<T> action(ParaseData<T> data);
}
