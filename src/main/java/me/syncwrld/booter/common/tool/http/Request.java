package me.syncwrld.booter.common.tool.http;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Getter
public class Request<T> {

    private HttpURLConnection requestConnection;
    private RequestResponse<T> response;
    private String contentType;
    @Getter
    private Exception exception;
    private long requestTime;
    private boolean requireSSL = true;

    private final URL url;
    private int timeout;

    public Request(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("A url provida é inválida: " + e);
        }
    }

    public Request(URL url) {
        this.url = url;
    }

    public Request(URL url, int timeoutInMillis) {
        this.url = url;
        this.timeout = timeoutInMillis;
    }

    public void setContentType(String type) {
        contentType = type;
    }

    public void setRequireSSL(boolean b) {
        requireSSL = b;
    }

    public boolean execute() {
        try {
            final long now = System.currentTimeMillis();
            int timeout = this.timeout == 0 ? 5000 : this.timeout;

            requestConnection = (HttpURLConnection) this.url.openConnection();
            requestConnection.setRequestMethod("GET");
            requestConnection.setRequestProperty("requireSSL", String.valueOf(requireSSL));
            requestConnection.setConnectTimeout(timeout);

            if (contentType != null && !contentType.isEmpty()) {
                requestConnection.setRequestProperty("Content-Type", contentType);
            }

            requestConnection.connect();
            requestTime = (System.currentTimeMillis() - now);

            buildResponse();

            return true;
        } catch (RuntimeException | IOException e) {
            exception = e;
            return false;
        }
    }

    private void buildResponse() throws IOException {
        int responseCode = requestConnection.getResponseCode();
        long responseTime = requestTime;
        String headers = requestConnection.getHeaderFields().toString();

        BufferedReader in = new BufferedReader(new InputStreamReader(requestConnection.getInputStream()));
        String inputLine;
        StringBuilder raw = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            raw.append(inputLine);
        }

        in.close();

        String data = raw.toString();

        response = new RequestResponse<T>((T) raw.toString(), data, responseCode, responseTime, headers, ResponseStatus.get(responseCode));
    }

    public RequestResponse<T> getResponse() {
        return response;
    }
}
