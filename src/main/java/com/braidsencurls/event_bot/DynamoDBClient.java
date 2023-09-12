package com.braidsencurls.event_bot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBClient {
    private static DynamoDBClient INSTANCE;
    private DynamoDbClient dynamoDbClient;

    private DynamoDBClient() {
        dynamoDbClient = amazonDynamoDBClient();
    }

    public static DynamoDBClient getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DynamoDBClient();
        }
        return INSTANCE;
    }

    public DynamoDbClient getDynamoDbClient() {
        return dynamoDbClient;
    }

    private DynamoDbClient amazonDynamoDBClient() {
        return DynamoDbClient.builder()
                .region(Region.of("ap-southeast-1"))
                .build();
    }
}
