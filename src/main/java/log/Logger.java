package log;

/**
 * Created by grishberg on 07.09.17.
 */
public interface Logger {
    void log(String message);
    void log(String format, String ... values);
}
