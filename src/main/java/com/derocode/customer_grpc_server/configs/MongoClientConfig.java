package com.derocode.customer_grpc_server.configs;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.List;

@Configuration
public class MongoClientConfig {

    @Bean
    public MongoClient mongoClient() {
        MongoCredential credential = MongoCredential.createScramSha256Credential(
                "dero",
                "admin",
                "dero".toCharArray()
        );

        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(List.of(new ServerAddress("localhost",27017)))
                        )
                        .credential(credential)
                        .build()
        );

    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient,"customer");
    }
}
