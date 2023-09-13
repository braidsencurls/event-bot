package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class SetEventLocationState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetEventLocationState.class);

    @Override
    public String getKey() {
        return "SET_EVENT_LOCATION";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will process SET_EVENT_LOCATION state");
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        LOGGER.info("Will process SET_EVENT_LOCATION");
        SharedData.getInstance().getTempEvents().get(chatId).setDescription(messageText);
        setNextState(chatId, "SET_EVENT_DATE_TIME");
        return generate(chatId, "Cool! Where is this event going to happen?");
    }
}
