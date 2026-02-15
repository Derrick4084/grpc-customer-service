package com.derocode.customer_grpc_server.configs;


import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.Map;

@Component
public class AwsSecretsPropertyLoader implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

        ConfigurableEnvironment env = event.getEnvironment();

        String secretName = env.getProperty("spring.data.mongo.secret");
        String region = env.getProperty("aws.region", "us-east-1");

        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region))
                .build()) {

            String secretJson = client.getSecretValue(
                    GetSecretValueRequest.builder()
                            .secretId(secretName)
                            .build()
            ).secretString();

            JsonParser parser = JsonParserFactory.getJsonParser();
            Map<String, Object> props = parser.parseMap(secretJson);

            env.getPropertySources().addFirst(
                    new MapPropertySource("aws-secrets", props)
            );
        }


    }
}
