package intlapp.dragonpass.com.mvpmodel.callback;


import intlapp.dragonpass.com.mvpmodel.base.ObjectObserver;
import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;

/**
 * Created by steam_l on 2018/11/1.
 * Desprition :处理结果的超类
 */

public abstract class HandleResult {

    public abstract <T> void handleResult(ParaseData<T> data, ObjectObserver<T> observer) throws Exception;
}
