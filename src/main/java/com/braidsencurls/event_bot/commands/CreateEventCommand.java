package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.Event;
import com.braidsencurls.event_bot.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class CreateEventCommand implements Command {

    private static Logger LOGGER = LoggerFactory.getLogger(CreateEventCommand.class);

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
        setNextState(chatId, "DESCRIBE_EVENT");
        return generateSendMessage(chatId,"What is the event name?");
    }

    private static void addEvent(Long chatId, String username) {
        Event newEvent = new Event();
        String eventId = UUID.randomUUID().toString();
        newEvent.setId(eventId);
        newEvent.setOrganizer("@" + username);
        SharedData.getInstance().getPendingEvents().put(chatId, newEvent);
    }
}
