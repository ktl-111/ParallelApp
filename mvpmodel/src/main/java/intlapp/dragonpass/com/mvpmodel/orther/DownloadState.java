package intlapp.dragonpass.com.mvpmodel.orther;

/**
 * Created by steam_l on 2019/2/18.
 * Desprition :
 */

public interface DownloadState {
    int STATE_START = 1 << 1;        //开始
    int STATE_PAUSE = 1 << 2;     //暂停
    int STATE_SUCCESS = 1 << 3;      //下载完成
    int STATE_FAILED = 1 << 4;       //失败
    int STATE_WAIT = 1 << 5;         //等待
    int STATE_DOWNLOAD = 1 << 6;     //下载中
    int STATE_STOP = 1 << 7;         //停止
}
