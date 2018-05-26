package io.shunters.collector.component;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import io.shunters.collector.domain.EventLog;
import io.shunters.collector.domain.event.BaseProperties;
import io.shunters.collector.domain.event.CartEvent;
import io.shunters.collector.util.JsonUtils;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.shunters.collector.util.DisruptorCreator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

public class HttpRequestHandler implements Handler<RoutingContext>, InitializingBean {

    private static Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private static Logger validationLog = LoggerFactory.getLogger("event-log-validation");

    private Disruptor<EventLog> validationDisruptor;

    private BaseTranslator.EventLogTranslator eventLogValidationTranslator;

    public static final String EVENT_URI = "/events";

    private boolean closeResponse = true;

    private EventHandler<EventLog> validationHandler;

    public void setCloseResponse(boolean closeResponse) {
        this.closeResponse = closeResponse;
    }

    public void setValidationHandler(EventHandler<EventLog> validationHandler) {
        this.validationHandler = validationHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.validationDisruptor = DisruptorCreator.singleton(DisruptorCreator.DISRUPTOR_NAME_VALIDATION, EventLog.FACTORY, 1024, this.validationHandler);

        this.eventLogValidationTranslator = new BaseTranslator.EventLogTranslator();
    }


    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = request.response();

        String path = request.path();
        String resultData = "";

        if (path.startsWith(EVENT_URI)) {
            Handler<Buffer> bodyHandler = new EventBodyHandler(this.validationDisruptor, this.eventLogValidationTranslator);
            request.bodyHandler(bodyHandler);

            response.setStatusCode(200);
            response.setStatusMessage("OK");
            resultData = "OK";
        } else {
            response.setStatusCode(404);
            response.setStatusMessage("NOT ALLOWED");
        }

        response.putHeader("Content-Length", "" + String.valueOf(resultData.getBytes().length));
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.end(resultData);

        if (closeResponse) {
            response.close();
        }
    }


    static class EventBodyHandler implements Handler<Buffer> {
        private static Logger log = LoggerFactory.getLogger(EventBodyHandler.class);

        private Disruptor<EventLog> validationDisruptor;
        private BaseTranslator.EventLogTranslator eventLogValidationTranslator;

        private ObjectMapper mapper = new ObjectMapper();

        public EventBodyHandler(Disruptor<EventLog> validationDisruptor, BaseTranslator.EventLogTranslator eventLogValidationTranslator) {
            this.validationDisruptor = validationDisruptor;
            this.eventLogValidationTranslator = eventLogValidationTranslator;
        }

        @Override
        public void handle(Buffer body) {

            String json = new String(body.getBytes());

            if(json.equals(""))
            {
                log.info("body is empty...");

                return ;
            }

            try {
                Map<String, Object> map = JsonUtils.toMap(mapper, json);

                Map<String, Object> basePropertiesMap = (Map<String, Object>) map.get(CartEvent.PropertyName.baseProperties.getPropertyName());

                String version = (String) basePropertiesMap.get(BaseProperties.PropertyName.version.getPropertyName());
                String eventType = (String) basePropertiesMap.get(BaseProperties.PropertyName.eventType.getPropertyName());


                this.eventLogValidationTranslator.setVersion(version);
                this.eventLogValidationTranslator.setEventType(eventType);
                this.eventLogValidationTranslator.setValue(json);

                this.validationDisruptor.publishEvent(this.eventLogValidationTranslator);
            }catch (Exception e)
            {
                e.printStackTrace();
                log.error(e.getMessage());

                return;
            }
        }
    }
}
