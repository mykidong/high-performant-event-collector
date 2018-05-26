package io.shunters.collector.component;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Properties;

public class KafkaProducerFactory implements InitializingBean, FactoryBean<Producer<Integer, String>> {

    private Producer<Integer, String> producer;

    private Properties kafkaProp;


    public void setKafkaProp(Properties kafkaProp) {
        this.kafkaProp = kafkaProp;
    }

    @Override
    public Producer<Integer, String> getObject() throws Exception {
        return this.producer;
    }

    @Override
    public Class<?> getObjectType() {
        return this.producer.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        producer = new KafkaProducer<>(kafkaProp);
    }
}
