package io.shunters.collector.component.generator;

import io.shunters.collector.util.TimeUtils;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.shunters.collector.component.HttpRequestHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleEventGenerator {

    private static Logger log = LoggerFactory.getLogger(SimpleEventGenerator.class);


    @Test
    public void sendEvents() throws Exception {
        String host = System.getProperty("host", "localhost");
        String port = System.getProperty("port", "8887");
        String maxLoop = System.getProperty("maxLoop", "100000");
        String intervalStr = System.getProperty("interval", "1000000000");
        String eventSize = System.getProperty("eventSize", "100");

        long MAX = Long.parseLong(maxLoop);
        if (MAX == 0) {
            MAX = Long.MAX_VALUE;
        }

        long interval = Long.parseLong(intervalStr);

        int size = Integer.valueOf(eventSize);

        Vertx vertx = Vertx.vertx();

        HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setDefaultHost(host)
                .setDefaultPort(Integer.parseInt(port))
                .setMaxPoolSize(5);

        HttpClient client = vertx.createHttpClient(httpClientOptions);

        // EventLog List.
        List<List<String>> eventList = new ArrayList<>();
        eventList.add(new CartEventBuilder().getEventList(size));


        ObjectMapper mapper = new ObjectMapper();
        Random random = new Random();

        log.info("ready to send...");

        int count = 0;
        while (count < MAX) {
            List<String> jsonList = eventList.get(random.nextInt(eventList.size()));
            for (String json : jsonList) {
                HttpClientRequest request = client.post(HttpRequestHandler.EVENT_URI,
                        new Handler<HttpClientResponse>() {
                            public void handle(HttpClientResponse resp) {
                            }
                        });

                byte[] bytes = json.getBytes();

                request.putHeader("Content-Length", String.valueOf(bytes.length));

                Buffer myBuffer = Buffer.buffer(bytes);

                request.write(myBuffer);

                request.end();

                TimeUtils.pause(interval);
                count++;
            }

        }


        Thread.sleep(10000);
        client.close();
    }
}
