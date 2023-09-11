package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
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

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("List All Events Command is Triggered");
        String formattedEvents = SharedData.getInstance().getAllFormattedEvents().toString();
        return generateSendMessage(update.getMessage().getChatId(), formattedEvents);
    }
}
