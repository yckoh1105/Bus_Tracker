package my.edu.tarc.tracker;

/**
 * Created by Dell PC on 7/10/2018.
 */

public class Constant {
    public static final String URLServer = "http://i2hub.tarc.edu.my:4887/collegebus/";
    public static final String UrlGetRoute = URLServer + "select_routes.php";
    public static final String URL = "http://i2hub.tarc.edu.my:4887";
    public static final int ServerPort = 4887;

    public static final String MQTT_HOST = "tcp://i2hub.tarc.edu.my:6788";
    public static final String MQTT_USERNAME = "ezride";
    public static final String MQTT_PASSWORD = "ezride2018";
    public static final String MQTT_TRACKING_TOPIC_PREFIX = "MY/TARUC/VPS/TRACKING/";
    public static final int MQTT_TRACKING_TOPIC_ORDER = 1;
    public static final String MQTT_START_CMD = "00071001        ";
    public static final String MQTT_STOP_CMD = "00071002        ";
}
