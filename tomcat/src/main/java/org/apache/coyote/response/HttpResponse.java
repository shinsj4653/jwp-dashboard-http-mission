package org.apache.coyote.response;

import org.apache.coyote.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final ResponsePrinter responsePrinter;
    private StatusCode statusCode;
    private ContentType contentType;
    private String responseBody;
    private Location location;
    private Cookie cookie = Cookie.empty();

    private HttpResponse(final OutputStream outputStream) {
        this.responsePrinter = new ResponsePrinter(outputStream);
    }

    public static HttpResponse from(final OutputStream outputStream) {
        return new HttpResponse(outputStream);
    }

    public void setResponse(final StatusCode statusCode, final ContentType contentType, final String requestPath) throws URISyntaxException {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = new String(Objects.requireNonNull(readAllFile(requestPath)), UTF_8);
    }

    public void setResponse(final StatusCode statusCode, final ContentType contentType, final Location location) throws URISyntaxException {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.location = location;
        this.responseBody = new String(Objects.requireNonNull(readAllFile(location.toString())), UTF_8);
    }

    public void setResponse(final StatusCode statusCode, final ContentType contentType, final Location location, final Cookie cookie) throws URISyntaxException {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.location = location;
        this.responseBody = new String(Objects.requireNonNull(readAllFile(location.toString())), UTF_8);
        this.cookie = cookie;
    }

    public void print() {
        final String response = makeResponse();
        responsePrinter.flushBuffer(response);
    }

    private String makeResponse() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ").append(statusCode).append(" \r\n");
        builder.append("Content-Type: ").append(contentType).append(";charset=utf-8 \r\n");
        builder.append("Content-Length: ").append(responseBody.getBytes().length).append(" \r\n");

        appendLocation(builder);
        appendSetCookie(builder);

        builder.append("\r\n");
        builder.append(responseBody);

        return builder.toString();
    }

    public Cookie getCookie() {
        return cookie;
    }

    private void appendLocation(StringBuilder builder) {
        if (location != null) {
            builder.append("Location: ").append(location).append(" \r\n");
        }
    }

    private void appendSetCookie(StringBuilder builder) {
        if (!cookie.isEmpty()) {
            builder.append("Set-Cookie: ").append(cookie.toHeaderFormat()).append(" \r\n");
        }
    }

    private static byte[] readAllFile(final String requestUrl) throws URISyntaxException {
        final URL resourceUrl = ClassLoader.getSystemResource("static" + requestUrl);
        final Path path = Path.of(resourceUrl.toURI());

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("파일을 읽어들이지 못했습니다.");
            return new byte[0];
        }
    }

}
