package com.braidsencurls.event_bot.repositories;

import com.braidsencurls.event_bot.entities.Event;
import java.util.List;

public interface EventRepository {
    void save(Event event);
    boolean delete(String id);
    Event findById(String id);
    List<Event> findByStatus(String status);
}
