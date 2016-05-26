import org.junit.Test;

import java.util.Random;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Ксения on 24.05.2016.
 */

public class LazyFactoryTest {
    private class IntSupplier implements Supplier<Integer> {
        private Random random = new Random();
        public Integer get() {
            return random.nextInt(10000);
        }
    };

    @Test
    public void createLazyTest() {
        Supplier<Integer> supplier = new IntSupplier();
        Lazy<Integer>[] lazyList = new Lazy[100];

        for (int i = 0; i < 100; i++)
            lazyList[i] = LazyFactory.createLazy(supplier);

        for (int i = 0; i < 100; i++)
            assertEquals(lazyList[i].get(), lazyList[i].get());
    }

    @Test
    public void createThreadLazyTest() {
        Supplier<Integer> supplier = new IntSupplier();
        final Lazy<Integer> lazy = LazyFactory.createThreadLazy(supplier);
        Thread[] threads = new Thread[20];
        final Integer[] res = new Integer[20];

        for (int j = 0; j < 20; j++) {
            final int tmp = j;
            threads[j] = new Thread(new Runnable() {
                @Override
                public void run() {
                    res[tmp] = lazy.get();
                }
            });
        }

        for (int i = 0; i < 20; i++)
            threads[i].start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        for (int i = 1; i < 20; i++)
            assertEquals (res[i], res[i - 1]);
    }

    @Test
    public void lockFreeTest() {
        Supplier<Integer> supplier = new IntSupplier();
        final Lazy<Integer> lazy = LazyFactory.createLockFreeLazy(supplier);
        Thread[] threads = new Thread[20];
        final Integer[] res = new Integer[20];

        for (int j = 0; j < 20; j++) {
            final int tmp = j;
            threads[j] = new Thread(new Runnable() {
                @Override
                public void run() {
                    res[tmp] = lazy.get();
                }
            });
        }

        for (int i = 0; i < 20; i++)
            threads[i].start();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < 20; i++)
            assertEquals (res[i], res[i - 1]);
    }

}
