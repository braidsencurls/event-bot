package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.states.State;
import com.braidsencurls.event_bot.states.StateRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ResponseCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseCommand.class);

    @Override
    public String getTextCommand() {
        return null;
    }

    @Override
    public boolean isUserAuthorized(String username) {
        return true;
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Response Command is Triggered");
        Long chatId = update.getMessage().getChatId();
        State state = StateRegistry.getInstance().getStates().get(getCurrentState(chatId));
        return state.process(update);
    }

    private static String getCurrentState(Long chatId) {
        String currentState = SharedData.getInstance().getState().get(chatId);
        currentState = currentState != null ? currentState : "UNKNOWN";
        return currentState;
    }
}
