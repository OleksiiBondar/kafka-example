package kafkaExample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG, "io.micrometer.core.instrument.binder.kafka.MicrometerKafkaMetrics");
        KafkaConsumer consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList("odd", "even"));
        int counter = 0;
        while (counter <= 10) {
            ConsumerRecords<String, String> recs = consumer.poll(10);
            if (recs.count() == 0) {
            } else {
                for (ConsumerRecord<String, String> rec : recs) {
                    System.out.printf("Recieved %s: %s", rec.key(), rec.value());
                    System.out.println();
                }
            }
            counter++;
        }
    }
}
