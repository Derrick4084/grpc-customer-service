package com.derocode.customer_grpc_server.configs;

import com.mongodb.DBRef;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.ServerAddress;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;

import org.bson.Document;
import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

import java.util.List;

@Configuration
public class MongoClientConfig {

    @Value("${mongo.password}")
    private String docDbSecret;

    @Value("${mongo.username}")
    private String docDbUserName;

    @Value("${spring.data.mongo.host}")
    private String docDbHost;

    @Value("${mongo.port}")
    private int docDbPort;

    @Bean
    public MongoClient mongoClient() {

        MongoCredential credential = MongoCredential.createScramSha256Credential(
                docDbUserName,
                "admin",
                docDbSecret.toCharArray()
        );
        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(List.of(
                                        new ServerAddress(docDbHost,
                                        docDbPort)))
                        )
                        .credential(credential)
                        .build()
        );

    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient,"customers");
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory factory,
            MongoCustomConversions conversions,
            MongoMappingContext context) {
        MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
        mappingMongoConverter.setCustomConversions(conversions);
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingMongoConverter;

    }
}
