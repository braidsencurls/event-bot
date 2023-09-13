package com.braidsencurls.event_bot.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StateRegistry {
    private Map<String, State> states;
    private static StateRegistry INSTANCE = null;
    public static final String STATES_PACKAGE = "com.braidsencurls.event_bot.states";
    private static final Logger LOGGER = LoggerFactory.getLogger(StateRegistry.class);

    private StateRegistry() {
        this.states = new HashMap<>();
        registerStates();
    }

    public static StateRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StateRegistry();
        }
        return INSTANCE;
    }

    public Map<String, State> getStates() {
        return states;
    }

    private void registerStates() {
        //TODO: Investigate why it take some time to scan
        /*long start = System.currentTimeMillis();
        try {
            Reflections reflections = new Reflections(STATES_PACKAGE);

            Set<Class<? extends State>> implementations = reflections.getSubTypesOf(State.class);

            for (Class<? extends State> implementation : implementations) {
                State state = implementation.newInstance();
                String key = state.getKey();
                states.put(key, state);
            }
            LOGGER.info("Scanning took: " + (System.currentTimeMillis() - start) + " ms");
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to populate State Registry");
        }*/
        states.put("CANCEL_EVENT", new CancelEventState());
        states.put("DESCRIBE_EVENT", new DescribeEventState());
        states.put("EVENT_SAVED", new EventSavedState());
        states.put("GRANT_USER_ACCESS", new GrantUserAccessState());
        states.put("JOIN_EVENT", new JoinEventState());
        states.put("LIST_ATTENDEES", new ListAttendeesState());
        states.put("QUIT_EVENT", new QuitEventState());
        states.put("REVOKE_USER_ACCESS", new RevokeUserAccessState());
        states.put("SET_EVENT_DATE_TIME", new SetEventDateTimeState());
        states.put("SET_EVENT_LOCATION", new SetEventLocationState());
        states.put("UNKNOWN", new UnknownState());
        states.put("UPDATE_EVENT", new UpdateEventState());
    }
}
