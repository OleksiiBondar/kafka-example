package kafkaExample;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Arrays;
import java.util.Properties;

public class Stream {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-example");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.METRIC_REPORTER_CLASSES_CONFIG,
                "io.micrometer.core.instrument.binder.kafka.KafkaStreamsApiMetrics");
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(Arrays.asList("odd", "even"));
        KStream<String, String> joined = stream.map((key, value) -> KeyValue.pair(key, key.concat(value)));
        joined.to("odd-even");
        ForeachAction<String, String> action = new ForeachAction<String, String>() {
            public void apply(String key, String value) {
                System.out.println(key + ":" + value);
            };
        };
        joined.foreach(action);
        KafkaStreams kafkaStreams = new KafkaStreams(new Topology(), config);
        kafkaStreams.start();
    }
}
