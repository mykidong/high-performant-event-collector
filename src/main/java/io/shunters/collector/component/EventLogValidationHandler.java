package io.shunters.collector.component;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import io.shunters.collector.api.component.EventValidator;
import io.shunters.collector.domain.EventLog;
import io.shunters.collector.util.DisruptorCreator;
import io.shunters.collector.util.JsonUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

public class EventLogValidationHandler implements
        EventHandler<EventLog>, InitializingBean {

    private static Logger log = LoggerFactory.getLogger(EventLogValidationHandler.class);

    private ObjectMapper mapper = new ObjectMapper();

    private Disruptor<EventLog> putDisruptor;

    private BaseTranslator.EventLogTranslator putEventTranslator;

    private boolean eventLogValidationEnabled;

    private EventHandler<EventLog> produceToKafkaHandler;

    private EventHandler<EventLog> loggerEventHandler;

    /**
     * event log validator.
     */
    private EventValidator evenValidator;

    public void setEvenValidator(EventValidator evenValidator) {
        this.evenValidator = evenValidator;
    }

    public void setEventLogValidationEnabled(boolean eventLogValidationEnabled) {
        this.eventLogValidationEnabled = eventLogValidationEnabled;
    }

    public void setProduceToKafkaHandler(EventHandler<EventLog> produceToKafkaHandler) {
        this.produceToKafkaHandler = produceToKafkaHandler;
    }

    public void setLoggerEventHandler(EventHandler<EventLog> loggerEventHandler) {
        this.loggerEventHandler = loggerEventHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.putDisruptor = DisruptorCreator.singleton(DisruptorCreator.DISRUPTOR_NAME_PUT, EventLog.FACTORY, 1024, produceToKafkaHandler, loggerEventHandler);

        this.putEventTranslator = new BaseTranslator.EventLogTranslator();
    }


    @Override
    public void onEvent(final EventLog eventLog, final long sequence, final boolean endOfBatch) throws Exception {
        String version = eventLog.getVersion();
        String json = eventLog.getValue();

        // EventLog Log Version 에 맞게 Validation
        if (eventLogValidationEnabled) {
            try {
                Map<String, Object> map = JsonUtils.toMap(mapper, json);

                // version 7.0 validation.
                if (version.equals("7.0")) {
                    if (!evenValidator.isValid(map)) {

                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());

                return;
            }
        }

        this.putEventTranslator.setVersion(version);
        this.putEventTranslator.setEventType(eventLog.getEventType());
        this.putEventTranslator.setValue(json);

        this.putDisruptor.publishEvent(this.putEventTranslator);
    }
}
