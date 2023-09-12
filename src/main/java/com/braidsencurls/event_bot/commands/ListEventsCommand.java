package com.braidsencurls.event_bot.commands;

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

public class ListEventsCommand implements Command {

    @Override
    public String getTextCommand() {
        return "/listevents";
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(ListEventsCommand.class);
    private UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("List All Events Command is Triggered");
        String formattedEvents = SharedData.getInstance().getAllFormattedEvents().toString();
        return generateSendMessage(update.getMessage().getChatId(), formattedEvents);
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
}
