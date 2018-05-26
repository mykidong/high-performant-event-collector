package io.shunters.collector.component.generator;

import com.cedarsoftware.util.io.JsonWriter;
import io.shunters.collector.domain.event.BaseProperties;
import io.shunters.collector.domain.event.CartEvent;
import io.shunters.collector.util.JsonUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.*;

/**
 * Created by mykidong on 2017-12-05.
 */
public class CartEventBuilder implements EventBuilder {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<String> getEventList(int max) {
        List<String> jsonList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            String json = buildJsonEvent(i);
            jsonList.add(json);
        }

        return jsonList;
    }

    private String buildJsonEvent(int count) {
        BaseProperties baseProperties = new BaseProperties();
        baseProperties.setEventType("cart-event");
        baseProperties.setVersion("7.0");
        baseProperties.setUid("any-uid" + count);
        baseProperties.setTs(new Date().getTime());


        CartEvent cartEvent = new CartEvent();
        cartEvent.setBaseProperties(baseProperties);
        cartEvent.setItemId("any-item-id" + count);
        cartEvent.setQuantity(2 + count);
        cartEvent.setPrice(1000 + count);

        Map<String, Object> basePropertiesMap = EventBuilderUtils.buildBasePropertiesMap(baseProperties);

        Map<String, Object> cartEventMap = new HashMap<>();
        cartEventMap.put(CartEvent.PropertyName.baseProperties.getPropertyName(), basePropertiesMap);
        cartEventMap.put(CartEvent.PropertyName.itemId.getPropertyName(), cartEvent.getItemId());
        cartEventMap.put(CartEvent.PropertyName.quantity.getPropertyName(), cartEvent.getQuantity());
        cartEventMap.put(CartEvent.PropertyName.price.getPropertyName(), cartEvent.getPrice());

        String json = JsonUtils.toJson(mapper, cartEventMap);

        return json;
    }

    @Test
    public void printJson()
    {
        String json = buildJsonEvent(0);
        json = JsonWriter.formatJson(json);
        System.out.printf("%s", json);
    }
}
