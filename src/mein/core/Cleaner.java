package mein.core;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

/* 
 * Cleaner
 * 2022-05-17  16:34:25  GMT+8
 */

public final class Cleaner
{
    final CCLinkedList list;
    final ReferenceQueue queue;

    private Cleaner() {
        this.list = new CCLinkedList();
        this.queue = new ReferenceQueue();
        CleanerCleanable first = new CleanerCleanable(this, this, null);
        list.add(first); // register self, list.size > 0
        // The cleaner will end when the cleaner itself is phantom reachable
        // and all registered clean actions are completed. (list.size == 0)
    }


    /**
     * Create a new Cleaner
     * The cleaner creates a daemon thread to process
     * the phantom reachable objects and to invoke cleaning actions
     * 
     * @return a new Cleaner
     */
    public static Cleaner create() {
        Cleaner cleaner = new Cleaner();
        CleanerThreadFactory.getInstance()
                            .newThread(new Clean(cleaner)).start();
        return cleaner;
    }

    /**
     * Create a new Cleaner using a Thread from the ThreadFactory.
     * Unlike java.lang.ref.Cleaner, this method does not set the thread returned by
     * {@link ThreadFactory#newThread(Runnable) newThread} as a
     * {@link Thread#setDaemon(boolean) daemon thread} but start it directly.
     * So you can decide for yourself whether the thread runs as a daemon thread,
     * just set it before the newThread method returns
     * 
     * @param factory  return a Thread to run clean actions
     * @return a new Cleaner
     */
    public static Cleaner create(ThreadFactory factory) {
        Objects.requireNonNull(factory, "factory");
        Cleaner cleaner = new Cleaner();
        factory.newThread(new Clean(cleaner)).start();
        return cleaner;
    }

    /**
     * Registers an object and a cleaning action to run when the object becomes phantom reachable
     * 
     * @param obj  the object to monitor
     * @param action  a Runnable to invoke when the object becomes phantom reachable
     * @return a Cleanable instance
     */
    public Cleanable register(Object obj, Runnable action) {
        Objects.requireNonNull(obj, "obj");
        Objects.requireNonNull(action, "action");
        CleanerCleanable cc = new CleanerCleanable(this, obj, action);
        list.add(cc);
        return cc;
    }


    /* 
     * Default Thread Factory
     */
    private static class CleanerThreadFactory implements ThreadFactory {
        private CleanerThreadFactory() {}

        private static final CleanerThreadFactory INSTANCE = new CleanerThreadFactory();
        public static final CleanerThreadFactory getInstance() { return INSTANCE; }

        private int threadNumber;
        private String name() {
            int num;
            synchronized (this) {
                num = threadNumber++;
            }
            return "Cleaner-" + num;
        }

        public Thread newThread(Runnable target) {
            Thread thread = new Thread(target, name());
            thread.setDaemon(true);
            return thread;
        }
    }


    private static class Clean implements Runnable {
        private final CCLinkedList list;
        private final ReferenceQueue queue;

        private Clean(Cleaner cleaner) {
            this.list = cleaner.list;
            this.queue = cleaner.queue;
        }

        @Override
        public void run() {
            while (list.size() > 0) {
                try {
                    Reference ref = queue.remove();
                    ((Cleanable)ref).clean();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }


    private static class CleanerCleanable extends PhantomReference implements Cleanable {
        CleanerCleanable    prev,    next;

        CCLinkedList list;

        Runnable action;

        private CleanerCleanable(Cleaner cleaner, Object obj, Runnable action) {
            super(obj, cleaner.queue);
            this.action = action;
        }


        @Override
        public void clean() {
            CCLinkedList.remove(this);
            Runnable r;
            synchronized (this) {
                r = this.action;
                    this.action = null;
            }
            if (r != null) {
                r.run();
            }
        }
    }


    private static class CCLinkedList {
        private CCLinkedList() {}

        private CleanerCleanable first;

        private int size;

        public int size() {
            return size;
        }

        synchronized void add(CleanerCleanable cc) {
            if (cc.list != null) throw new RuntimeException(
                "the reference(cc.list) should be null, but it is not"
            );
            CleanerCleanable first = this.first;
            if (first != null) {
                first.prev = cc;
                cc.next = first;
            }
            this.first = cc;
            cc.list = this;
            size++;
        }

        private static CCLinkedList takeAwayList(CleanerCleanable cc) {
            synchronized (cc) {
                CCLinkedList list = cc.list;
                cc.list = null;
                return list;
            }
        }

        static void remove(CleanerCleanable cc) {
            if (cc.list == null) return;
            CCLinkedList list = takeAwayList(cc);
            if (list == null) return;
            synchronized (list) {
                CleanerCleanable next = cc.next;
                CleanerCleanable prev = cc.prev;
                if (next != null) {
                    next.prev = prev;
                }
                if (prev != null) {
                    prev.next = next;
                } else if (cc == list.first) {
                    list.first = next;
                } else throw new NullPointerException(
                    cc + " is not 'list.first', but its prev is null"
                );
                list.size--;
            }
            cc.prev = cc.next = null;
        }

    }


}
