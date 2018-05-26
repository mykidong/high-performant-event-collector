package io.shunters.collector.domain;

/**
 * Created by mykidong on 2017-11-20.
 */
public class BaseEvent {

    private String version;

    private String eventType;

    /**
     * event log json string.
     */
    private String value;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
