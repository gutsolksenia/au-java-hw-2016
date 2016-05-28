import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created by Ксения on 24.05.2016.
 */
public class LazyFactory {

    public static<T> Lazy<T> createLazy(final Supplier<T> supplier){
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

    public static<T> Lazy<T> createThreadLazy(final Supplier<T> supplier) {
        return new Lazy<T>() {
            private T result;
            private boolean hasResult = false;
            public T get() {
                if(!hasResult) {
                    synchronized (supplier) {
                        result = supplier.get();
                        hasResult = true;
                    }
                }
                return result;
            }
        };
    }

    public static<T> Lazy<T> createLockFreeLazy(final Supplier<T> supplier) {
        class LockFreeLazy implements Lazy {
            private AtomicReference<T> result = new AtomicReference<>();
            public T get() {
                if (result.get() == null) {
                    result.compareAndSet(null, supplier.get());
                }
                return result.get();
            }
        }
        return (Lazy<T>) new LockFreeLazy();
    }
}
