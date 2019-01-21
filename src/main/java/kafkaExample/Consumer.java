package kafkaExample;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Consumer {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        DatadogMeterRegistry registry = new DatadogMeterRegistry(new CustomDatadogConfig(), Clock.SYSTEM);
        Metrics.addRegistry(registry);
        
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG,
                "io.micrometer.core.instrument.binder.kafka.KafkaConsumerApiMetrics");
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList("odd", "even"));
            while (true) {
                ConsumerRecords<String, String> recs = consumer.poll(Duration.ofMillis(5000));
                if (recs.count() > 0) {
                    for (ConsumerRecord<String, String> rec : recs) {
                        logger.error("Recieved {} : {}", rec.key(), rec.value());
                    }
                    consumer.commitSync();
                }
            }
        }
    }
}
