package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.SharedData;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public interface Command {
    SendMessage execute(Update update);
    String getTextCommand();
    boolean isUserAuthorized(String username);

    default SendMessage generateSendMessage(Long chatId, String responseText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(responseText);

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);

        sendMessage.setReplyMarkup(replyKeyboardRemove);
        return sendMessage;
    }

    default ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        List<Event> activeEvents = SharedData.getInstance().getActiveEvents();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        activeEvents.forEach((event) -> {
            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(event.getId());
            keyboardRow.add(keyboardButton);
            keyboardRowList.add(keyboardRow);

        });
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;

    }
    default void setNextState(Long chatId, String nextState) {
        SharedData.getInstance().getState().put(chatId, nextState);
    }
}
