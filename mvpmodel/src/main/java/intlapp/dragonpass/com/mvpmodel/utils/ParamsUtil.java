package intlapp.dragonpass.com.mvpmodel.utils;

import java.util.Map;

/**
 * Created by steam_l on 2018/11/5.
 * Desprition :
 */

public interface ParamsUtil {
    /**
     * 公共参数
     *
     * @return
     */
    Map<String, String> getPublicParams();

    /**
     * 公共headers
     *
     * @return
     */
    Map<String, String> getPublicHeaders();
}
