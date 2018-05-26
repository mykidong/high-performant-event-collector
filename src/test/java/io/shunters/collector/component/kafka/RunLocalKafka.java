package io.shunters.collector.component.kafka;


import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RunLocalKafka {

    @Before
    public void init() throws Exception {
        java.net.URL url = new RunLocalKafka().getClass().getResource("/log4j-test.xml");
        System.out.println("log4j url: " + url.toString());

        DOMConfigurator.configure(url);
    }


    @Test
    public void startKafka() throws Exception {

        String brokerPath = System.getProperty("brokerPath", "kafka-prop-local.properties");
        String zkPath = System.getProperty("zkPath", "zk-prop-local.properties");

        Properties kafkaProperties = this.getProperties(brokerPath);
        Properties zkProperties = this.getProperties(zkPath);

        // delete kafka, zookeeper data directories.
        String kafkaDataPath = kafkaProperties.getProperty("log.dir");
        File kafkaDir = new File(kafkaDataPath);
        if(kafkaDir.exists()) {
            FileUtils.deleteDirectory(kafkaDir);
            System.out.println("kafka data dir deleted...");
        }

        String zkDataPath = zkProperties.getProperty("dataDir");
        File zkDir = new File(zkDataPath);
        if(zkDir.exists()) {
            FileUtils.deleteDirectory(zkDir);
            System.out.println("zookeeper dir deleted...");
        }

        try {
            //start kafka
            KafkaLocal kafka = new KafkaLocal(kafkaProperties, zkProperties);

            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e){
            e.printStackTrace(System.out);
            System.err.println("Error running local Kafka broker");
        }
    }

    private Properties getProperties(String propPath) throws Exception
    {
        PropertiesFactoryBean propBean = new PropertiesFactoryBean();
        propBean.setLocation(new ClassPathResource(propPath));
        propBean.afterPropertiesSet();

        return  propBean.getObject();
    }


    private static class KafkaLocal {

        public KafkaServerStartable kafka;


        public KafkaLocal(Properties kafkaProperties, Properties zkProperties) throws IOException, InterruptedException{
            KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);

            //start local zookeeper
            System.out.println("starting local zookeeper...");
            ZooKeeperLocal zookeeper = new ZooKeeperLocal(zkProperties);
            System.out.println("done");

            //start local kafka broker
            kafka = new KafkaServerStartable(kafkaConfig);
            System.out.println("starting local kafka broker...");
            kafka.startup();

            System.out.println("done");
        }


        public void stop(){
            //stop kafka broker
            System.out.println("stopping kafka...");
            kafka.shutdown();
            System.out.println("done");
        }

    }

    private static class ZooKeeperLocal {

        ZooKeeperServerMain zooKeeperServer;

        public ZooKeeperLocal(Properties zkProperties) throws FileNotFoundException, IOException{
            QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
            try {
                quorumConfiguration.parseProperties(zkProperties);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }

            zooKeeperServer = new ZooKeeperServerMain();
            final ServerConfig configuration = new ServerConfig();
            configuration.readFrom(quorumConfiguration);

            new Thread() {
                public void run() {
                    try {
                        zooKeeperServer.runFromConfig(configuration);
                    } catch (IOException e) {
                        System.out.println("ZooKeeper Failed");
                        e.printStackTrace(System.err);
                    }
                }
            }.start();
        }
    }
}
