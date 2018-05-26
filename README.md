# High Performant Event Collector

This is the codes about the blog https://medium.com/@mykidong/howto-high-performant-event-collector-with-disruptor-and-vert-x-2e1a1949a62c

If you want to run demo, follow the next steps.

Run Kafka and Zookeeper on Local:

    mvn -e -Dtest=RunLocalKafka -P local test;
  
  
Run Http Log Server:

    mvn -e -Dtest=RunLogEventServer -P local test;
    
Run Client to send messages:

    mvn -e -Dtest=SimpleEventGenerator -Dhost=localhost -DmaxLoop=0 -Dinterval=1000000000 -DeventSize=1 test;
    
    
