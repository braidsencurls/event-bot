package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.DateUtil;
import com.braidsencurls.event_bot.EventAction;
import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.repositories.EventRepository;
import com.braidsencurls.event_bot.repositories.EventRepositoryImpl;
import com.braidsencurls.event_bot.services.NotificationService;
import com.braidsencurls.event_bot.services.NotificationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

import static com.braidsencurls.event_bot.DateUtil.DATE_TIME_24_HOUR;
import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class EventSavedState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSavedState.class);
    private static final EventRepository eventRepository = new EventRepositoryImpl();
    private static final NotificationService notificationService = new NotificationServiceImpl();

    @Override
    public String getKey() {
        return "EVENT_SAVED";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will process EVENT_SAVED");
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        String username = update.getMessage().getChat().getUserName();

        try {
            LocalDateTime dateTime = DateUtil.parseDateTime(message, DATE_TIME_24_HOUR);
            SharedData.getInstance().getTempEvents().get(chatId).setDateTime(dateTime);
            setNextState(chatId, null);

            Event completedEvent = SharedData.getInstance().getTempEvents().get(chatId);
            completedEvent.setStatus("ACTIVE");
            LOGGER.info("Event Id: " + completedEvent.getId());

            //Save Event to Database
            eventRepository.save(completedEvent);
            //Remove Temporary Event
            SharedData.getInstance().getTempEvents().remove(chatId);

            new Thread(()-> {
                notificationService.notifyEventsSubscribers(EventAction.SAVED, completedEvent, username);
            }).start();

            return generate(chatId, "Alright! Your event is successfully saved!\n"
                    + completedEvent);
        } catch (Exception e) {
            LOGGER.error("Fail to save event", e);
            return  generate(chatId, "Sorry can you enter that again." +
                    " Use format yyyy-mm-dd HH:mm (24-hour). For example 2023-12-30 16:00");
        }
    }
}
