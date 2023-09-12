package com.braidsencurls.event_bot.entities;

import com.braidsencurls.event_bot.DateUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.braidsencurls.event_bot.DateUtil.DATE_TIME_24_HOUR;

public class Event {
    public static final String LINE_BREAK = "\n";

    private String id;
    private String name;
    private String description;
    private String location;
    private String organizer;
    private String status;
    private LocalDateTime dateTime;
    private Set<String> attendees;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setAttendees(Set<String> attendees) {
        this.attendees = attendees;
    }

    public Set<String> getAttendees() {
        return attendees == null ? new HashSet<>() : attendees;
    }

    public void addAttendees(String attendeeId) {
        if(CollectionUtils.isEmpty(attendees)) {
            attendees = new HashSet<>();
        }
        attendees.add(attendeeId);
    }

    @Override
    public String toString() {
        return  "Id: " + id + LINE_BREAK +
                "Name: " + name + LINE_BREAK +
                "Description: " + description + LINE_BREAK +
                "Location: " + location + LINE_BREAK +
                "Date and Time: " + DateUtil.formatDateTime(dateTime, DATE_TIME_24_HOUR) + LINE_BREAK +
                "Organizer: " + organizer;

    }
}
