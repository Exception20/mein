package example.mein.core;

import example.mein.core.TryWithResourcesExample.TestResourceInputStream;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import mein.core.Cleaner;
import mein.core.Config;

/* 
 * 2022-05-17  22:10  GMT+8
 */

public class CleanerTest
{
    private final InputStream in;
    private final OutputStream out;

    CleanerTest(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        try { run0(); }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run0() throws IOException {
        byte[] buffer = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            if (in.read(buffer) == -1) {
                break;
            }
            out.write(buffer[i]);
        }
        out.flush();
    }


    private static Runnable newCloseAction(final AutoCloseable closeable,
                                           final Consumer<? super Exception> handler) {
        return new Runnable() { public void run() {
                try {
                    closeable.close();
                    System.out.println("close: " + closeable);
                } catch (Exception e) {
                    if (handler != null)
                        handler.accept(e);
                }
        }};
    }


    static class FileAccess {
        private static File tempFileDir = new File(Config.get("project.dir") + "/bin/temp/");
        static OutputStream newTempFileOutputStream(int bufferSize) {
            if (!tempFileDir.isDirectory()) {
                if (!tempFileDir.exists()) {
                    tempFileDir.mkdirs();
                } else { // is a file ?
                    if (tempFileDir.length() == 0) {
                        tempFileDir.delete();
                        tempFileDir.mkdirs();
                    }
                }
            }
            String name = "output_" + System.currentTimeMillis();
            File file = new File(tempFileDir, name);
            try {
                OutputStream out = new FileOutputStream(file);
                if (bufferSize > Byte.MAX_VALUE) {
                    out = new BufferedOutputStream(out, bufferSize);
                }
                return out;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


    //* static */ public static final Cleaner cleaner = Cleaner.create();
    public static void main(String[] args) {
        Cleaner cleaner = Cleaner.create();
        observation(cleaner);
        byte[] bytes = new byte[1024*1024];
        final ByteBuffer bbuf = ByteBuffer.wrap(bytes);
        new Random().longs(bytes.length>>3).forEach(new LongConsumer() {
            public void accept(long value) { bbuf.putLong(value); }
        });
        InputStream in = new TestResourceInputStream(bytes, false);
        OutputStream out = FileAccess.newTempFileOutputStream(1024);
        CleanerTest example = new CleanerTest(in, out);
        cleaner.register(example, newCloseAction(in, null));
        cleaner.register(example, newCloseAction(out, null));
        example.run();
    }


    private static void observation(Cleaner cleaner) {
        // ReferenceQueue
        final Object queue = Access.get(Access.getField(cleaner.getClass(), "queue"), cleaner);
        // ReferenceQueue.lock
        final Object lock = Access.get(Access.getField(queue.getClass(), "lock"), queue);
        // Cleaner.CCLinkedList
        final Object list = Access.get(Access.getField(cleaner.getClass(), "list"), cleaner);
        // CCLinkedList.size
        final Field size = Access.getField(list.getClass(), "size");
        new Thread(new Runnable() { public void run() {
            synchronized (lock) {
                int listSize = Integer.MAX_VALUE;
                while (listSize > 0) {
                    listSize = Access.get(size, list);
                    System.out.println("size: " + listSize);
                    try { lock.wait(1000L); }
                    catch (InterruptedException e) {
                        System.out.println("Interrupted");
                        return;
                    }
                }
            }
            System.out.println("ObserverThread end");
        }}).start();
    }


    private static class Access {
        private static Field getField(Class clazz, String name) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            }
        }
        private static Object get(Field field, Object object) {
            try {
                return field.get(object);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
