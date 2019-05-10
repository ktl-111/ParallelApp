package intlapp.dragonpass.com.mvpmodel.orther;

/**
 * Created by steam_l on 2019/2/2.
 * Desprition :
 */

public class TypeThrowable extends Throwable {
    public TypeThrowable(boolean isNetword, Throwable throwable) {
        this.isNetword = isNetword;
        this.throwable = throwable;
    }

    private boolean isNetword;
    private Throwable throwable;

    public boolean isNetword() {
        return isNetword;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
