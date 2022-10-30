package mein.io;

import java.io.Closeable;
import java.util.List;

/* 
 * @Date 2022-10-30 20:22 +08:00
 */

public class AutoClose {

    public static interface Try<R, E extends Throwable> {
        R run() throws E;
    }


    public static interface Catch<E extends Throwable, R> {
        Class<E> getCatchType();
        R handle(E e);
    }


    private final Closeable[] resources;


    private AutoClose(Closeable[] resources) {
        this.resources = resources;
    }


    public static AutoClose of(Closeable... resources) {
        return new AutoClose(resources);
    }


    public void close() {
        for (Closeable c : resources) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    // suppressed
                }
            }
        }
    }


    public <R, E extends Throwable> R run(Try<R,E> tryBlock) throws E {
        try {
            return tryBlock.run();
        } finally {
            close();
        }
    }


    public <R, E extends Throwable> R run(Try<R,E> tryBlock, Catch<E,R> catchBlock) {
        try {
            return run(tryBlock);
        } catch (Throwable e) {
            if (canCatch(catchBlock, e)) {
                return catchBlock.handle(catchBlock.getCatchType().cast(e));
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new RuntimeException("This catchBlock cannot catch this Exception", e);
            }
        }
    }


    public <R> R run(Try<R,? extends Throwable> tryBlock, List<Catch<Throwable,R>> catchBlocks) {
        try {
            return run(tryBlock);
        } catch (Throwable e) {
            for (Catch<Throwable,R> catchBlock : catchBlocks) {
                if (canCatch(catchBlock, e)) {
                    return catchBlock.handle(e);
                }
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new RuntimeException("No catchBlock can catch this Exception", e);
            }
        }
    }


    public <R, E extends Throwable> R run(Try<R,E> tryBlock, Catch<E,R> catchBlock, Runnable finallyBlock) {
        try {
            return run(tryBlock, catchBlock);
        } finally {
            finallyBlock.run();
        }
    }


    public <R> R run(Try<R,? extends Throwable> tryBlock, List<Catch<Throwable,R>> catchBlocks, Runnable finallyBlock) {
        try {
            return run(tryBlock, catchBlocks);
        } finally {
            finallyBlock.run();
        }
    }


    public <R, E extends Throwable> R run(Try<R,E> tryBlock, Runnable finallyBlock) throws E {
        try {
            return run(tryBlock);
        } finally {
            finallyBlock.run();
        }
    }


    private static boolean canCatch(Catch<? extends Throwable, ?> catchBlock, Throwable e) {
        if (catchBlock == null) {
            final NullPointerException npe = new NullPointerException("catchBlock is null");
            npe.initCause(e);
            throw npe;
        }

        final Class<? extends Throwable> clazz = catchBlock.getCatchType();
        if (clazz == null) {
            final NullPointerException npe = new NullPointerException("catchBlock.getCatchType() returned null");
            npe.initCause(e);
            throw npe;
        }

        return clazz.isInstance(e);
    }

}
