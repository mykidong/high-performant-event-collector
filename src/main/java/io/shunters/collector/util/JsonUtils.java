package io.shunters.collector.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Map;

public class JsonUtils {

    public static Map<String, Object> toMap(ObjectMapper mapper, String json)
    {
        try {
            Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(ObjectMapper mapper, Object obj)
    {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
