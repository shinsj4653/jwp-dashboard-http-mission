package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestHeader {
    private final String startLine;
    private final Map<String, String> headers;

    public HttpRequestHeader(final String startLine, final Map<String, String> headers) {
        this.startLine = startLine;
        this.headers = headers;
    }


    public String getRequestUrl() {
        String requestUrl = startLine.split(" ")[1];
        requestUrl = makeDefaultRequestUrl(requestUrl);

        return requestUrl;
    }

    private static String makeDefaultRequestUrl(String requestUrl) {
        if (!requestUrl.contains(".") && requestUrl.equals("/")) {
            requestUrl = requestUrl + ".html";
        }
        return requestUrl;
    }
}
