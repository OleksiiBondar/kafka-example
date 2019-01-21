package kafkaExample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Properties;

public class Producer {
    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG, "io.micrometer.core.instrument.binder.kafka.KafkaProducerApiMetrics");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> data;
            if (i % 2 == 0) {
                data = new ProducerRecord<String, String>("even", 0, Integer.toString(i), String.format("%d is even", i));
            } else {
                data = new ProducerRecord<String, String>("odd", 0, Integer.toString(i), String.format("%d is odd", i));
            }
            producer.send(data);
            logger.info("Sent record to topic");
            Thread.sleep(1L);
        }
        producer.close();
    }
}
