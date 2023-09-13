package com.braidsencurls.event_bot;

import com.braidsencurls.event_bot.commands.*;
import com.braidsencurls.event_bot.entities.Event;
import com.braidsencurls.event_bot.entities.User;
import com.braidsencurls.event_bot.repositories.EventRepository;
import com.braidsencurls.event_bot.repositories.EventRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SharedData {
    public static final String COMMANDS_PACKAGE = "com.braidsencurls.event_bot.commands";
    private static SharedData instance = null;
    private Map<Long, String> state;
    private Map<Long, Event> tempEvents;
    private Map<String, Command> commandRegistry;
    private List<User> authorizedUsers;
    private static final Logger LOGGER = LoggerFactory.getLogger(SharedData.class);
    private static final EventRepository eventRepository = new EventRepositoryImpl();

    private SharedData() {
        LOGGER.debug("Instantiating SharedData");
        this.state = new HashMap<>();
        this.tempEvents = new ConcurrentHashMap<>();
        this.commandRegistry = new HashMap<>();
        this.authorizedUsers = new ArrayList<>();

        registerCommands();
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public Map<Long, String> getState() {
        return state;
    }

    public Map<Long, Event> getTempEvents() {
        return tempEvents;
    }

    public Map<String, Command> getCommandRegistry() {
        return commandRegistry;
    }

    public List<User> getAuthorizedUsers() {
        return authorizedUsers;
    }

    private void registerCommands() {
        //TODO: Investigate why it take some time to scan
        /*long start = System.currentTimeMillis();
        try {
            Reflections reflections = new Reflections(COMMANDS_PACKAGE);

            Set<Class<? extends Command>> implementations = reflections.getSubTypesOf(Command.class);

            for (Class<? extends Command> implementation : implementations) {
                Command command = implementation.newInstance();
                String textCommand = command.getTextCommand();
                commandRegistry.put(textCommand, command);
            }
            LOGGER.info("Scanning took: " + (System.currentTimeMillis() - start) + " ms");
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to populate Command Registry");
        }*/
        commandRegistry.put("/start", new StartCommand());
        commandRegistry.put("/createevent", new CreateEventCommand());
        commandRegistry.put("/cancelevent", new CancelEventCommand());
        commandRegistry.put("/listevents", new ListEventsCommand());
        commandRegistry.put("/listattendees", new ListEventAttendeesCommand());
        commandRegistry.put("/joinevent", new JoinEventCommand());
        commandRegistry.put("/quitevent", new QuitEventCommand());
        commandRegistry.put("/subscribetoevents", new SubscribeToNewEventsCommand());
        commandRegistry.put("/unsubscribetoevents", new UnSubscribeToNewEventsCommand());
        commandRegistry.put("/updateevent", new UpdateEventCommand());
        commandRegistry.put("/grantuseraccess", new GrantUserAccessCommand());
        commandRegistry.put("/revokeuseraccess", new RevokeUserAccessCommand());
    }

    public List<Event> getActiveEvents() {
        return eventRepository.findByStatus("ACTIVE");
    }

    public StringBuilder getAllFormattedEvents() {
        StringBuilder formattedAllEvents = new StringBuilder();
        List<Event> activeEvents = eventRepository.findByStatus("ACTIVE");
        if(activeEvents.isEmpty()) {
            return formattedAllEvents.append("No active events at the moment");
        }
        activeEvents.forEach(event -> {
            formattedAllEvents
                    .append("Event Details:")
                    .append("\n")
                    .append(event)
                    .append("\n\n");
        });
        return formattedAllEvents;
    }
}

