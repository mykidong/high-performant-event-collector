package io.shunters.collector.component;

import com.lmax.disruptor.EventTranslator;
import io.shunters.collector.domain.EventLog;

/**
 * Created by mykidong on 2017-11-20.
 */
public class BaseTranslator {

    public static class EventLogTranslator extends EventLog implements EventTranslator<EventLog>
    {
        @Override
        public void translateTo(EventLog eventLog, long sequence) {
            eventLog.setVersion(this.getVersion());
            eventLog.setEventType(this.getEventType());
            eventLog.setValue(this.getValue());
        }
    }
}
