package mein.core;

public final class Result<R>
{
    private static final Result VOID = new Result(null, null);

    private final R result;
    private Throwable error;

    public Result(R result, Throwable error) {
        this.result = result;
        this.error = error;
    }

    public static <R> Result<R> voidResult() {
        return VOID;
    }

    public static <R> Result<R> result(R result) {
        return new Result<R>(result, null);
    }

    public static <R> Result<R> error(Throwable error) {
        return new Result<R>(null, error);
    }

    public R get() {
        throwError();
        return result;
    }

    public Throwable getError() {
        return error;
    }

    public Result<R> throwError() {
        Throwable error = this.error;
        
        if (error == null) return this;
        
        if (error instanceof RuntimeException)
            throw (RuntimeException) error;
        
        if (error instanceof Error)
            throw (Error) error;
        
            throw new RuntimeException(error);
    }

    public Result<R> clearError() {
        this.error = null;
        return this;
    }
}
