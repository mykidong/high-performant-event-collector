package io.shunters.collector.domain;

public class ResponseData {

    /**
     * status code.
     */
    public static final int STATUS_OK = 200;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int NOT_IMPLEMENTED = 501;


    public static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";

    private int statusCode;

    private String statusMessage;

    private String contentType;

    private String responseMessage;

    public ResponseData(int statusCode, String statusMessage, String responseMessage)
    {
        this(statusCode, statusMessage, responseMessage, DEFAULT_CONTENT_TYPE);
    }

    public ResponseData(int statusCode, String statusMessage, String responseMessage, String contentType)
    {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseMessage = responseMessage;
        this.contentType = contentType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getContentType() {
        return contentType;
    }
}
