package io.shunters.collector.component;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


public class LogHttpServer implements InitializingBean, DisposableBean {

    private static Logger log = LoggerFactory.getLogger(LogHttpServer.class);

    private Vertx vertx;

    protected int port;

    protected Handler<RoutingContext> httpRequestHandler;

    protected int threadSize;

    protected int workerPoolSize;

    protected int idleTimeoutInSeconds;

    protected int verticleCount;

    public void setVerticleCount(int verticleCount) {
        this.verticleCount = verticleCount;
    }

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
    public void afterPropertiesSet() throws Exception {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(verticleCount);
        deploymentOptions.setWorkerPoolSize(workerPoolSize);
        deploymentOptions.setWorker(true);

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
        vertxOptions.setEventLoopPoolSize(threadSize);
        vertxOptions.setWorkerPoolSize(workerPoolSize);

        vertx = Vertx.vertx(vertxOptions);

        // run http server verticles.
        vertx.deployVerticle("io.shunters.collector.component.HttpServerVerticle", deploymentOptions, ar -> {
            if(ar.succeeded())
            {
                log.info("http server verticles deployed completely...");
            }
            else
            {
                log.error("something wrong: " + ar.cause().toString());
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        vertx.close();
    }
}
