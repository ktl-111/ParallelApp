package intlapp.dragonpass.com.mvpmodel.base;

import android.content.Context;

/**
 * Created by steam_l on 2018/10/31.
 * Desprition :ui展示
 */

public interface ObjectView {

    void onSuccess(String msg);

    void onError(String msg);

    void showLoading();

    void hindeLoading();


}
