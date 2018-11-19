package mock;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class TestExecutorService implements ExecutorService {
    private int count;
    private ExecutorService original;

    public TestExecutorService(int x){
        count = 0;
        original = Executors.newFixedThreadPool(x);
    }

    public int getCount() {
        return count;
    }

    @Override
    public void shutdown() {
        original.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return original.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return original.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return original.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return original.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        count++;
        return original.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        count++;
        return original.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        count++;
        return original.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return original.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return original.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return original.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return original.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        original.execute(command);
    }
}
