import java.util.function.Supplier;

/**
 * Created by Ксения on 24.05.2016.
 */
public class LazyFactory {

    public static <T> Lazy<T> createLazy(final Supplier<T> supplier) {
        return new Lazy<T>() {
            private T result;
            private boolean hasResult = false;

            public T get() {
                if (!hasResult) {
                    result = supplier.get();
                    hasResult = true;
                }
                return result;
            }
        };
    }

    public static <T> Lazy<T> createThreadLazy(final Supplier<T> supplier) {
        return new Lazy<T>() {
            private T result;
            private boolean hasResult = false;

            public T get() {
                if (!hasResult) {
                    synchronized (supplier) {
                        if (!hasResult) {
                            result = supplier.get();
                            hasResult = true;
                        }
                    }
                }
                return result;
            }
        };
    }

    public static <T> Lazy<T> createLockFreeLazy(final Supplier<T> supplier) {
        class LockFreeLazy implements Lazy<T> {
            private volatile T result;
            private volatile Supplier<T> sup = supplier;

            public T get() {
                if (sup != null) {
                    synchronized (this) {
                        if (sup != null) {
                            result = sup.get();
                            sup = null;
                        }
                    }
                }
                return result;
            }
        }
        ;
        return new LockFreeLazy();
    }
}
