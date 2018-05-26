package io.shunters.collector.domain;

import com.lmax.disruptor.EventFactory;

public class EventLog extends BaseEvent {

    public final static EventFactory<EventLog> FACTORY = EventLog::new;
}
