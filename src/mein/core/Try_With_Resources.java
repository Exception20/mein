package mein.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import mein.util.Exceptions;

/* 
 * Created on 2022-03-04
 * last modified  2022-04-19  22:42  GMT+8
 * @author Exception20
 */

public class Try_With_Resources
{
    private final AutoCloseable[] resources;

    private ExtRunnable _try;

    private List<Entry<Class,Consumer>> _catch;

    private Runnable _finally;


    private Try_With_Resources(AutoCloseable[] resources) {
        this.resources = resources;
    }


    public static Try_With_Resources of(AutoCloseable... resources) {
        return new Try_With_Resources(resources);
    }


    private void set_try(ExtRunnable _try) {
        this._try = _try;
    }


    private void add_catch(Class exClass, Consumer exHandler) {
        if (_catch == null) _catch = new ArrayList<>(1);
        _catch.add(Entry.of(exClass, exHandler));
    }


    public Try_With_Resources try_(ExtRunnable _try) {
        if (_try == null)
            closeDueToNPE(new NullPointerException());
        
        set_try(_try);
        return this;
    }


    public Try_With_Resources try_(final Runnable _try) {
        if (_try == null)
            closeDueToNPE(new NullPointerException());
        
        set_try(new ExtRunnable() {
            public void runn() {
                _try.run();
            }
        });
        return this;
    }


    public Try_With_Resources catch_(Consumer<? super Exception> exceptionHandler) {
        if (exceptionHandler == null)
            closeDueToNPE(new NullPointerException("exceptionHandler == null"));
        
        add_catch(Exception.class, exceptionHandler);
        return this;
    }


    public <E extends Throwable> Try_With_Resources catch_(Class<E> exClass, Consumer<? super E> exceptionHandler) {
        if (exClass == null)
            closeDueToNPE(new NullPointerException("exClass == null"));
        
        if (exceptionHandler == null)
            closeDueToNPE(new NullPointerException("exceptionHandler == null"));
        
        add_catch(exClass, exceptionHandler);
        return this;
    }


    public Try_With_Resources finally_(Runnable _finally) {
        if (_finally == null)
            closeDueToNPE(new NullPointerException());
        
        this._finally = _finally;
        return this;
    }


    public void run() {
        try {
            run_try();
        } catch (Throwable ex) {
            run_catch(ex);
        } finally {
            run_finally();
        }
    }


    private void run_try() throws Throwable {
        ExtRunnable _try = this._try;
        Throwable primaryEx = null;
        try {
            if (_try != null) {
                _try.runn();
            }
        } catch (Throwable ex) {
            primaryEx = ex;
        } finally {
            close(primaryEx);
        }
    }


    private void run_catch(Throwable ex) {
        List<Entry<Class,Consumer>> _catch = this._catch;
        if (_catch == null)
            _catch = Collections.emptyList();
        
        for (Entry<Class,Consumer> entry : _catch) {
            if (entry.key.isInstance(ex)) {
                entry.value.accept(ex);
                return;
            }
        }
        
        if (ex instanceof RuntimeException)
            throw (RuntimeException) ex;
        
        if (ex instanceof Error)
            throw (Error) ex;
        
        throw Exceptions.wrap("Uncaught Exception", ex);
    }


    private void run_finally() {
        Runnable _finally = this._finally;
        if (_finally != null) {
            _finally.run();
        }
    }


    private void close(Throwable primaryEx) throws Throwable {
        try {
            for (AutoCloseable resource : resources) {
                try {
                    resource.close();
                } catch (Throwable ex) {
                    if (primaryEx != null) {
                        primaryEx.addSuppressed(ex);
                    } else {
                        primaryEx = ex;
                    }
                }
            }
        } catch (Throwable ex) {
            if (primaryEx != null) {
                primaryEx.addSuppressed(ex);
            } else {
                primaryEx = ex;
            }
        } finally {
            if (primaryEx != null) {
                throw primaryEx;
            }
        }
    }


    private void closeDueToNPE(NullPointerException npe) {
        try {
            close(npe);
        } catch (Throwable t) {
            // assert t == npe;
        } finally {
            throw npe;
        }
    }
}
