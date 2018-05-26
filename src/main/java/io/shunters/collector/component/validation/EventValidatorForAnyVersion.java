package io.shunters.collector.component.validation;

import io.shunters.collector.api.component.EventValidator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by mykidong on 2017-12-07.
 */
public class EventValidatorForAnyVersion implements EventValidator {

    private static Logger validationLog = LoggerFactory.getLogger("event-log-validation");
    private static Logger log = LoggerFactory.getLogger(EventValidatorForAnyVersion.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isValid(Map<String, Object> map) {

        // TODO: implement validation.

        return true;
    }

}
