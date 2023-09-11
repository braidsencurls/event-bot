package com.braidsencurls.event_bot.repositories;

import com.braidsencurls.event_bot.DateUtil;
import com.braidsencurls.event_bot.DynamoDBClient;
import com.braidsencurls.event_bot.Event;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.LocalDateTime;
import java.util.*;

import static com.braidsencurls.event_bot.DateUtil.DATE_TIME_24_HOUR;

public class EventRepositoryImpl implements EventRepository {

    public static final String EVENT_TABLE = "Event";
    private final DynamoDbClient dynamoDbClient = DynamoDBClient.getInstance().getDynamoDbClient();
    private static final Logger LOGGER = LoggerFactory.getLogger(EventRepositoryImpl.class);

    @Override
    public void save(Event event) {
        String eventDateTimeStr = DateUtil.formatDateTime(event.getDateTime(), DATE_TIME_24_HOUR);
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("id", AttributeValue.builder().s(event.getId()).build());
        itemValues.put("name", AttributeValue.builder().s(event.getName()).build());
        itemValues.put("description", AttributeValue.builder().s(event.getDescription()).build());
        itemValues.put("location", AttributeValue.builder().s(event.getLocation()).build());
        itemValues.put("dateTime", AttributeValue.builder().s(eventDateTimeStr).build());
        itemValues.put("organizer", AttributeValue.builder().s(event.getOrganizer()).build());
        itemValues.put("status", AttributeValue.builder().s(event.getStatus()).build());

        if(CollectionUtils.isNotEmpty(event.getAttendees())) {
            AttributeValue attendeesAttribute = AttributeValue.builder()
                    .ss(event.getAttendees()).build();
            itemValues.put("attendees", attendeesAttribute);
        }

        PutItemRequest request = PutItemRequest.builder()
                .tableName(EVENT_TABLE)
                .item(itemValues)
                .build();

        try {
            dynamoDbClient.putItem(request);
            LOGGER.info("Successfully saved Event!");
        } catch (DynamoDbException e) {
            LOGGER.error("Exception occur while saving the event", e);
            throw new RuntimeException("Saving of Event Failed!", e);
        }
    }

    @Override
    public boolean delete(String id) {
        HashMap<String,AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("id", AttributeValue.builder()
                .s(id)
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(EVENT_TABLE)
                .key(keyToGet)
                .build();

        try {
            dynamoDbClient.deleteItem(deleteReq);
            LOGGER.info("Successfully deleted an event with id {}", id);
            return true;
        } catch (DynamoDbException e) {
            LOGGER.error("Exception occur while deleting the event", e);
            return false;
        }
    }

    @Override
    public Event findById(String id) {
        HashMap<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":id", AttributeValue.builder()
                .s(id)
                .build());

        ScanRequest queryReq = ScanRequest.builder()
                .tableName(EVENT_TABLE)
                .filterExpression( "id = :id")
                .expressionAttributeValues(attrValues)
                .build();

        try {
            ScanResponse response = dynamoDbClient.scan(queryReq);
            List<Event> eventsResult = convertToEvent(response);
            return CollectionUtils.isNotEmpty(eventsResult) ? eventsResult.get(0) : null;
        } catch (DynamoDbException e) {
            LOGGER.error("Failed to find event with id: {}", id, e);
            throw new RuntimeException("Failed to find event with id " + id);
        }
    }

    @Override
    public List<Event> findByStatus(String status) {
        HashMap<String, String> aliases = new HashMap<>();
        aliases.put("#status", "status");

        HashMap<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":status", AttributeValue.builder()
                .s(status)
                .build());

        ScanRequest queryReq = ScanRequest.builder()
                .tableName(EVENT_TABLE)
                .filterExpression("#status = :status")
                .expressionAttributeNames(aliases)
                .expressionAttributeValues(attrValues)
                .build();

        try {
            ScanResponse response = dynamoDbClient.scan(queryReq);
            List<Event> events = convertToEvent(response);
           return events;
        } catch (DynamoDbException e) {
            LOGGER.error("Failed to find all events", e);
            throw new RuntimeException("Failed to find all events");
        }
    }

    public static List<Event> convertToEvent(ScanResponse scanResponse) {
        List<Event> events = new ArrayList<>();

        if (CollectionUtils.isEmpty(scanResponse.items())) {
            LOGGER.info("Items is empty");
            return events;
        }

        for (Map<String, AttributeValue> eventItem : scanResponse.items()) {
            String id = eventItem.get("id").s();
            String name = eventItem.get("name").s();
            String description = eventItem.get("description").s();
            String location = eventItem.get("location").s();
            String organizer = eventItem.get("organizer").s();
            String status = eventItem.get("status").s();
            String localDateTimeStr = eventItem.get("dateTime").s();
            LocalDateTime localDateTime = DateUtil.parseDateTime(localDateTimeStr, DATE_TIME_24_HOUR);

            Event event = new Event();
            event.setId(id);
            event.setName(name);
            event.setDescription(description);
            event.setLocation(location);
            event.setDateTime(localDateTime);
            event.setOrganizer(organizer);
            event.setStatus(status);

            if(eventItem.get("attendees") != null) {
                List<String> attendeeList = eventItem.get("attendees").ss();
                Set<String> attendeeSet = new HashSet<>(attendeeList);
                event.setAttendees(attendeeSet);
            }

            events.add(event);
        }

        LOGGER.info("Total Events: " + events.size());
        return events;
    }
}
