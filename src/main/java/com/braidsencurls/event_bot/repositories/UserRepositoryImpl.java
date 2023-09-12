package com.braidsencurls.event_bot.repositories;

import com.braidsencurls.event_bot.DynamoDBClient;
import com.braidsencurls.event_bot.exceptions.NoUserFoundException;
import com.braidsencurls.event_bot.entities.Role;
import com.braidsencurls.event_bot.entities.User;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private static final String USER_TABLE = "User";
    private final DynamoDbClient dynamoDbClient = DynamoDBClient.getInstance().getDynamoDbClient();


    @Override
    public void save(User user) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("id", AttributeValue.builder().s(user.getId()).build());
        itemValues.put("username", AttributeValue.builder().s(user.getUsername()).build());
        itemValues.put("role", AttributeValue.builder().s(user.getRole().name()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(USER_TABLE)
                .item(itemValues)
                .build();

        try {
            dynamoDbClient.putItem(request);
            LOGGER.info("Successfully saved User!");
        } catch (DynamoDbException e) {
            LOGGER.error("Exception occur while saving the user", e);
            throw new RuntimeException("Saving of User Failed!", e);
        }
    }

    @Override
    public boolean deleteUser(String id) {
        HashMap<String,AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("id", AttributeValue.builder()
                .s(id)
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(USER_TABLE)
                .key(keyToGet)
                .build();

        try {
            dynamoDbClient.deleteItem(deleteReq);
            LOGGER.info("Successfully deleted a user with id {}", id);
            return true;
        } catch (DynamoDbException e) {
            LOGGER.error("Exception occur while deleting the event", e);
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        HashMap<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":username", AttributeValue.builder()
                .s(username)
                .build());

        ScanRequest queryReq = ScanRequest.builder()
                .tableName(USER_TABLE)
                .filterExpression("username = :username")
                .expressionAttributeValues(attrValues)
                .build();

        try {
            ScanResponse response = dynamoDbClient.scan(queryReq);
            List<User> users = convertToUser(response);
            if(CollectionUtils.isEmpty(users)) {
                throw new NoUserFoundException("No Users Found with username " + username);
            }
            return users.get(0);
        } catch (DynamoDbException e) {
            LOGGER.error("Failed to find all events", e);
            throw new RuntimeException("Failed to find all events");
        }
    }

    private List<User> convertToUser(ScanResponse scanResponse) {
        List<User> users = new ArrayList<>();

        if (CollectionUtils.isEmpty(scanResponse.items())) {
            LOGGER.info("Items is empty");
            return users;
        }

        for (Map<String, AttributeValue> eventItem : scanResponse.items()) {
            String id = eventItem.get("id").s();
            String username = eventItem.get("username").s();
            String role = eventItem.get("role").s();

            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setRole(Role.valueOf(role));

            users.add(user);
        }

        LOGGER.info("Total Users: " + users.size());
        return users;
    }
}
