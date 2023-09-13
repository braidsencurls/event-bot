package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.exceptions.NoUserFoundException;
import com.braidsencurls.event_bot.exceptions.UnauthorizedUserException;
import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class CreateEventCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateEventCommand.class);
    private static final UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public String getTextCommand() {
        return "/createevent";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Create Event Command is Triggered");
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getChat().getUserName();
        addEvent(chatId, username);
        initState(chatId, "DESCRIBE_EVENT");
        return generate(chatId,"What is the event name?");
    }

    @Override
    public boolean isUserAuthorized(String username) {
        try {
            userService.findByUsername(username);
            return true;
        } catch (NoUserFoundException e) {
            throw new UnauthorizedUserException("User is not authorized to perform this command");
        }
    }

    private static void addEvent(Long chatId, String username) {
        Event newEvent = new Event();
        String eventId = UUID.randomUUID().toString();
        newEvent.setId(eventId);
        newEvent.setOrganizer("@" + username);
        saveTemporaryEvent(chatId, newEvent);
    }

    private static Event saveTemporaryEvent(Long chatId, Event newEvent) {
        return SharedData.getInstance().getTempEvents().put(chatId, newEvent);
    }
}
