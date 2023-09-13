package com.braidsencurls.event_bot.services;

import com.braidsencurls.event_bot.EventAction;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.repositories.EventSubscribersRepository;
import com.braidsencurls.event_bot.repositories.EventSubscribersRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Set;

import static com.braidsencurls.event_bot.TelegramFactory.sender;
import static java.lang.System.getenv;

public class NotificationServiceImpl implements NotificationService {
    private static final EventSubscribersRepository eventSubscriberRepository =
            new EventSubscribersRepositoryImpl();
    private static final AbsSender SENDER = sender(getenv("bot_token"), getenv("bot_username"));
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    public void notifyEventsSubscribers(EventAction action, Event event, String actor) {
        Set<Long> subscribers = eventSubscriberRepository.findAll();
        subscribers.forEach(subscriber -> {
            SendMessage message = new SendMessage();
            message.setChatId(subscriber);
            message.setText("An event has been " + action + " by @" + actor + "\n" + event.toString());

            try {
                SENDER.execute(message);
                LOGGER.info("Message sent to chat ID: " + subscriber);
            } catch (Exception e) {
                LOGGER.error("Failed to send message to chat ID: " + subscriber, e);
            }
        });
    }
}
