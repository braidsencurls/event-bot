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

public class QuitEventState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuitEventState.class);
    private static final EventRepository eventRepository = new EventRepositoryImpl();

    @Override
    public String getKey() {
        return "QUIT_EVENT";
    }

    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will process QUIT_EVENT");
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        Event selectedEvent = eventRepository.findById(message);
        if(selectedEvent == null) {
            return generate(chatId, "Sorry! I can't find that event. Can you try again? Enter the Event id you want to quit from\n"
                    + SharedData.getInstance().getAllFormattedEvents());
        } else {
            boolean removedUser = selectedEvent.getAttendees().remove("@" + update.getMessage().getChat().getUserName());
            SendMessage sendMessage = generate(chatId, "You are currently not registered to this event");
            if(removedUser) {
                eventRepository.save(selectedEvent);
                sendMessage = generate(chatId, "You've successfully opted out from this event. You know what to do if you changed your mind!");
            }
            setNextState(chatId, null);
            return sendMessage;
        }
    }
}
