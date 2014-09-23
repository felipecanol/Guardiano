package system;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author felipe
 */
public class Config {

    private final Properties configProp = new Properties();
    private static Config INSTANCE = null;
    //    private static final String s = "\\"; // For Windows
    private static final String s = "/"; // For Unix

    public static Config getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Config();
            INSTANCE.Init(s + "system" + s + "usuarios.properties");
        }
        return INSTANCE;
    }

    public boolean Init(String url) {
        InputStream in;
        try {
            in = getClass().getClassLoader().getResourceAsStream(url); //new FileInputStream(url);
            configProp.load(in);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getProperty(String name) {
        return Config.getInstance().configProp.getProperty(name, "");
    }

    public static void setProperty(String name, String val) {
        Config.getInstance().configProp.setProperty(name, val);
    }
}
