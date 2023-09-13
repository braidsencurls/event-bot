package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.exceptions.NoUserFoundException;
import com.braidsencurls.event_bot.exceptions.UnauthorizedUserException;
import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

import static com.braidsencurls.event_bot.SendMessageGenerator.createReplyKeyboardMarkup;
import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class UpdateEventCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateEventCommand.class);
    private static final UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public String getTextCommand() {
        return "/updateevent";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Update Event Command is Triggered");
        Long chatId = update.getMessage().getChatId();
        initState(chatId, "UPDATE_EVENT");

        return getSendMessage(chatId);
    }

    private static SendMessage getSendMessage(Long chatId) {
        SendMessage sendMessage = generate(chatId,"Select the Event Id of the event you would like to update.\n" +
                SharedData.getInstance().getAllFormattedEvents());
        List<String> eventIds = SharedData.getInstance().getActiveEvents()
                .stream().map(Event::getId).collect(Collectors.toList());
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup(eventIds));
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
