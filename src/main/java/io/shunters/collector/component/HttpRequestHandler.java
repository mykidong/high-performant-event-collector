package io.shunters.collector.component;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import io.shunters.collector.domain.EventLog;
import io.shunters.collector.domain.ResponseData;
import io.shunters.collector.domain.event.BaseProperties;
import io.shunters.collector.domain.event.CartEvent;
import io.shunters.collector.util.DisruptorCreator;
import io.shunters.collector.util.JsonUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandler implements Handler<RoutingContext>, InitializingBean {

    private static Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private static Logger validationLog = LoggerFactory.getLogger("event-log-validation");

    public static final String CONTEXT_RESPONSE_DATA = "responseData";
    public static final String CONTEXT_CLOSE_RESPONSE = "closeResponse";

    private Disruptor<EventLog> validationDisruptor;

    private BaseTranslator.EventLogTranslator eventLogValidationTranslator;

    public static final String EVENT_URI = "/events";

    private boolean closeResponse = true;

    private ObjectMapper mapper = new ObjectMapper();

    private EventHandler<EventLog> validationHandler;

    private String json;

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

        // read body string.
        request.bodyHandler(body -> {
            json = body.toString();
        });

        routingContext.vertx().<ResponseData>executeBlocking(future -> {

            if(json.equals(""))
            {
                log.info("body is empty...");

                future.complete(new ResponseData(ResponseData.NOT_IMPLEMENTED, "NOT_IMPLEMENTED", "{}"));
            }

            try {
                Map<String, Object> map = JsonUtils.toMap(mapper, json);

                Map<String, Object> basePropertiesMap = (Map<String, Object>) map.get(CartEvent.PropertyName.baseProperties.getPropertyName());

                String version = (String) basePropertiesMap.get(BaseProperties.PropertyName.version.getPropertyName());
                String eventType = (String) basePropertiesMap.get(BaseProperties.PropertyName.eventType.getPropertyName());


                this.eventLogValidationTranslator.setVersion(version);
                this.eventLogValidationTranslator.setEventType(eventType);
                this.eventLogValidationTranslator.setValue(json);

                // send message to disruptor queue.
                this.validationDisruptor.publishEvent(this.eventLogValidationTranslator);


                // just build some response data.
                Map responseMap = new HashMap();
                responseMap.put("status", "OK");
                responseMap.put("serverTime", System.currentTimeMillis());
                String responseJson = JsonUtils.toJson(mapper, responseMap);
                ResponseData responseData = new ResponseData(ResponseData.STATUS_OK, "OK", responseJson);

                future.complete(responseData);

            }catch (Exception e)
            {
                e.printStackTrace();
                log.error(e.getMessage());

                future.complete(new ResponseData(ResponseData.NOT_IMPLEMENTED, "NOT_IMPLEMENTED", "{}"));
            }
        }, false, res -> {
            if(res.succeeded())
            {
                ResponseData responseData = res.result();

                // response data set to the context in order to respond to the client in the next pipeline.
                routingContext.put(CONTEXT_RESPONSE_DATA, responseData);
                routingContext.put(CONTEXT_CLOSE_RESPONSE, closeResponse);
                routingContext.next();
            }
            else if(res.failed())
            {
                log.error("error: [{}]", res.cause().getMessage());
                if(!response.ended())
                {
                    response.end();
                }
            }
        });
    }


    public static void doResponse(RoutingContext routingContext)
    {
        HttpServerResponse response = routingContext.response();

        ResponseData responseData = routingContext.get(CONTEXT_RESPONSE_DATA);
        Boolean closeResponse = routingContext.get(CONTEXT_CLOSE_RESPONSE);

        doResponse(response, responseData, closeResponse);
    }



    private static void doResponse(HttpServerResponse response, ResponseData responseData, boolean closeResponse) {

        // if response connection ended or closed, do nothing.
        if(response.ended() || response.closed())
        {
            return;
        }

        // send response to the client.
        response.setStatusCode(responseData.getStatusCode());
        response.setStatusMessage(responseData.getStatusMessage());

        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Content-Type", responseData.getContentType());
        response.putHeader("Content-Length", "" + String.valueOf(responseData.getResponseMessage().getBytes().length));
        response.end(responseData.getResponseMessage());

        if (closeResponse) {
            response.close();
        }
    }
}
