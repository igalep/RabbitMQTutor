package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * A singleton for properties reader
 * Created by epshtein.
 * Date: 03/10/2018
 */
public final class PropertiesReaderSingleton {
    private static PropertiesReaderSingleton instance;

    private Properties properties;
    private PropertiesReaderSingleton() {
        try {
            properties = new Properties();
            Files.newDirectoryStream(Paths.get("./src/main/resources"), path -> path.toFile().isFile()).
                    forEach(value -> {
                        if (value.toString().endsWith("properties")) {
                            Properties currentProp = new Properties();
                            try {
                                currentProp.load(this.getClass().getClassLoader().getResourceAsStream(value.toFile().getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            properties.putAll(currentProp);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesReaderSingleton getInstance(){
        if (instance == null)
            instance = new PropertiesReaderSingleton();

        return instance;

    }

    /**
     * return specific property value by provided key
     * @param key is the key to return property for.
     * @return required property as int
     */
    public int getPropertyAsInteger(String key){
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * return specific property value by provided key
     * @param key is the key to return property for.
     * @return required property as long
     */
    public long getPropertyAsLong(String key){
        return Long.parseLong(properties.getProperty(key));
    }


    /**
     * return specific property value by provided key
     * @param key is the key to return property for.
     * @return required property as string
     */
    public String getPropertyAsString(String key){
        return properties.getProperty(key);
    }
}