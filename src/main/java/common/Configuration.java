package common;

/**
 * ip and port configuration if they will not be entered
 */
public class Configuration {
    public static final String IP = "localhost";
    public static final int PORT = 8087;
    public static final String CONFIG = "MD5";
    public static final String DB_HOST = "localhost";
    public static final int DB_PORT = 2908;
    public static final boolean HELIOS = true;

    public static final String jdbcLocal = String.format("jdbc:postgresql://%s:%d/studs", HELIOS? DB_HOST : IP, HELIOS? 5432 : DB_PORT);
}

