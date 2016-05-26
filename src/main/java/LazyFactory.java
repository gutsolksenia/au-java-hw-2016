import javafx.util.Pair;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Created by Ксения on 24.05.2016.
 */
public class LazyFactory {

    public static<T> Lazy<T> createLazy(final Supplier<T> supplier){
        return new Lazy<T>() {
            private T result;
            private Boolean hasResult = false;
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
            private Boolean hasResult = false;
            public synchronized T get() {
                if(!hasResult) {
                    result = supplier.get();
                    hasResult = true;
                }
                return result;
            }
        };
    }

    public static<T> Lazy<T> createLockFreeLazy(final Supplier<T> supplier) {
        class LockFreeLazy implements Lazy {
            private volatile Object result = null;
            private AtomicReferenceFieldUpdater updaterResult = AtomicReferenceFieldUpdater.newUpdater(LockFreeLazy.class, Object.class, "result");
            public T get() {
                updaterResult.compareAndSet(this, null,
                        (Object) new Pair<>(supplier.get(), true));
                return ((Pair<T, Boolean>)result).getKey();
            }
        }
        return (Lazy<T>) new LockFreeLazy();
    }
}
