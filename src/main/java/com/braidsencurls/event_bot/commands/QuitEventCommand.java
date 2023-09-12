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

public class QuitEventCommand implements  Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuitEventCommand.class);
    private UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public String getTextCommand() {
        return "/quitevent";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Quit Event Command is Triggered");
        Long chatId = update.getMessage().getChatId();
        setNextState(chatId, "QUIT_EVENT");
        String responseText = "Choose the Event Id of the event you want to quit from\n" +
                SharedData.getInstance().getAllFormattedEvents();
        SendMessage sendMessage = generateSendMessage(chatId, responseText);
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup());
        return sendMessage;
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
