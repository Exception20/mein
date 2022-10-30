package mein.util;

public class Objects {

    public static void requireNonNull(Object obj) {
        if (obj == null) throw new NullPointerException();
    }

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) throw new NullPointerException(message);
    }

}
