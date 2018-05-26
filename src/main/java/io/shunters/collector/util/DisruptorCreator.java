package io.shunters.collector.util;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

/**
 * Created by mykidong on 2016-09-01.
 */
public class DisruptorCreator {

    private static Logger log = LoggerFactory.getLogger(DisruptorCreator.class);

    public static final String DISRUPTOR_NAME_VALIDATION = "Validation";
    public static final String DISRUPTOR_NAME_PUT = "Put";

    private static ConcurrentMap<String, Disruptor> disruptorMap;
    private static final Object lock = new Object();

    public static <T> Disruptor singleton(String disruptorName, EventFactory<T> factory, int bufferSize, EventHandler<T>... handlers)
    {
        if(disruptorMap == null) {
            synchronized(lock) {
                if(disruptorMap == null) {
                    disruptorMap = new ConcurrentHashMap<>();
                    Disruptor disruptor = newInstance(disruptorName, factory, bufferSize, handlers);
                    disruptorMap.put(disruptorName, disruptor);
                }
            }
        }
        else
        {
            synchronized(lock) {
                if (!disruptorMap.containsKey(disruptorName)) {
                    Disruptor disruptor = newInstance(disruptorName, factory, bufferSize, handlers);
                    disruptorMap.put(disruptorName, disruptor);
                }
            }
        }

        return disruptorMap.get(disruptorName);
    }

    public static <T> Disruptor newInstance(String disruptorName, EventFactory<T> factory, int bufferSize, EventHandler<T>... handlers) {
        Disruptor disruptor = new Disruptor(factory,
                bufferSize,
                Executors.newCachedThreadPool(),
                ProducerType.SINGLE, // Single producer
                new BlockingWaitStrategy());

        disruptor.handleEventsWith(handlers);
        disruptor.start();

        return disruptor;
    }
}
