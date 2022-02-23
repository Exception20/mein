package mein.core;

import java.io.FileReader;
import java.util.Properties;

public final class Config
{
    private static final Properties prop;

    static {
        init(prop = new Properties());
    }

    private static void init(Properties prop) {
        FileReader reader = null;
        try {
            prop.load(reader = new FileReader(
                "/storage/emulated/0/AppProjects/mein/src/Config.properties"
            ));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Config() {}


    public static String get(String key) {
        return prop.getProperty(key);
    }
}
