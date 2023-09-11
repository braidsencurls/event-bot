package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CancelEventCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(CancelEventCommand.class);

    @Override
    public String getTextCommand() {
        return "/cancelevent";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Cancel Event Command is Triggered");
        Long chatId = update.getMessage().getChatId();

        String responseText = "No active events at the moment";
        if(CollectionUtils.isNotEmpty(SharedData.getInstance().getActiveEvents())) {
            responseText = "Choose the Event Id of the event you want to cancel\n" +
                    SharedData.getInstance().getAllFormattedEvents();
            setNextState(chatId, "CANCEL_EVENT");
        }

        SendMessage sendMessage = generateSendMessage(chatId, responseText);
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup());
        return sendMessage;
    }
}
