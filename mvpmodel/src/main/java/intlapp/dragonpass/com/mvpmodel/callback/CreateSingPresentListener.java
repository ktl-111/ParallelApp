package intlapp.dragonpass.com.mvpmodel.callback;


import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;

/**
 * Created by steam_l on 2018/11/5.
 * Desprition : 创建p接口
 */

public interface CreateSingPresentListener<P extends ObjectPresenter> {
    P createPresent();
}
