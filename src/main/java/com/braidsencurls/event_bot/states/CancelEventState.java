package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.EventAction;
import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.repositories.EventRepository;
import com.braidsencurls.event_bot.repositories.EventRepositoryImpl;
import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.NotificationService;
import com.braidsencurls.event_bot.services.NotificationServiceImpl;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class CancelEventState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(CancelEventState.class);
    private static final EventRepository eventRepository = new EventRepositoryImpl();
    private static final UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
    private static final NotificationService notificationService = new NotificationServiceImpl();

    @Override
    public String getKey() {
        return "CANCEL_EVENT";
    }

    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will process CANCEL_EVENT");
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        String username = update.getMessage().getChat().getUserName();

        Event selectedEvent = eventRepository.findById(message);
        if(selectedEvent == null) {
            return generate(chatId, "Sorry! I can't find that event. Can you try again? Enter the Event id you want to participate in\n"
                    + SharedData.getInstance().getAllFormattedEvents());
        } else {
            if(username.equalsIgnoreCase(selectedEvent.getOrganizer()) || userService.isAdmin(username)) {
                eventRepository.delete(message);
                new Thread(()-> {
                    notificationService.notifyEventsSubscribers(EventAction.CANCELLED, selectedEvent, username);
                }).start();
                setNextState(chatId, null);
                return generate(chatId, "Event has been successfully cancelled");
            } else {
                setNextState(chatId, null);
                return generate(chatId, "Sorry but you're not allowed to cancel this event. " +
                        "Please contact the organizer of the event or an admin");
            }
        }
    }
}
