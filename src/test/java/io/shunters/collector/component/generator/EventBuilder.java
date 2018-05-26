package io.shunters.collector.component.generator;

import java.util.List;

/**
 * Created by mykidong on 2017-12-06.
 */
public interface EventBuilder {

    List<String> getEventList(int max);
}
