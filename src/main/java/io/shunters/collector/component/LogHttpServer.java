package io.shunters.collector.component;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


public class LogHttpServer implements InitializingBean, DisposableBean {

    private static Logger log = LoggerFactory.getLogger(LogHttpServer.class);

    private HttpServer server;

    private int port;

    private Handler<RoutingContext> httpRequestHandler;

    private int threadSize;

    private int workerPoolSize;

    private int idleTimeoutInSeconds;

    public void setIdleTimeoutInSeconds(int idleTimeoutInSeconds) {
        this.idleTimeoutInSeconds = idleTimeoutInSeconds;
    }

    public void setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHttpRequestHandler(Handler<RoutingContext> httpRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
    }

    @Override
    public void destroy() throws Exception {
        this.server.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
        vertxOptions.setEventLoopPoolSize(this.threadSize);
        vertxOptions.setWorkerPoolSize(this.workerPoolSize);

        Vertx vertx = Vertx.vertx(vertxOptions);

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setSendBufferSize(100 * 1024);
        httpServerOptions.setReceiveBufferSize(100 * 1024);
        httpServerOptions.setAcceptBacklog(10000);
        httpServerOptions.setIdleTimeout(idleTimeoutInSeconds);

        Router router = Router.router(vertx);

        router.route(HttpRequestHandler.EVENT_URI).handler(this.httpRequestHandler);

        this.server = vertx.createHttpServer(httpServerOptions);
        server.requestHandler(router::accept).listen(port);

        log.info("http server is listening on " + port);
    }
}
