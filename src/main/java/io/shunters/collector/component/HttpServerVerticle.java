package io.shunters.collector.component;


import io.shunters.collector.util.SpringContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class HttpServerVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(HttpServerVerticle.class);

    private int port;

    private Handler<RoutingContext> httpRequestHandler;

    private int idleTimeoutInSeconds;


    public HttpServerVerticle()
    {
        ApplicationContext applicationContext = SpringContext.getApplicationContext();

        // get log http server bean from spring application context.
        LogHttpServer logHttpServer = applicationContext.getBean("logHttpServer", LogHttpServer.class);

        port = logHttpServer.port;

        httpRequestHandler = logHttpServer.httpRequestHandler;

        idleTimeoutInSeconds = logHttpServer.idleTimeoutInSeconds;
    }



    @Override
    public void start() throws Exception {

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setSendBufferSize(100 * 1024);
        httpServerOptions.setReceiveBufferSize(100 * 1024);
        httpServerOptions.setAcceptBacklog(10000);
        httpServerOptions.setIdleTimeout(idleTimeoutInSeconds);

        Router router = Router.router(vertx);

        router.route(HttpRequestHandler.EVENT_URI).handler(this.httpRequestHandler).handler(HttpRequestHandler::doResponse);

        HttpServer server = vertx.createHttpServer(httpServerOptions);
        server.requestHandler(router::accept).listen(port);

        log.info("http server is listening on " + port);
    }
}
