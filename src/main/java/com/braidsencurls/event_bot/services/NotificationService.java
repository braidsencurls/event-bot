package com.braidsencurls.event_bot.services;

import com.braidsencurls.event_bot.EventAction;
import com.braidsencurls.event_bot.entities.Event;

public interface NotificationService {
    void notifyEventsSubscribers(EventAction action, Event event, String actor);
}
