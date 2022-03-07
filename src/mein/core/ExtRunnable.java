package mein.core;

/* 
 * Extended java.lang.Runnable Interface
 * allow throw Exception
 */

@FunctionalInterface
public interface ExtRunnable
{
    public void runn() throws Exception;
}
