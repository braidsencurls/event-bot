package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.repositories.EventSubscribersRepository;
import com.braidsencurls.event_bot.repositories.EventSubscribersRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnSubscribeToNewEventsCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnSubscribeToNewEventsCommand.class);

    private static final EventSubscribersRepository eventSubscribersRepository = new EventSubscribersRepositoryImpl();

    @Override
    public String getTextCommand() {
        return "/unsubscribetoevents";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("UnSubscribe to New Events Command is Triggered");
        Long chatId = update.getMessage().getChatId();

        boolean isSubscribed = eventSubscribersRepository.delete(chatId);
        String responseText = "You are not subscribed at all";
        if(isSubscribed) {
            responseText = "You will no longer receive notifications everytime a new event is scheduled";
        }

        return generateSendMessage(chatId, responseText);
    }
}
