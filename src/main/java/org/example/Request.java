package org.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private static final URLEncodedUtils URL_ENCODED_UTILS = new URLEncodedUtils();
    private final String method;
    private final Map<String, String> headers;
    private final String stringHeaders;
    private final byte[] body;
    private final String[] requestLine;
    private final String path;

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String[] getRequestLine() {
        return requestLine;
    }

    public String getPath() {
        return path;
    }
    public String getCleanPath() {
        return this.path.split("\\?")[0];
    }

    public Request(String[] requestLine, String stringHeaders, Map<String, String> headers, byte[] body) {
        method = requestLine[0];
        this.headers = headers;
        this.stringHeaders = stringHeaders;
        path = requestLine[1];
        this.body = body;
        this.requestLine = requestLine;
    }
// ?
    public List<NameValuePair> getQueryParams() throws IOException {
        return getListQueryParams(stringHeaders);
    }
//?
    public String getQueryParam(String headerName) {
        return headers.getOrDefault(headerName, null);
    }
    // для парсинга запроса, поиск заголовков 
    private static List<NameValuePair> getListQueryParams(String queryString) {
        return URL_ENCODED_UTILS.parse
                (queryString, StandardCharsets.UTF_8);
    }
// для поиска заголовков
    private static Map<String, String> headersLitToMap(List<NameValuePair> headers) {
        Map<String, String> mapHeaders = new HashMap<>();
        for (NameValuePair line : headers) {
            mapHeaders.put(line.getName(), line.getValue());
        }
        return mapHeaders;
    }

    public static Request requestFromInputStream(InputStream inputStream) throws IOException {

        var in = new BufferedReader(new InputStreamReader(inputStream));

        // читаем request line
        final var REQUEST_LINE = in.readLine().split(" ");
        if (REQUEST_LINE.length != 3) {
            throw new IOException("String[] REQUEST_LINE.length != 3");
        }

        final var METHOD = REQUEST_LINE[0];
        if (!Main.allowedMethods.contains(METHOD)) {
            throw new IOException("415 Method not support.");
        }

        final var path = REQUEST_LINE[1];
        if (!path.startsWith("/")) {
            throw new IOException("PATH starts not with '/'.");

        }
        // ищем заголовки
        final var STRING_HEADERS = in.readLine();
        final var LIST_NAME_VALUE_PAIR = getListQueryParams(STRING_HEADERS);
        final var HEADERS = headersLitToMap(LIST_NAME_VALUE_PAIR);

        //Ищем тело, в случае наличия
        //  * пропускаем пустую строку
        in.readLine();
        final byte[] BODY =
                (!(REQUEST_LINE[0].equals(Main.GET)) && HEADERS.containsKey("Content-Length")) ?
                        in.readLine().getBytes(StandardCharsets.UTF_8) : null;

        return new Request(REQUEST_LINE, STRING_HEADERS,  HEADERS, BODY);
    }
    @Override
    public String toString() {
        return "Request[" +
                "method=" + method + ", " +
                "path=" + path + ", " +
                "headers=" + headers + "]";
    }
}