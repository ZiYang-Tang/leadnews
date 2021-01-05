package com.heima.kafak.simple;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;

import java.util.Properties;

/**
 * 消息生产者
 */
public class ProducerFastStart {
    /**
     * 主题
     */
    private static final String TOPIC = "itcast-heima";

    public static void main(String[] args) {
        //添加kafka的配置信息
        Properties properties = new Properties();
        //配置docker 信息
        properties.put("bootstrap.servers", "192.168.200.130:9092");
        // 生产者键序列化器配置
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        // 值序列化器配置
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        properties.put(ProducerConfig.RETRIES_CONFIG, 10);

        // 创建生产者对象，给定配置信息
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //封装消息
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, "00001", "hello 王格！ !");
        //发送消息
        try {
            producer.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //关闭消息通道，释放资源
        producer.close();

    }
}
