package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class SetEventDateTimeState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetEventDateTimeState.class);

    @Override
    public String getKey() {
        return "SET_EVENT_DATE_TIME";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will process SET_EVENT_DATE_TIME");
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        SharedData.getInstance().getTempEvents().get(chatId).setLocation(messageText);
        setNextState(chatId, "EVENT_SAVED");
        return generate(chatId, "And when is it going to happen? " +
                "Enter date in yyyy-mm-dd HH:mm (24-hour) format For example 2023-12-30 16:00?");
    }
}
