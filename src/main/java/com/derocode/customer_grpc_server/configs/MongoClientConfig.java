package com.derocode.customer_grpc_server.configs;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoClientConfig {

    @Value("${password}")
    private String docDbSecret;

    @Value("${username}")
    private String docDbUserName;

    @Value("${customer_endpoint}")
    private String docDbHost;

    @Value("${port}")
    private int docDbPort;

    @Bean
    public MongoClient mongoClient() throws Exception {

        MongoCredential credential = MongoCredential.createScramSha256Credential(
                docDbUserName,
                "admin",
                docDbSecret.toCharArray()
        );

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = new FileInputStream("/app/certs/rds-combined-ca-bundle.pem")) {
            trustStore.load(null,null);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certs = cf.generateCertificates(is);
            int i = 0;
            for (Certificate cert : certs) {
                trustStore.setCertificateEntry("rds-cert-" + i++, cert);
            }
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(List.of(
                                        new ServerAddress(docDbHost, docDbPort)))
                                .requiredReplicaSetName("rs0")
                )
                .applyToSslSettings(builder -> {
                    builder.enabled(true);
                    builder.context(sslContext);
                    builder.invalidHostNameAllowed(true);
                })
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(10, TimeUnit.SECONDS))
                .retryWrites(false)
                .credential(credential)
                .build();
        return MongoClients.create(mongoClientSettings);

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
