package mein.io;

import java.util.concurrent.Callable;

import mein.util.Objects;
import mein.util.function.Function;

/* 
 * @Date 2022-10-30 23:40 +08:00
 */

public class AutoCloseUtil {

    private AutoCloseUtil() {
    }

    static class RunnableTryBlock implements AutoClose.Try<Void,RuntimeException> {

        private final Runnable target;

        RunnableTryBlock(Runnable target) {
            this.target = target;
        }

        @Override
        public Void run() throws RuntimeException {
            target.run();
            return null;
        }

    }

    static class CallableTryBlock<R> implements AutoClose.Try<R,Exception> {

        private final Callable<R> target;

        CallableTryBlock(Callable<R> target) {
            this.target = target;
        }

        @Override
        public R run() throws Exception {
            return target.call();
        }

    }

    static class CatchBlock<E extends Throwable, R> implements AutoClose.Catch<E,R> {

        private final Class<E> catchType;
        private final Function<? super E, R> handler;

        CatchBlock(Class<E> catchType, Function<? super E, R> handler) {
            this.catchType = catchType;
            this.handler = handler;
        }

        @Override
        public Class<E> getCatchType() {
            return catchType;
        }

        @Override
        public R handle(E e) {
            return handler.apply(e);
        }

    }


    public static AutoClose.Try<Void,RuntimeException> tryBlock(Runnable target) {
        Objects.requireNonNull(target, "target is null");
        return new RunnableTryBlock(target);
    }

    public static <R> AutoClose.Try<R,Exception> tryBlock(Callable<R> target) {
        Objects.requireNonNull(target, "target is null");
        return new CallableTryBlock<R>(target);
    }

    public static <E extends Throwable, R> AutoClose.Catch<E,R> catchBlock(Class<E> type, Function<? super E, R> handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        return new CatchBlock<E,R>(type, handler);
    }

}
