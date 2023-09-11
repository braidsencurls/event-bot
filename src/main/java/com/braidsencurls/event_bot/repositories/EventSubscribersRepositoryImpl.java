package com.braidsencurls.event_bot.repositories;

import com.braidsencurls.event_bot.DynamoDBClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public class EventSubscribersRepositoryImpl implements EventSubscribersRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSubscribersRepositoryImpl.class);
    private static final DynamoDbClient dynamoDbClient = DynamoDBClient.getInstance().getDynamoDbClient();
    private static final String EVENT_SUBSCRIBERS_TABLE = "Events_Subscribers";

    @Override
    public void save(Long chatId) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("chatId", AttributeValue.builder().n(String.valueOf(chatId)).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(EVENT_SUBSCRIBERS_TABLE)
                .item(itemValues)
                .build();

        try {
            dynamoDbClient.putItem(request);
            LOGGER.info("Successfully saved Event Subscribers!");
        } catch (DynamoDbException e) {
            LOGGER.error("Exception occur while saving the subscribers", e);
            throw new RuntimeException("Saving of Event Subscribers failed!", e);
        }
    }

    @Override
    public boolean delete(Long chatId) {
        HashMap<String,AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("chatId", AttributeValue.builder()
                .n(String.valueOf(chatId))
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(EVENT_SUBSCRIBERS_TABLE)
                .key(keyToGet)
                .build();

        try {
            dynamoDbClient.deleteItem(deleteReq);
            LOGGER.info("Successfully removed from the event subscribers {}", chatId);
            return true;
        } catch (DynamoDbException e) {
            LOGGER.error("Exception occur while deleting a chatId from events subscribers", e);
            return false;
        }
    }

    @Override
    public Set<Long> findAll() {
        ScanRequest queryReq = ScanRequest.builder()
                .tableName(EVENT_SUBSCRIBERS_TABLE)
                .build();

        try {
            ScanResponse response = dynamoDbClient.scan(queryReq);
            Set<Long> eventsSubscribers = getSubscribers(response);
            if (eventsSubscribers != null) return eventsSubscribers;

        } catch (DynamoDbException e) {
            LOGGER.error("Failed to find all events", e);
            throw new RuntimeException("Failed to find all events");
        }
        return null;
    }

    private static Set<Long> getSubscribers(ScanResponse response) {
        Set<Long> eventsSubscribers = new HashSet<>();

        if (CollectionUtils.isEmpty(response.items())) {
            LOGGER.info("Subscribers list is empty");
            return eventsSubscribers;
        }

        for (Map<String, AttributeValue> subscriber : response.items()) {
            eventsSubscribers.add(Long.valueOf(subscriber.get("chatId").n()));
        }
        return eventsSubscribers;
    }
}
