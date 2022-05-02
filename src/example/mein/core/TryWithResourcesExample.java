package example.mein.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import mein.core.ExtRunnable;
import mein.core.TryWithResources;

/* 
 * 2022-05-02  10:21  GMT+8
 */

public class TryWithResourcesExample
{
    static class TestResourceInputStream extends FilterInputStream
    {
        private boolean throwException;
        private boolean closed;

        TestResourceInputStream(byte[] src, boolean throwException) {
            super(new ByteArrayInputStream(src));
            this.throwException = throwException;
        }


        @Override
        public int read() throws IOException {
            if (throwException)
                throw new IOException();

            return in.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (throwException)
                throw new IOException();

            return in.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }


    public static void main(String[] args) {
        TestResourceInputStream in = new TestResourceInputStream(
            "1145141919810".getBytes(),
            true // if true, the read() method throws an IOException
        );
        OutputStream out = new ByteArrayOutputStream();
        try {
            new TryWithResourcesExample().example(in, out);
        } catch (Exception e) {
            System.out.println("is closed: " + in.isClosed());
            e.printStackTrace();
        }
    }


    void example(final InputStream in, final OutputStream out) {

        TryWithResources.of(in, out).try_(new ExtRunnable() {
            @Override public void runn() throws IOException {
                byte[] buffer = new byte[1];
                int total;
                while ((total = in.read(buffer)) != -1) {
                    out.write(buffer, 0, total);
                }
                out.flush();
            }
        }).run();
    }


    void example2(final InputStream in, final OutputStream out) {

        TryWithResources.of(in, out).try_(new ExtRunnable() {
            @Override public void runn() throws IOException {
                byte[] buffer = new byte[1];
                int total;
                while ((total = in.read(buffer)) != -1) {
                    out.write(buffer, 0, total);
                }
                out.flush();
            }
        }).catch_(IOException.class, new Consumer<IOException>() {
            @Override public void accept(IOException e) {
                System.out.println("oh, :(");
                e.printStackTrace();
            }
        }).run();
    }

}
