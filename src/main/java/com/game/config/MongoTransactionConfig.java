package com.game.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.transaction.ReactiveTransactionManager;

//@Configuration
//@EnableReactiveMongoRepositories(basePackages = {"com.game.repository"})
//@EnableTransactionManagement
public class MongoTransactionConfig extends AbstractReactiveMongoConfiguration {

    //@Bean
    ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory factory) {
        return new ReactiveMongoTransactionManager(factory);
    }

    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }
}