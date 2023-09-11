package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.SharedData;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ListEventAttendeesCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListEventAttendeesCommand.class);

    @Override
    public String getTextCommand() {
        return "/listattendees";
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("List All Attendees Command is Triggered");
        Long chatId = update.getMessage().getChatId();

        String responseText = "No active events at the moment";
        if(CollectionUtils.isNotEmpty(SharedData.getInstance().getActiveEvents())) {
            responseText = "Choose the Event Id of the event you want to check attendees\n" +
                    SharedData.getInstance().getAllFormattedEvents();
            SendMessage sendMessage = getSendMessage(chatId, responseText);
            setNextState(chatId, "LIST_ATTENDEES");
            return sendMessage;
        }

        return generateSendMessage(chatId, responseText);
    }

    private SendMessage getSendMessage(Long chatId, String responseText) {
        SendMessage sendMessage = generateSendMessage(chatId, responseText);
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup());
        return sendMessage;
    }
}
