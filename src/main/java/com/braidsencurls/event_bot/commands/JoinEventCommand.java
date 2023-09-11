package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class JoinEventCommand implements  Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinEventCommand.class);

    @Override
    public String getTextCommand() {
        return "/joinevent";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Join Event Command is Triggered");
        Long chatId = update.getMessage().getChatId();

        String responseText = "No active events at the moment";
        if(CollectionUtils.isNotEmpty(SharedData.getInstance().getActiveEvents())) {
            responseText = "Choose the Event Id of the event you want to participate in\n" +
                    SharedData.getInstance().getAllFormattedEvents();
            setNextState(chatId, "JOIN_EVENT");
        }

        SendMessage sendMessage = generateSendMessage(chatId, responseText);
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup());
        return sendMessage;
    }
}
