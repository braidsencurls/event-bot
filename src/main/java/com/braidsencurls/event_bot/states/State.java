package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.SharedData;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface State {
    SendMessage process(Update update);
    String getKey();
    default void setNextState(Long chatId, String nextState) {
        SharedData.getInstance().getState().put(chatId, nextState);
    }
}
