package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.exceptions.NoUserFoundException;
import com.braidsencurls.event_bot.exceptions.UnauthorizedUserException;
import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

import static com.braidsencurls.event_bot.SendMessageGenerator.createReplyKeyboardMarkup;
import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class ListEventAttendeesCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListEventAttendeesCommand.class);
    private static final UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public String getTextCommand() {
        return "/listattendees";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("List All Attendees Command is Triggered");
        Long chatId = update.getMessage().getChatId();

        String responseText = "No active events at the moment";
        if(CollectionUtils.isNotEmpty(SharedData.getInstance().getActiveEvents())) {
            responseText = "Choose the Event Id of the event you want to check attendees\n" +
                    SharedData.getInstance().getAllFormattedEvents();
            SendMessage sendMessage = getSendMessage(chatId, responseText);
            initState(chatId, "LIST_ATTENDEES");
            return sendMessage;
        }

        return generate(chatId, responseText);
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

    private SendMessage getSendMessage(Long chatId, String responseText) {
        SendMessage sendMessage = generate(chatId, responseText);
        List<String> eventIds = SharedData.getInstance().getActiveEvents()
                .stream().map(Event::getId).collect(Collectors.toList());
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup(eventIds));
        return sendMessage;
    }
}
