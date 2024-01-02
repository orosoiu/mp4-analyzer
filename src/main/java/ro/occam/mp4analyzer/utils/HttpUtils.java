package ro.occam.mp4analyzer.utils;

import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.Set;

public class HttpUtils {

    public static boolean isNotSuccessStatusCode(int statusCode) {
        return !isSuccessStatusCode(statusCode);
    }
    public static boolean isSuccessStatusCode(int statusCode) {
        return Objects.equals(HttpStatus.OK.value(), statusCode);
    }

    public static boolean isRedirectStatusCode(int statusCode) {
        return Set.of(
                HttpStatus.MOVED_PERMANENTLY.value(),
                HttpStatus.FOUND.value(),
                HttpStatus.SEE_OTHER.value(),
                HttpStatus.TEMPORARY_REDIRECT.value(),
                HttpStatus.PERMANENT_REDIRECT.value()
        ).contains(statusCode);
    }
}
