package com.madroid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.madroid.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.URL;

@SpringBootApplication
public class VelibDataKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(VelibDataKafkaApplication.class, args);
    }

    @Value("${opendata.velib.smove.url.station-info}")
    String stationInfoUrl;

    @Value("${opendata.velib.smove.url.station-status}")
    String stationStatusUrl;

    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate) {
        return args -> {


            // Recuperation station info
            URL url = new URL(stationInfoUrl);
            StringBuffer content = HttpUtil.getUrl(url);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode stationInfoNode = objectMapper.readTree(content.toString());

            kafkaTemplate.send("velibdata-station-info", stationInfoNode.toString());

            // Recuperation station status
            url = new URL(stationStatusUrl);
            content = HttpUtil.getUrl(url);

            objectMapper = new ObjectMapper();
            JsonNode stationStatusNode = objectMapper.readTree(content.toString());

            kafkaTemplate.send("velibdata-station-status", stationStatusNode.toString());

        };
    }
}
