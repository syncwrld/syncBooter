package me.syncwrld.booter.common.tool.http;

import lombok.Getter;

@Getter
public class RequestResponse<T> {

    private final T rawData;
    private final String data;
    private final Integer statusCode;
    private final Long time;
    private final String headers;
    private final ResponseStatus responseStatus;

    public RequestResponse(T rawData, String data, Integer statusCode, Long time, String headers, ResponseStatus responseStatus) {
        this.rawData = rawData;
        this.data = data;
        this.statusCode = statusCode;
        this.time = time;
        this.headers = headers;
        this.responseStatus = responseStatus;
    }
}