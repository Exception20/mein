package mein.io;

import java.io.*;

public class FileIO
{
    public static class Input extends FileInputStream
    {
        public Input(String name) throws FileNotFoundException {
            super(name);
        }
        
        public Input(File file) throws FileNotFoundException {
            super(file);
        }
        
        public Input(FileDescriptor fdObj) {
            super(fdObj);
        }
        
        
        private void exceptionClose(Throwable e) {
            try {
                super.close();
            } catch (Throwable ex) {
                e.addSuppressed(ex);
            }
        }
        
        @Override
        public int read() throws UncheckedIOException {
            try {
                return super.read();
            } catch (IOException e) {
                exceptionClose(e);
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public int read(byte[] b) throws UncheckedIOException {
            try {
                return super.read(b);
            } catch (IOException e) {
                exceptionClose(e);
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public int read(byte[] b, int off, int len) throws UncheckedIOException {
            try {
                return super.read(b, off, len);
            } catch (IOException e) {
                exceptionClose(e);
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public long skip(long n) throws UncheckedIOException {
            try {
                return super.skip(n);
            } catch (IOException e) {
                exceptionClose(e);
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public int available() throws UncheckedIOException {
            try {
                return super.available();
            } catch (IOException e) {
                exceptionClose(e);
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


    public static class Output extends FileOutputStream
    {
        public Output(String name) throws FileNotFoundException {
            super(name);
        }
        
        public Output(String name, boolean append) throws FileNotFoundException {
            super(name, append);
        }
        
        public Output(File file) throws FileNotFoundException {
            super(file);
        }
        
        public Output(File file, boolean append) throws FileNotFoundException {
            super(file, append);
        }
        
        public Output(FileDescriptor fdObj) {
            super(fdObj);
        }
        
        
        private void exceptionClose(Throwable e) {
            try {
                super.close();
            } catch (Throwable ex) {
                e.addSuppressed(ex);
            }
        }
        
        @Override
        public void write(int b) throws UncheckedIOException {
            try {
                super.write(b);
            } catch (IOException e) {
                exceptionClose(e);
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public void write(byte[] b) throws UncheckedIOException {
            try {
                super.write(b);
            } catch (IOException e) {
                exceptionClose(e);
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws UncheckedIOException {
            try {
                super.write(b, off, len);
            } catch (IOException e) {
                exceptionClose(e);
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public void flush() throws UncheckedIOException {
            try {
                super.flush();
            } catch (IOException e) {
                exceptionClose(e);
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
            return new Input(name);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Input input(File file) {
        try {
            return new Input(file);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Input input(FileInputStream fis) {
        try {
            return new Input(fis.getFD());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(String name) {
        try {
            return new Output(name);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(String name, boolean append) {
        try {
            return new Output(name, append);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(File file) {
        try {
            return new Output(file);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(File file, boolean append) {
        try {
            return new Output(file, append);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Output output(FileOutputStream fos) {
        try {
            return new Output(fos.getFD());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] readAllBytes(String path) {
        return readAllBytes(new File(path));
    }

    public static byte[] readAllBytes(File file) {
        Input in = input(file);
        try {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return bytes;
        } finally {
            in.close();
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
