package intlapp.dragonpass.com.mvpmodel.entity;

import java.io.File;

import intlapp.dragonpass.com.mvpmodel.orther.DownloadState;

/**
 * Created by steam_l on 2019/2/18.
 * Desprition :
 */

public class DownloadEntity {
    public String download_url;//下载链接

    public File file;//保存路劲

    public long currlength = 0;

    public long totallength = 0;

    public int state = DownloadState.STATE_WAIT;
    public Throwable mThrowable;
}
