package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public interface Command {
    SendMessage execute(Update update);
    String getTextCommand();
    boolean isUserAuthorized(String username);
    default void initState(Long chatId, String nextState) {
        SharedData.getInstance().getState().put(chatId, nextState);
    }
}
