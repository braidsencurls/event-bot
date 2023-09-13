package com.braidsencurls.event_bot.services;

import com.braidsencurls.event_bot.SharedData;
import com.braidsencurls.event_bot.entities.Role;
import com.braidsencurls.event_bot.entities.User;
import com.braidsencurls.event_bot.exceptions.NoUserFoundException;
import com.braidsencurls.event_bot.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private static UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User findByUsername(String username) {
        //Check cache first
        User user = getUserFromCache(username);
        user = getUserFromDatabase(username, user);
        return user;

    }

    private User getUserFromDatabase(String username, User user) {
        if (user == null) {
            LOGGER.info("Trying to find user from the database");
            user = userRepository.findByUsername(username);
            if (user != null) {
                SharedData.getInstance().getAuthorizedUsers().add(user);
            }
        }
        return user;
    }

    private static User getUserFromCache(String username) {
        List<User> cachedAuthorizedUsers = SharedData.getInstance().getAuthorizedUsers();
        Optional<User> user = cachedAuthorizedUsers.stream().filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst();
        if(user.isPresent()) {
            LOGGER.info("Cache hit! User has been found");
            return user.get();
        }
        return null;
    }

    @Override
    public void addUser(String username, String role) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setRole(Role.valueOf(role));

        userRepository.save(user);
        SharedData.getInstance().getAuthorizedUsers().add(user);
    }
    @Override
    public boolean deleteUser(String username) {
        try {
            User user = findByUsername(username);
            userRepository.deleteUser(user.getId());
            SharedData.getInstance().getAuthorizedUsers().remove(user);
            return true;
        } catch (NoUserFoundException e) {
            LOGGER.error("No user found with username " + username);
            return false;
        }
    }

    @Override
    public boolean isAdmin(String username) {
        User user = findByUsername(username);
        if(Role.ADMIN.equals(user.getRole())) {
            return true;
        }
        return false;
    }
}
