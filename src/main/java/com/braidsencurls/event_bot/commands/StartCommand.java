package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class StartCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

    @Override
    public String getTextCommand() {
        return "/start";
    }

    @Override
    public boolean isUserAuthorized(String username) {
        return true;
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Start Command is Triggered");
        Long chatId = update.getMessage().getChatId();
        clearTempEvent(chatId);
        clearState(chatId);

        return generate(update.getMessage().getChatId(), "Hello There!");
    }

    private static void clearState(Long chatId) {
        if(SharedData.getInstance().getState().get(chatId) != null) {
            SharedData.getInstance().getState().put(chatId, null);
        }
    }

    private static void clearTempEvent(Long chatId) {
        if(SharedData.getInstance().getTempEvents().get(chatId) != null) {
            SharedData.getInstance().getTempEvents().put(chatId, null);
        }
    }
}
