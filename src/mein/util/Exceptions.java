package mein.util;

/* 
 * 2022-04-19  22:38  GMT+8
 */

public class Exceptions
{
    private Exceptions() throws Exception {
        throw new InstantiationException();
    }


    /* shared empty stack trace */
    public static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];


    public static RuntimeException wrap(String message, Throwable cause) {
        RuntimeException ex = new RuntimeException(message, cause);
        ex.setStackTrace(EMPTY_STACK_TRACE);
        return ex;
    }

    public static RuntimeException wrap(Throwable cause) {
        RuntimeException ex = new RuntimeException(cause);
        ex.setStackTrace(EMPTY_STACK_TRACE);
        return ex;
    }

}
