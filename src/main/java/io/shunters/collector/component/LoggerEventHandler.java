package io.shunters.collector.component;


import com.codahale.metrics.MetricRegistry;
import com.lmax.disruptor.EventHandler;
import io.shunters.collector.domain.EventLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class LoggerEventHandler implements EventHandler<EventLog> {

    private static Logger eventLogger = LoggerFactory.getLogger("event-logger");

    private static Logger log = LoggerFactory.getLogger(LoggerEventHandler.class);

    private static final String METRICS_PREFIX = "meter-collection-handler." + LoggerEventHandler.class.getSimpleName();

    private static AtomicLong count = new AtomicLong(0);

    private boolean eventLoggingEnabled = true;


    public void setEventLoggingEnabled(boolean eventLoggingEnabled) {
        this.eventLoggingEnabled = eventLoggingEnabled;
    }


    /**
     * metric registry.
     */
    private MetricRegistry metricRegistry;

    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }


    @Override
    public void onEvent(EventLog eventLog, long sequence, boolean endOfBatch)
            throws Exception {

        String eventType = eventLog.getEventType();
        String json = eventLog.getValue();

        if (this.eventLoggingEnabled) {
            eventLogger.info(json);
        }

        this.metricRegistry.meter(METRICS_PREFIX).mark();
        this.metricRegistry.meter(METRICS_PREFIX + "." + eventType).mark();

        long cnt = count.incrementAndGet();
        if ((cnt % 10000) == 0) {
            log.info("count of messages logged: [" + cnt + "], current timestamp: [" + new Date().getTime() + "]");
        }
    }
}
