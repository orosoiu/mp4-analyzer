package ro.occam.mp4analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ro.occam.mp4analyzer.utils.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Component
public class UrlConnectionService {

    @Value("${server.connection.timeout:1000}")
    private int connectionTimeout;

    @Value("${server.read.timeout:5000}")
    private int readTimeout;

    /**
     * Service to open an HTTP connection to an URL and returns the input stream for manual processing.
     * Supports redirects. Configurable.
     * Closing the stream is the responsibility of the caller.
     * @throws IOException if URL is invalid or the server does not return a successful response
     */
    public InputStream getUrlConnectionInputStream(String url) throws IOException {
        URLConnection urlConn = openConnection(url, true);
        urlConn.setConnectTimeout(connectionTimeout);
        urlConn.setReadTimeout(readTimeout);
        return urlConn.getInputStream();
    }

    private URLConnection openConnection(String mp4Url, boolean followRedirect) throws IOException {
        try {
            URL url = new URL(mp4Url);
            URLConnection urlConn = url.openConnection();
            int responseCode = ((HttpURLConnection) urlConn).getResponseCode();
            if (followRedirect && HttpUtils.isRedirectStatusCode(responseCode)) {
                String newLocation = urlConn.getHeaderField("Location");
                if (StringUtils.hasText(newLocation)) {
                    ((HttpURLConnection) urlConn).disconnect();
                    return openConnection(newLocation, false);
                } else {
                    throw new IOException("Location " + mp4Url + " has moved but no new location specified");
                }
            }
            if (HttpUtils.isNotSuccessStatusCode(responseCode)) {
                throw new IOException("HTTP call to " + mp4Url + " returned unsupported status code " + responseCode);
            }

            return urlConn;
        } catch (MalformedURLException mue) {
            // this case should not occur as the URL value is validated in the controller, however
            // as a sanity check wrap it as an IOException to be handled by the controller
            throw new IOException("Invalid URL " + mp4Url);
        }
    }
}
