package io.shunters.collector.domain.event;


public class BaseProperties {

    public static enum PropertyName {

        eventType("eventType"),
        ts("ts"),
        version("version"),
        uid("uid");

        private final String propertyName;

        private PropertyName(String propertyName)
        {
            this.propertyName = propertyName;
        }

        public String getPropertyName()
        {
            return this.propertyName;
        }
    }

    private String eventType;
    private long ts;
    private String version;
    private String uid;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
