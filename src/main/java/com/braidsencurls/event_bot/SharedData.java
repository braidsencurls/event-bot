package com.braidsencurls.event_bot;

import com.braidsencurls.event_bot.commands.*;
import com.braidsencurls.event_bot.repositories.EventRepository;
import com.braidsencurls.event_bot.repositories.EventRepositoryImpl;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SharedData {
    private static SharedData instance = null;
    private Map<Long, String> state;
    private Map<Long, Event> pendingEvents;
    private Map<String, Command> commandRegistry;
    private static final Logger LOGGER = LoggerFactory.getLogger(SharedData.class);
    private static final EventRepository eventRepository = new EventRepositoryImpl();

    private SharedData() {
        System.out.println("Instantiating.....");
        this.state = new HashMap<>();
        this.pendingEvents = new ConcurrentHashMap<>();
        this.commandRegistry = new HashMap<>();

        //populateCommandRegistry();
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

    public Map<Long, Event> getPendingEvents() {
        return pendingEvents;
    }

    public Map<String, Command> getCommandRegistry() {
        //return this.commandRegistry;
        commandRegistry.put("/start", new StartCommand());
        commandRegistry.put("/createevent", new CreateEventCommand());
        commandRegistry.put("/cancelevent", new CancelEventCommand());
        commandRegistry.put("/listevents", new ListEventsCommand());
        commandRegistry.put("/listattendees", new ListEventAttendeesCommand());
        commandRegistry.put("/joinevent", new JoinEventCommand());
        commandRegistry.put("/quitevent", new QuitEventCommand());
        commandRegistry.put("/subscribetoevents", new SubscribeToNewEventsCommand());
        commandRegistry.put("/unsubscribetoevents", new UnSubscribeToNewEventsCommand());
        return commandRegistry;
    }

    private void populateCommandRegistry() {
        long start = System.currentTimeMillis();
        try {
            String packageName = "com.braidsencurls.event_bot.commands";
            Reflections reflections = new Reflections(packageName);

            Set<Class<? extends Command>> implementations = reflections.getSubTypesOf(Command.class);

            for (Class<? extends Command> implementation : implementations) {
                Command command = implementation.newInstance();
                String textCommand = command.getTextCommand();
                commandRegistry.put(textCommand, command);
            }
            LOGGER.info("Scanning took: " + (System.currentTimeMillis() - start) + " ms");
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to populate Command Registry");
        }
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

