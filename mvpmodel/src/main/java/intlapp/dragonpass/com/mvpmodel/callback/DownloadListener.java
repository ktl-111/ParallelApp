package intlapp.dragonpass.com.mvpmodel.callback;


import android.content.Context;

import intlapp.dragonpass.com.mvpmodel.base.ObjectObserver;
import intlapp.dragonpass.com.mvpmodel.base.ObjectView;
import intlapp.dragonpass.com.mvpmodel.entity.DownloadEntity;

/**
 * Created by steam_l on 2019/2/19.
 * Desprition :
 */

public abstract class DownloadListener {
    ObjectView mHandleCallback;
    private boolean isShowLoading;

    public DownloadListener(Context context) {
        this(context, null);
    }

    public DownloadListener(Context context, ObjectObserver.Builder builder) {
        if (builder == null) {
            builder = new ObjectObserver.Builder();
        }
        if (builder.getMvpView() == null) {
            mHandleCallback = new HandleCallback(context);
        }
        isShowLoading = builder.isShowLoading();
    }

    /**
     * 开始下载
     *
     * @param entity
     */
    public void onStartDownload(DownloadEntity entity) {
        if (isShowLoading) {
            mHandleCallback.showLoading();
        }
    }

    /**
     * 进度
     *
     * @param entity
     */
    public void onProgress(DownloadEntity entity) {
    }

    /**
     * 暂停
     *
     * @param entity
     */
    public void onPause(DownloadEntity entity) {
    }

    /**
     * 完成下载
     *
     * @param entity
     */
    public void onFinishDownload(DownloadEntity entity) {
        if (isShowLoading) {
            mHandleCallback.hindeLoading();
        }
    }

    /**
     * 下载失败
     *
     * @param entity
     * @param throwable
     */
    public void onFiled(DownloadEntity entity, Throwable throwable) {
        if (isShowLoading) {
            mHandleCallback.hindeLoading();
        }
    }

    /**
     * 等待
     *
     * @param entity
     */
    public void onWait(DownloadEntity entity) {
    }

    /**
     * 停止
     *
     * @param entity
     */
    public void onStop(DownloadEntity entity) {
        if (isShowLoading) {
            mHandleCallback.hindeLoading();
        }
    }
}
