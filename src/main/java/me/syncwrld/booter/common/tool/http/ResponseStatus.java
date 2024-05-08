package me.syncwrld.booter.common.tool.http;

public enum ResponseStatus {
        SUCCESS,
        ACCEPTED,
        MOVED_PERMANENTLY,
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        INTERNAL_SERVER_ERROR,
        NOT_IMPLEMENTED,
        SERVICE_UNAVAILABLE,
        GATEWAY_TIMEOUT,
        UNKNOWN;

        public static ResponseStatus get(int code) {
            switch (code) {
                case 200:
                    return SUCCESS;
                case 202:
                    return ACCEPTED;
                case 301:
                    return MOVED_PERMANENTLY;
                case 400: return
                        BAD_REQUEST;
                case 401:
                    return UNAUTHORIZED;
                case 403:
                    return FORBIDDEN;
                case 404:
                    return NOT_FOUND;
                case 500:
                    return INTERNAL_SERVER_ERROR;
                case 501:
                    return NOT_IMPLEMENTED;
                case 503:
                    return SERVICE_UNAVAILABLE;
                case 504:
                    return GATEWAY_TIMEOUT;
                default:
                    return UNKNOWN;
            }
        }
    }