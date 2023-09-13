package com.braidsencurls.event_bot.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class UnknownState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnknownState.class);

    @Override
    public String getKey() {
        return "UNKNOWN";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Unable to process response");
        Long chatId = update.getMessage().getChatId();
        return generate(chatId, "Sorry! I don't understand that command!");
    }
}
