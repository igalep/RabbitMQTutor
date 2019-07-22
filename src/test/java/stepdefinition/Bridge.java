package stepdefinition;

/**
 * Created by epshtein.
 * Date: 2019-07-22
 */
public class Bridge {

    static Object object = new Object();


    public static String getStatus() {
        synchronized (object) {
            return status;
        }
    }

    public static void setStatus(String status) {
        synchronized (object) {
            Bridge.status = status;
        }
    }

    private static String status = "";
}
