package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.repositories.EventSubscribersRepository;
import com.braidsencurls.event_bot.repositories.EventSubscribersRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SubscribeToNewEventsCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscribeToNewEventsCommand.class);

    private static final EventSubscribersRepository eventSubscribersRepository = new EventSubscribersRepositoryImpl();

    @Override
    public String getTextCommand() {
        return "/subscribetoevents";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Subscribe to New Events Command is Triggered");
        Long chatId = update.getMessage().getChatId();

        eventSubscribersRepository.save(chatId);
        String responseText = "You will now be receiving notification everytime a new event is scheduled";
        return generateSendMessage(chatId, responseText);
    }
}
