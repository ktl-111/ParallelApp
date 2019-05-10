package intlapp.dragonpass.com.mvpmodel.callback;

/**
 * Created by steam_l on 2019/1/29.
 * Desprition :重试等待时间回调
 */
public interface TimeCallback {
    /**
     * @param currCount 当前重试次数
     * @return 下一次等待多久后重试
     */
    long timeBack(int currCount);
}
