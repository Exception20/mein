package mein.io;

import java.io.*;
import java.nio.file.Files;

/* 
 * 2022-05-02  09:55  GMT+8
 */

public class FileIO
{
    static void exceptionClose(Closeable c, IOException e) {
        try {
            c.close();
        } catch (Throwable t) {
            e.addSuppressed(t);
        }
    }


    public static class Input extends FilterInputStream
    {
        public Input(InputStream in) {
            super(in);
        }


        @Override
        public int read() throws UncheckedIOException {
            try {
                return in.read();
            } catch (IOException e) {
                exceptionClose(in, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public int read(byte[] b) throws UncheckedIOException {
            try {
                return in.read(b);
            } catch (IOException e) {
                exceptionClose(in, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws UncheckedIOException {
            try {
                return in.read(b, off, len);
            } catch (IOException e) {
                exceptionClose(in, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public long skip(long n) throws UncheckedIOException {
            try {
                return in.skip(n);
            } catch (IOException e) {
                exceptionClose(in, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public int available() throws UncheckedIOException {
            try {
                return in.available();
            } catch (IOException e) {
                exceptionClose(in, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void close() throws UncheckedIOException {
            try {
                in.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


    public static class Output extends FilterOutputStream
    {
        public Output(OutputStream out) {
            super(out);
        }


        @Override
        public void write(int b) throws UncheckedIOException {
            try {
                out.write(b);
            } catch (IOException e) {
                exceptionClose(out, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void write(byte[] b) throws UncheckedIOException {
            try {
                out.write(b);
            } catch (IOException e) {
                exceptionClose(out, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws UncheckedIOException {
            try {
                out.write(b, off, len);
            } catch (IOException e) {
                exceptionClose(out, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void flush() throws UncheckedIOException {
            try {
                out.flush();
            } catch (IOException e) {
                exceptionClose(out, e);
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void close() throws UncheckedIOException {
            try {
                super.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


    public static Input input(String name) {
        try {
            InputStream in = new FileInputStream(name);
            return new Input(in);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Input input(File file) {
        try {
            InputStream in = new FileInputStream(file);
            return new Input(in);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Input input(InputStream in) {
        return new Input(in);
    }

    public static Output output(String name) {
        try {
            OutputStream out = new FileOutputStream(name);
            return new Output(out);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(String name, boolean append) {
        try {
            OutputStream out = new FileOutputStream(name, append);
            return new Output(out);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            return new Output(out);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(File file, boolean append) {
        try {
            OutputStream out = new FileOutputStream(file, append);
            return new Output(out);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(OutputStream out) {
        return new Output(out);
    }

    public static byte[] readAllBytes(String path) {
        return readAllBytes(new File(path));
    }

    public static byte[] readAllBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(File file, byte[] bytes) {
        write(file, false, bytes);
    }

    public static void write(File file, boolean append, byte[] bytes) {
        Output out = output(file, append);
        try {
            out.write(bytes);
        } finally {
            out.close();
        }
    }
}
