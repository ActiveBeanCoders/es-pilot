package com.activebeancoders.fitness.data.es.config;

import net.pladform.elasticsearch.service.EsService;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Dan Barrese
 */
@Configuration
@PropertySource(value = "classpath:/data-es-service.properties", ignoreResourceNotFound = false)
@PropertySource(value = "file:/activebeancoders/data-es-service.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/activebeancoders/urls.properties", ignoreResourceNotFound = true)
public class DataEsServiceConfig {

    //To resolve ${} in @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean // from elasticsearch-util
    public EsService esService() {
        return new EsService();
    }

    @Bean // from elasticsearch-util
    @Scope("singleton")
    public Client esClient() {
        try {
            // TODO: resolve via property
            String hostname = "localhost";
            int port = 9300; // 9300 for Java API, 9200 for REST API.
            InetAddress host = InetAddress.getByName(hostname);
            InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(host, port);
            return TransportClient.builder().build().addTransportAddress(transportAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
