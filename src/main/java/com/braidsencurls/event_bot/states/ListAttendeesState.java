package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.repositories.EventRepository;
import com.braidsencurls.event_bot.repositories.EventRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class ListAttendeesState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListAttendeesState.class);
    private static final EventRepository eventRepository = new EventRepositoryImpl();

    @Override
    public String getKey() {
        return "LIST_ATTENDEES";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will process LIST_ATTENDEES");
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        Event selectedEvent = eventRepository.findById(message);
        if(selectedEvent == null) {
            return generate(chatId, "Sorry! I can't find that event. Can you try again? Enter the Event id you want to participate in\n"
                    + SharedData.getInstance().getAllFormattedEvents());
        } else {
            LOGGER.info("Event found and listing all attendees");
            if(selectedEvent.getAttendees().isEmpty()) {
                return generate(chatId, "No participants for this event yet!");
            } else {
                setNextState(chatId, null);
                return generate(chatId, "These guys will attend the event\n"
                        + String.join("\n", selectedEvent.getAttendees()));
            }
        }
    }
}
