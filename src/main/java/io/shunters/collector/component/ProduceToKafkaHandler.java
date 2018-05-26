package io.shunters.collector.component;

import com.codahale.metrics.MetricRegistry;
import com.lmax.disruptor.EventHandler;
import io.shunters.collector.domain.EventLog;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class ProduceToKafkaHandler implements EventHandler<EventLog> {

    private static Logger log = LoggerFactory.getLogger(ProduceToKafkaHandler.class);

    private static final String METRICS_PREFIX = "meter-collection-handler." + ProduceToKafkaHandler.class.getSimpleName();

    private Producer<Integer, String> producer;

    private static AtomicLong count = new AtomicLong(0);


    private boolean sendMessageEnabled;

    private String topicName;

    /**
     * metric registry.
     */
    private MetricRegistry metricRegistry;

    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void setSendMessageEnabled(boolean sendMessageEnabled) {
        this.sendMessageEnabled = sendMessageEnabled;
    }

    public void setProducer(Producer<Integer, String> producer) {
        this.producer = producer;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }


    @Override
    public void onEvent(EventLog eventLog, long l, boolean b) throws Exception {

        String topic = this.topicName;

        String eventType = eventLog.getEventType();
        String json = eventLog.getValue();

        // send event log to kafka.
        if (this.sendMessageEnabled) {
            producer.send(new ProducerRecord<Integer, String>(topic, json));
        }


        this.metricRegistry.meter(METRICS_PREFIX).mark();
        this.metricRegistry.meter(METRICS_PREFIX + "." + eventType).mark();

        long cnt = count.incrementAndGet();
        if ((cnt % 10000) == 0) {
            log.info("count of messages received: [" + cnt + "], current timestamp: [" + new Date().getTime() + "]");
        }
    }
}
