package org.apache.coyote.request;

import java.util.Map;

public class HttpRequestHeader {

    private static final String HTML_EXTENSION = ".html";
    private static final String QUERY_START_CHARACTER = "?";
    private static final String ROOT = "/";
    private static final String EXTENSION_CHARACTER = ".";

    private final String startLine;
    private final Map<String, String> headers;

    public HttpRequestHeader(final String startLine, final Map<String, String> headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public String getRequestUrlWithoutQuery() {
        final String requestUrl = getRequestUrl();
        if (requestUrl.contains(QUERY_START_CHARACTER)) {
            final int index = requestUrl.indexOf(QUERY_START_CHARACTER);
            return requestUrl.substring(0, index);
        }
        return requestUrl;
    }

    public String getRequestUrl() {
        String requestUrl = startLine.split(" ")[1];
        requestUrl = makeDefaultRequestUrl(requestUrl);

        return requestUrl;
    }

    private String addExtension(final String requestUrl) {
        final int index = requestUrl.indexOf(QUERY_START_CHARACTER);
        if (index != -1) {
            final String path = requestUrl.substring(0, index);
            final String queryString = requestUrl.substring(index + 1);
            return path + HTML_EXTENSION + QUERY_START_CHARACTER + queryString;
        }
        return requestUrl + HTML_EXTENSION;
    }

    private String makeDefaultRequestUrl(String requestUrl) {
        if (requestUrl.equals(ROOT)) {
            return "/index.html";
        }
        if (!requestUrl.contains(EXTENSION_CHARACTER)) {
            return addExtension(requestUrl);
        }
        return requestUrl;
    }
}