import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Properties {

    private static final java.util.Properties properties;

    static{
        properties = new java.util.Properties();
        try {
            InputStream in = new FileInputStream("config.properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
