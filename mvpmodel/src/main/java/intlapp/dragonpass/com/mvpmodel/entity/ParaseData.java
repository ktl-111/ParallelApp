package intlapp.dragonpass.com.mvpmodel.entity;

/**
 * Created by steam_l on 2019/1/29.
 * Desprition :封装类
 */
public class ParaseData<T> {
    public T data;//具体类
    public String result = "";//json数据
    public boolean cache;//是否是缓存
    public Throwable mThrowable;//error
}
