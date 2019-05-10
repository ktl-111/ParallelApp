package intlapp.dragonpass.com.mvpmodel.orther;

/**
 * Created by liub on 2019/4/25.
 * Desprition :
 */

public class MyJsonPreconditions {
    private MyJsonPreconditions() {
        throw new UnsupportedOperationException();
    }

    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }
}
