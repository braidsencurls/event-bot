package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.DateUtil;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.EventAction;
import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.entities.User;
import com.braidsencurls.event_bot.repositories.*;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.time.LocalDateTime;
import java.util.Set;

import static com.braidsencurls.event_bot.DateUtil.DATE_TIME_24_HOUR;
import static com.braidsencurls.event_bot.TelegramFactory.sender;
import static java.lang.System.getenv;

public class ResponseCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseCommand.class);
    private static final AbsSender SENDER = sender(getenv("bot_token"), getenv("bot_username"));
    private static final EventRepository eventRepository = new EventRepositoryImpl();
    private static final EventSubscribersRepository eventSubscriberRepository = new EventSubscribersRepositoryImpl();
    private static final UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public String getTextCommand() {
        return null;
    }

    @Override
    public boolean isUserAuthorized(String username) {
        return true;
    }

    @Override
    public SendMessage execute(Update update) {
        LOGGER.info("Response Command is Triggered");
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getChat().getUserName();
        String messageText = update.getMessage().getText();
        String currentState = SharedData.getInstance().getState().get(chatId);
        currentState = currentState != null ? currentState : "INIT";
        switch (currentState) {
            case "DESCRIBE_EVENT": {
                LOGGER.info("Will process DESCRIBE_EVENT");
                SharedData.getInstance().getTempEvents().get(chatId).setName(messageText);
                setNextState(chatId, "SET_EVENT_LOCATION");
                return generateSendMessage(chatId, "Okay! Tell us some description for this event");
            }
            case "SET_EVENT_LOCATION": {
                LOGGER.info("Will process SET_EVENT_LOCATION");
                SharedData.getInstance().getTempEvents().get(chatId).setDescription(messageText);
                setNextState(chatId, "SET_EVENT_DATE_TIME");
                return generateSendMessage(chatId, "Cool! Where is this event going to happen?");
            }
            case "SET_EVENT_DATE_TIME": {
                LOGGER.info("Will process SET_EVENT_DATE_TIME");
                SharedData.getInstance().getTempEvents().get(chatId).setLocation(messageText);
                setNextState(chatId, "EVENT_CREATED");
                return generateSendMessage(chatId, "And when is it going to happen? " +
                        "Enter date in yyyy-mm-dd HH:mm (24-hour) format For example 2023-12-30 16:00?");
            }
            case "EVENT_CREATED": {
                LOGGER.info("Will process EVENT_CREATED");
                try {
                    LocalDateTime dateTime = DateUtil.parseDateTime(messageText, DATE_TIME_24_HOUR);
                    SharedData.getInstance().getTempEvents().get(chatId).setDateTime(dateTime);
                    setNextState(chatId, null);

                    Event completedEvent = SharedData.getInstance().getTempEvents().get(chatId);
                    completedEvent.setStatus("ACTIVE");
                    LOGGER.info("Event Id: " + completedEvent.getId());

                    //Save Event to Database
                    eventRepository.save(completedEvent);
                    //Remove Temporary Event
                    SharedData.getInstance().getTempEvents().remove(chatId);

                    new Thread(()-> {
                        notifyEventsSubscribers(EventAction.CREATED, completedEvent, username);
                    }).start();

                    return generateSendMessage(chatId, "Alright! Your event is successfully created!\n"
                            + completedEvent);
                } catch (Exception e) {
                    LOGGER.error("Fail to save event", e);
                    return  generateSendMessage(chatId, "Sorry can you enter that again." +
                            " Use format yyyy-mm-dd HH:mm (24-hour). For example 2023-12-30 16:00");
                }
            }
            case "JOIN_EVENT": {
                LOGGER.info("Will process JOIN_EVENT");
                Event selectedEvent = eventRepository.findById(messageText);
                if(selectedEvent == null) {
                    return generateSendMessage(chatId, "Sorry! I can't find that event. Can you try again? Enter the Event id you want to participate in\n"
                            + SharedData.getInstance().getAllFormattedEvents());
                } else {
                    selectedEvent.addAttendees("@" + update.getMessage().getChat().getUserName());
                    eventRepository.save(selectedEvent);
                    setNextState(chatId, null);
                    return generateSendMessage(chatId, "You've been registered to the event! Have fun!");
                }
            }
            case "QUIT_EVENT": {
                LOGGER.info("Will process QUIT_EVENT");
                Event selectedEvent = eventRepository.findById(messageText);
                if(selectedEvent == null) {
                    return generateSendMessage(chatId, "Sorry! I can't find that event. Can you try again? Enter the Event id you want to quit from\n"
                            + SharedData.getInstance().getAllFormattedEvents());
                } else {
                    boolean removedUser = selectedEvent.getAttendees().remove("@" + update.getMessage().getChat().getUserName());
                    SendMessage sendMessage = generateSendMessage(chatId, "You are currently not registered to this event");
                    if(removedUser) {
                        eventRepository.save(selectedEvent);
                        sendMessage = generateSendMessage(chatId, "You've successfully opted out from this event. You know what to do if you changed your mind!");
                    }
                    setNextState(chatId, null);
                    return sendMessage;
                }
            }
            case "CANCEL_EVENT": {
                LOGGER.info("Will process CANCEL_EVENT");
                Event selectedEvent = eventRepository.findById(messageText);
                if(selectedEvent == null) {
                    return generateSendMessage(chatId, "Sorry! I can't find that event. Can you try again? Enter the Event id you want to participate in\n"
                            + SharedData.getInstance().getAllFormattedEvents());
                } else {
                    LOGGER.info("Event found and will be deleted");
                    eventRepository.delete(messageText);
                    setNextState(chatId, null);

                    new Thread(()-> {
                        notifyEventsSubscribers(EventAction.CANCELLED, selectedEvent, username);
                    }).start();

                    return generateSendMessage(chatId, "Event has been successfully cancelled");
                }
            }
            case "LIST_ATTENDEES": {
                LOGGER.info("Will process LIST_ATTENDEES");
                Event selectedEvent = eventRepository.findById(messageText);
                if(selectedEvent == null) {
                    return generateSendMessage(chatId, "Sorry! I can't find that event. Can you try again? Enter the Event id you want to participate in\n"
                            + SharedData.getInstance().getAllFormattedEvents());
                } else {
                    LOGGER.info("Event found and listing all attendees");
                    StringBuilder attendees = new StringBuilder();
                    if(selectedEvent.getAttendees().isEmpty()) {
                        return generateSendMessage(chatId, "No participants for this event yet!");
                    } else {
                        selectedEvent.getAttendees().forEach(k-> {
                            attendees.append(k).append("\n");
                        });
                        setNextState(chatId, null);
                        return generateSendMessage(chatId, "These guys will attend the event\n"
                                + String.join("\n", selectedEvent.getAttendees()));
                    }
                }
            }
            case "GRANT_USER_ACCESS": {
                LOGGER.info("Will Process GRANT_USER_ACCESS");
                String[] textArr = messageText.split(" - ");
                if(textArr.length != 2) {
                    return generateSendMessage(chatId, "I'm sorry can you repeat that again." +
                            "Follow the format <username> - <ADMIN/MEMBER>. For example, my_telegram_user - MEMBER");
                } else {
                    userService.addUser(textArr[0], textArr[1]);
                    setNextState(chatId, null);
                    return generateSendMessage(chatId, textArr[0] + " has been successfully granted an access ");
                }
            }
            case "REVOKE_USER_ACCESS": {
                LOGGER.info("Will Process REVOKE_USER_ACCESS");
                if(userService.deleteUser(messageText)) {
                    return generateSendMessage(chatId, messageText + " has been revoked an access");
                }
                return generateSendMessage(chatId, "Something went wrong! Make sure that "
                        + messageText + " has been granted an access before." +
                        "Try again. Enter the username you want to revoke an access");
            }
            default: {
                LOGGER.info("Unable to process response");
                return generateSendMessage(chatId, "Sorry! I don't understand that command!");
            }
        }
    }

    private void notifyEventsSubscribers(EventAction action, Event event, String actor) {
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
