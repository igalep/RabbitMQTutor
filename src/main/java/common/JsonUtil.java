package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import connection.DriverManager;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Created by epshtein.
 * Date: 2019-01-01
 */
public class JsonUtil {

    private JSONObject jsonObject;
    /**
     * Initializing desired Class with Json parameters
     */
    public static <T> T createClassFromJson (String json, Class<T> classOfT) throws Throwable{
        InputStream is = Files.newInputStream(Paths.get(json));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(is, classOfT);
    }

    /**
     *
     * @param jsonObject contains url info - ip, port, path
     * @return full url path
     */
    public static String urlFronjson (JSONObject jsonObject){
        String result = "http://"+jsonObject.get("ip").toString()+":"+jsonObject.get("port").toString()+jsonObject.get("path").toString();
        return  result;
    }

    public static String urlForDevice (String port){
        String result = "http://127.0.0.1:"+port+"/wd/hub";
        return  result;
    }

    public static String urlFronjsonMultipleDevices (JSONObject jsonObject, int deviceIndex){
        String result = "http://"+jsonObject.get("ip").toString()+":"+jsonObject.get("port")+Integer.toString(deviceIndex)+jsonObject.get("path").toString();
        return  result;
    }

    /**
     * @param file json file
     * @return  return JSONObject of the file
     */
    public static JSONObject readFile(String file) {
        InputStream resourceAsStream = DriverManager.class.getClassLoader().getResourceAsStream(file);
        if (resourceAsStream == null) {
            System.out.println("---ERROR Cannot find resource file " + "-" + file);
            return null;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {

            return new JSONObject(br.lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            System.out.println("---ERROR Cannot buffer-read");
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param jsonObject json file in JSONObject format
     * @param filter needed sub section in json
     * @return
     */
    public  static JSONObject getSubJSON(JSONObject jsonObject, String filter){
        return jsonObject.getJSONObject(filter);
    }
}