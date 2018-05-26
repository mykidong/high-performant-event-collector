package io.shunters.collector.component.generator;

import io.shunters.collector.domain.event.BaseProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mykidong on 2017-12-06.
 */
public class EventBuilderUtils {

    public static Map<String, Object> buildBasePropertiesMap(BaseProperties baseProperties) {
        Map<String, Object> basePropertiesMap = new HashMap<>();
        basePropertiesMap.put(BaseProperties.PropertyName.eventType.getPropertyName(), baseProperties.getEventType());
        basePropertiesMap.put(BaseProperties.PropertyName.version.getPropertyName(), baseProperties.getVersion());
        basePropertiesMap.put(BaseProperties.PropertyName.ts.getPropertyName(), baseProperties.getTs());
        basePropertiesMap.put(BaseProperties.PropertyName.uid.getPropertyName(), baseProperties.getUid());


        return basePropertiesMap;
    }
}
