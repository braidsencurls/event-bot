package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class DescribeEventState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(DescribeEventState.class);

    @Override
    public String getKey() {
        return "DESCRIBE_EVENT";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will process DESCRIBE_EVENT state");
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        SharedData.getInstance().getTempEvents().get(chatId).setName(messageText);
        setNextState(chatId, "SET_EVENT_LOCATION");
        return generate(chatId, "Okay! Tell us some description for this event");
    }
}
