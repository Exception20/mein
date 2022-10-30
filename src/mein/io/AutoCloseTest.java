package mein.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;

import mein.util.function.Function;

/* 
 * @
 */

public class AutoCloseTest {

    static class Resource implements Closeable {

        private final String name;

        Resource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Resource[$name]".replace("$name", String.valueOf(name));
        }

        @Override
        public void close() throws IOException {
            System.out.write("close: $resource\n".replace("$resource", toString()).getBytes());
        }

    }


    public static void main(String[] args) {
        final Resource resource = new Resource("src/mein/io/AutoClose.java");
        final int a = 0;
        final int b = 1;
        final int c = AutoClose.of(resource).run(
            new AutoClose.Try<Integer,RuntimeException>() {
                public Integer run() {
                    return a - b;
                }
            }
        );
        System.out.println(
            "$a - $b = $c"
            .replace("$a", String.valueOf(a))
            .replace("$b", String.valueOf(b))
            .replace("$c", String.valueOf(c))
        );
    }

}
