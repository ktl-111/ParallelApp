package intlapp.dragonpass.com.mvpmodel.db;

import intlapp.dragonpass.com.mvpmodel.entity.DownloadEntity;
import io.paperdb.Paper;

/**
 * Created by steam_l on 2019/2/19.
 * Desprition :
 */

public class DownloadDBHelper {

    public static void insert(DownloadEntity entity) {
        Paper.book().write(entity.download_url.replace("/","").replace(".",""), entity);
    }

    public static void update(DownloadEntity entity) {
        insert(entity);
    }

    public static void delete(DownloadEntity entity) {
        Paper.book().delete(entity.download_url.replace("/","").replace(".",""));
    }

    public static DownloadEntity query(String url) {
        return Paper.book().read(url.replace("/","").replace(".",""), null);
    }
}
