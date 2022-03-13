package mein.example.mein.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import mein.core.ExtRunnable;
import mein.core.Try_With_Resources;

public class $Try_With_Resources
{
    static class TestResourceInputStream extends InputStream
    {
        private int index;
        private byte[] src;
        private boolean throwException;
        private boolean closed;
        
        TestResourceInputStream(byte[] src, boolean throwException) {
            this.src = src;
            this.throwException = throwException;
        }

        @Override
        public int read() throws IOException {
            if (throwException)
                throw new IOException();
            
            try {
                return src[index++];
            } catch (ArrayIndexOutOfBoundsException e) {
                return -1;
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (throwException) throw new IOException();
            
            len = Math.min(len, src.length-index);
            System.arraycopy(src, index, b, off, len);
            index += len;
            return len;
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
            "1145141919810 やりますね".getBytes(),
            false // if true, the read() method throws an IOException
        );
        OutputStream out = null; // NullPointerException
        try {
            new $Try_With_Resources().example(in, out);
        } catch (Exception e) {
            System.out.println("is closed: " + in.isClosed());
            e.printStackTrace();
        }
    }


    void example(final InputStream in, final OutputStream out) {

        Try_With_Resources.of(in, out).try_(new ExtRunnable() {
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

        Try_With_Resources.of(in, out).try_(new ExtRunnable() {
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
