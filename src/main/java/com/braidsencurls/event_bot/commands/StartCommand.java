package com.braidsencurls.event_bot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

    @Override
    public String getTextCommand() {
        return "/start";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Start Command is Triggered");
        return generateSendMessage(update.getMessage().getChatId(), "Hello There!");
    }
}
