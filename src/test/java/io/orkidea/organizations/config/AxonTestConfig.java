package io.orkidea.organizations.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AxonTestConfig {

    @Bean
    @Primary
    public EventStorageEngine eventStorageEngine() {
        return new InMemoryEventStorageEngine();
    }

    @Autowired
    public void configureSubscribingProcessors(EventProcessingConfigurer configurer) {
        configurer.usingSubscribingEventProcessors();
    }
}
