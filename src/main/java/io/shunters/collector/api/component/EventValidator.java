package io.shunters.collector.api.component;

import java.util.Map;

/**
 * Created by mykidong on 2017-12-07.
 */
public interface EventValidator {

    boolean isValid(Map<String, Object> map);
}
