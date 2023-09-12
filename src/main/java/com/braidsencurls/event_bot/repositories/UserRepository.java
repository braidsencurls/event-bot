package com.braidsencurls.event_bot.repositories;

import com.braidsencurls.event_bot.entities.User;

public interface UserRepository {
    void save(User user);
    boolean deleteUser(String id);
    User findByUsername(String username);
}
